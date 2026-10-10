package sn.modiva.app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.modiva.app.domain.Panier;

/**
 * Spring Data JPA repository for the Panier entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PanierRepository extends JpaRepository<Panier, Long> {
    /**
     * Retrouve le panier lié (via ClienteProfile) au compte utilisateur du
     * login donné. Utilisé par l'endpoint self-service GET /api/paniers/me.
     */
    Optional<Panier> findOneByClienteProfile_User_Login(String login);
}
