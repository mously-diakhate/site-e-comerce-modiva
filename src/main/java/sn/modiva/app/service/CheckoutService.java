package sn.modiva.app.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sn.modiva.app.domain.ClienteProfile;
import sn.modiva.app.domain.Commande;
import sn.modiva.app.domain.LigneCommande;
import sn.modiva.app.domain.Livraison;
import sn.modiva.app.domain.Paiement;
import sn.modiva.app.domain.VarianteProduit;
import sn.modiva.app.domain.enumeration.StatutCommande;
import sn.modiva.app.domain.enumeration.StatutLivraison;
import sn.modiva.app.domain.enumeration.StatutPaiement;
import sn.modiva.app.repository.ClienteProfileRepository;
import sn.modiva.app.repository.CommandeRepository;
import sn.modiva.app.repository.LigneCommandeRepository;
import sn.modiva.app.repository.LivraisonRepository;
import sn.modiva.app.repository.PaiementRepository;
import sn.modiva.app.repository.VarianteProduitRepository;

/**
 * Passage de commande — visiteur OU cliente connectée.
 *
 * Tout ce qui touche à l'argent et au stock est décidé ICI, jamais par le navigateur :
 *  - les prix viennent de Produit.prix en base (le client n'envoie que varianteId + quantité) ;
 *  - le stock est déduit atomiquement (UPDATE ... WHERE stock >= qte) ;
 *  - le paiement est toujours créé EN_ATTENTE, la commande EN_ATTENTE ;
 *  - tout est dans UNE transaction : si une étape échoue, rien n'est enregistré
 *    et le stock déduit est restitué.
 */
@Service
public class CheckoutService {

    private static final Logger LOG = LoggerFactory.getLogger(CheckoutService.class);

    private static final Set<String> MODES_PAIEMENT = Set.of("WAVE", "ORANGE_MONEY");
    private static final int ADRESSE_MAX = 255;

    private final VarianteProduitRepository varianteProduitRepository;
    private final ClienteProfileRepository clienteProfileRepository;
    private final LivraisonRepository livraisonRepository;
    private final PaiementRepository paiementRepository;
    private final CommandeRepository commandeRepository;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final MonPanierService monPanierService;

    public CheckoutService(
        VarianteProduitRepository varianteProduitRepository,
        ClienteProfileRepository clienteProfileRepository,
        LivraisonRepository livraisonRepository,
        PaiementRepository paiementRepository,
        CommandeRepository commandeRepository,
        LigneCommandeRepository ligneCommandeRepository,
        MonPanierService monPanierService
    ) {
        this.varianteProduitRepository = varianteProduitRepository;
        this.clienteProfileRepository = clienteProfileRepository;
        this.livraisonRepository = livraisonRepository;
        this.paiementRepository = paiementRepository;
        this.commandeRepository = commandeRepository;
        this.ligneCommandeRepository = ligneCommandeRepository;
        this.monPanierService = monPanierService;
    }

    public record LigneRequest(@NotNull Long varianteId, @Min(1) @Max(MonPanierService.QUANTITE_MAX) int quantite) {}

    public record CheckoutRequest(
        @NotBlank @Size(max = 100) String nomComplet,
        @NotBlank @Size(max = 30) String telephone,
        @NotBlank @Size(max = 60) String pays,
        @NotBlank @Size(max = 60) String region,
        @NotBlank @Size(max = 100) String quartier,
        @Size(max = 500) String message,
        @NotBlank String modePaiement,
        @NotEmpty @Valid List<LigneRequest> lignes
    ) {}

    public record CheckoutResult(Long commandeId, String numero, BigDecimal montantTotal) {}

