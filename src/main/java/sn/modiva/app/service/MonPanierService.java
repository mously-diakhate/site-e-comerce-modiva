package sn.modiva.app.service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sn.modiva.app.domain.ClienteProfile;
import sn.modiva.app.domain.LignePanier;
import sn.modiva.app.domain.Panier;
import sn.modiva.app.domain.User;
import sn.modiva.app.domain.VarianteProduit;
import sn.modiva.app.repository.ClienteProfileRepository;
import sn.modiva.app.repository.LignePanierRepository;
import sn.modiva.app.repository.PanierRepository;
import sn.modiva.app.repository.UserRepository;
import sn.modiva.app.repository.VarianteProduitRepository;

/**
 * Panier de la cliente connectée (self-service).
 *
 * Toutes les méthodes reçoivent le LOGIN de l'appelante (tiré du token JWT par
 * le contrôleur) et ne manipulent jamais que SON panier : un id de ligne
 * appartenant à une autre cliente répond 404, comme s'il n'existait pas.
 */
@Service
@Transactional
public class MonPanierService {

    public static final int QUANTITE_MAX = 100;

    private final ClienteProfileRepository clienteProfileRepository;
    private final PanierRepository panierRepository;
    private final LignePanierRepository lignePanierRepository;
    private final VarianteProduitRepository varianteProduitRepository;
    private final UserRepository userRepository;

    public MonPanierService(
        ClienteProfileRepository clienteProfileRepository,
        PanierRepository panierRepository,
        LignePanierRepository lignePanierRepository,
        VarianteProduitRepository varianteProduitRepository,
        UserRepository userRepository
    ) {
        this.clienteProfileRepository = clienteProfileRepository;
        this.panierRepository = panierRepository;
        this.lignePanierRepository = lignePanierRepository;
        this.varianteProduitRepository = varianteProduitRepository;
        this.userRepository = userRepository;
    }

    /** Résultat renvoyé au frontend : l'id de ligne sert à PATCH/DELETE ensuite. */
    public record LigneResult(Long id, Long varianteId, int quantite) {}

    /** Retrouve le ClienteProfile de l'utilisatrice, ou le crée (lié à son compte). */
    public ClienteProfile getOrCreateProfile(String login) {
        return clienteProfileRepository
            .findOneByUserLogin(login)
            .orElseGet(() -> {
                User user = userRepository
                    .findOneByLogin(login)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur inconnu"));
                ClienteProfile profile = new ClienteProfile();
                profile.setUser(user);
                profile.setActif(true);
                return clienteProfileRepository.save(profile);
            });
    }

    /** Retrouve le Panier de l'utilisatrice, ou le crée (avec son profil si besoin). */
    public Panier getOrCreatePanier(String login) {
        ClienteProfile profile = getOrCreateProfile(login);
        Panier panier = profile.getPanier();
        if (panier == null) {
            panier = new Panier();
            panier.setDateCreation(Instant.now());
            panier = panierRepository.save(panier);
            profile.setPanier(panier);
            clienteProfileRepository.save(profile);
        }
        return panier;
    }

    /** Ajoute la variante au panier, ou fixe sa quantité si elle y est déjà (idempotent). */
    public LigneResult addOrSetLine(String login, Long varianteId, int quantite) {
        verifierQuantite(quantite);
        Panier panier = getOrCreatePanier(login);
        VarianteProduit variante = varianteProduitRepository
            .findById(varianteId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article introuvable"));

        LignePanier ligne = lignePanierRepository
            .findByPanierIdAndVarianteId(panier.getId(), varianteId)
            .orElseGet(() -> {
                LignePanier nouvelle = new LignePanier();
                nouvelle.setPanier(panier);
                nouvelle.setVariante(variante);
                return nouvelle;
            });
        ligne.setQuantite(quantite);
        return toResult(lignePanierRepository.save(ligne));
    }

    public LigneResult updateLine(String login, Long ligneId, int quantite) {
        verifierQuantite(quantite);
        LignePanier ligne = findOwnedLine(login, ligneId);
        ligne.setQuantite(quantite);
        return toResult(lignePanierRepository.save(ligne));
    }

    public void deleteLine(String login, Long ligneId) {
        lignePanierRepository.delete(findOwnedLine(login, ligneId));
    }

    /** Vide le panier serveur de l'utilisatrice (appelé après une commande réussie). */
    public void clearLines(String login) {
        Panier panier = getOrCreatePanier(login);
        List<LignePanier> lignes = lignePanierRepository.findByPanierId(panier.getId());
        lignePanierRepository.deleteAll(lignes);
    }

    private LignePanier findOwnedLine(String login, Long ligneId) {
        Panier panier = getOrCreatePanier(login);
        LignePanier ligne = lignePanierRepository.findById(ligneId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        // 404 (et non 403) : on ne révèle pas l'existence des lignes des autres.
        if (ligne.getPanier() == null || !Objects.equals(ligne.getPanier().getId(), panier.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ligne;
    }

    private void verifierQuantite(int quantite) {
        if (quantite < 1 || quantite > QUANTITE_MAX) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantité invalide");
        }
    }

    private LigneResult toResult(LignePanier ligne) {
        return new LigneResult(ligne.getId(), ligne.getVariante().getId(), ligne.getQuantite());
    }
}
