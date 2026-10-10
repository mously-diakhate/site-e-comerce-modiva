package sn.modiva.app.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.modiva.app.domain.LignePanier;

/**
 * Spring Data JPA repository for the LignePanier entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LignePanierRepository extends JpaRepository<LignePanier, Long> {
    /**
     * Retrouve les lignes du panier liées (via Panier -> ClienteProfile) au
     * compte utilisateur du login donné. Utilisé par l'endpoint self-service
     * GET /api/ligne-paniers/mine et par les vérifications de propriété dans
     * LignePanierResource.
     */
    List<LignePanier> findAllByPanier_ClienteProfile_User_Login(String login);

    /** Toutes les lignes d'un panier (utilisé par MonPanierService pour vider le panier). */
    List<LignePanier> findByPanierId(Long panierId);

    /** Ligne d'un panier pour une variante donnée (évite les doublons à l'ajout). */
    Optional<LignePanier> findByPanierIdAndVarianteId(Long panierId, Long varianteId);
}