    /**
     * @param login login de la cliente connectée, ou vide pour une commande invité.
     */
    @Transactional
    public CheckoutResult passerCommande(CheckoutRequest req, Optional<String> login) {
        String mode = req.modePaiement().trim().toUpperCase();
        if (!MODES_PAIEMENT.contains(mode)) {
            throw bad("Mode de paiement invalide.");
        }

        // 1. Fusionner les doublons éventuels (même variante envoyée deux fois).
        Map<Long, Integer> quantites = new LinkedHashMap<>();
        for (LigneRequest l : req.lignes()) {
            quantites.merge(l.varianteId(), l.quantite(), Integer::sum);
        }
        if (
            quantites
                .values()
                .stream()
                .anyMatch(q -> q > MonPanierService.QUANTITE_MAX)
        ) {
            throw bad("Quantité trop élevée pour un article.");
        }

        // 2. Charger les variantes RÉELLES (le prix vient d'ici, pas du navigateur).
        List<VarianteProduit> variantes = varianteProduitRepository.findAllById(quantites.keySet());
        if (variantes.size() != quantites.size()) {
            throw bad("Un article de votre panier n'est plus disponible.");
        }

        // 3. Déduire le stock (atomique) et calculer le total.
        BigDecimal total = BigDecimal.ZERO;
        for (VarianteProduit v : variantes) {
            int qte = quantites.get(v.getId());
            if (varianteProduitRepository.decrementStock(v.getId(), qte) == 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuffisant pour « " + v.getProduit().getNom() + " ».");
            }
            total = total.add(v.getProduit().getPrix().multiply(BigDecimal.valueOf(qte)));
        }

        // 4. Cliente : profil lié au compte si connectée, profil "léger" sinon.
        ClienteProfile clienteProfile = login
            .map(monPanierService::getOrCreateProfile)
            .orElseGet(() -> {
                ClienteProfile cp = new ClienteProfile();
                cp.setTelephone(req.telephone().trim());
                cp.setActif(true);
                return clienteProfileRepository.save(cp);
            });

        // 5. Livraison. Livraison n'a pas de champ "nom" ni "message" : on les place
        //    dans l'adresse pour ne pas les perdre (idéalement : ajouter ces champs).
        String adresse = req.nomComplet().trim() + " — " + req.quartier().trim() + ", " + req.region().trim() + ", " + req.pays().trim();
        if (req.message() != null && !req.message().isBlank()) {
            adresse += " | " + req.message().trim();
        }
        Livraison livraison = new Livraison();
        livraison.setAdresse(adresse.length() > ADRESSE_MAX ? adresse.substring(0, ADRESSE_MAX) : adresse);
        livraison.setVille(req.region().trim());
        livraison.setQuartier(req.quartier().trim());
        livraison.setTelephone(req.telephone().trim());
        livraison.setFraisLivraison(BigDecimal.ZERO);
        livraison.setStatutLivraison(StatutLivraison.EN_ATTENTE);
        livraison = livraisonRepository.save(livraison);

        // 6. Paiement : toujours EN_ATTENTE (seul un webhook / l'admin le passera à REUSSI).
        Paiement paiement = new Paiement();
        paiement.setMontant(total);
        paiement.setDatePaiement(Instant.now());
        paiement.setMoyenPaiement(mode);
        paiement.setReferenceTransaction(null);
        paiement.setStatutPaiement(StatutPaiement.EN_ATTENTE);
        paiement = paiementRepository.save(paiement);

        // 7. Commande.
        Commande commande = new Commande();
        commande.setNumero(genererNumero());
        commande.setDateCommande(Instant.now());
        commande.setMontantTotal(total);
        commande.setModePaiement(mode);
        commande.setStatutCommande(StatutCommande.EN_ATTENTE);
        commande.setPaiement(paiement);
        commande.setLivraison(livraison);
        commande.setClienteProfile(clienteProfile);
        commande = commandeRepository.save(commande);

        // 8. Lignes de commande, avec le prix unitaire lu en base.
        for (VarianteProduit v : variantes) {
            LigneCommande lc = new LigneCommande();
            lc.setQuantite(quantites.get(v.getId()));
            lc.setPrixUnitaire(v.getProduit().getPrix());
            lc.setVariante(v);
            lc.setCommande(commande);
            ligneCommandeRepository.save(lc);
        }

        // 9. Cliente connectée : on vide son panier serveur (sinon les articles commandés
        //    reviendraient à la prochaine connexion).
        login.ifPresent(monPanierService::clearLines);

        LOG.info("Commande {} créée ({} F, {} ligne(s), {})", commande.getNumero(), total, variantes.size(), login.orElse("invité"));
        return new CheckoutResult(commande.getId(), commande.getNumero(), total);
    }

    private String genererNumero() {
        return (
            "MDV-" +
            Long.toString(System.currentTimeMillis(), 36).toUpperCase() +
            String.format("%03d", ThreadLocalRandom.current().nextInt(1000))
        );
    }

    private ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
