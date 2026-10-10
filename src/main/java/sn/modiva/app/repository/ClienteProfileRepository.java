package sn.modiva.app.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.modiva.app.domain.ClienteProfile;

/**
 * Spring Data JPA repository for the ClienteProfile entity.
 */
@Repository
public interface ClienteProfileRepository extends JpaRepository<ClienteProfile, Long>, JpaSpecificationExecutor<ClienteProfile> {
    default Optional<ClienteProfile> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ClienteProfile> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ClienteProfile> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select clienteProfile from ClienteProfile clienteProfile left join fetch clienteProfile.user",
        countQuery = "select count(clienteProfile) from ClienteProfile clienteProfile"
    )
    Page<ClienteProfile> findAllWithToOneRelationships(Pageable pageable);

    @Query("select clienteProfile from ClienteProfile clienteProfile left join fetch clienteProfile.user")
    List<ClienteProfile> findAllWithToOneRelationships();

    @Query("select clienteProfile from ClienteProfile clienteProfile left join fetch clienteProfile.user where clienteProfile.id =:id")
    Optional<ClienteProfile> findOneWithToOneRelationships(@Param("id") Long id);

    /**
     * Retrouve le profil client lié au compte utilisateur connecté (via son
     * login). Utilisé par l'endpoint self-service GET /api/cliente-profiles/me,
     * pour qu'une cliente puisse consulter son propre profil sans passer par
     * l'API de recherche générale (réservée à l'admin).
     */
    Optional<ClienteProfile> findOneByUserLogin(String login);
}
