package sn.modiva.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.modiva.app.domain.Commande;

/**
 * Spring Data JPA repository for the Commande entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long>, JpaSpecificationExecutor<Commande> {
    /**
     * Retrouve les commandes dont le ClienteProfile est lié au compte
     * utilisateur du login donné (traverse Commande -> ClienteProfile -> User
     * -> login). Utilisé par l'endpoint self-service
     * GET /api/commandes/mes-commandes, pour qu'une cliente ne voie jamais
     * que ses propres commandes.
     */
    Page<Commande> findAllByClienteProfile_User_Login(String login, Pageable pageable);
}
