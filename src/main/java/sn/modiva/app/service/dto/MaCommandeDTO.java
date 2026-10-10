package sn.modiva.app.service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import sn.modiva.app.domain.enumeration.StatutCommande;
import sn.modiva.app.domain.enumeration.StatutLivraison;

/**
 * Vue "Mes commandes" d'une cliente : la commande + ses articles + sa livraison,
 * prête à afficher (un seul appel, aucun endpoint admin n'est ouvert).
 */
public record MaCommandeDTO(
    Long id,
    String numero,
    Instant dateCommande,
    BigDecimal montantTotal,
    String modePaiement,
    StatutCommande statutCommande,
    LivraisonInfo livraison,
    List<Ligne> lignes
) {
    public record LivraisonInfo(
        String adresse,
        String ville,
        String quartier,
        String telephone,
        BigDecimal fraisLivraison,
        StatutLivraison statutLivraison
    ) {}

    public record Ligne(Long id, Long produitId, String nom, String taille, String couleur, Integer quantite, BigDecimal prixUnitaire) {}
}
