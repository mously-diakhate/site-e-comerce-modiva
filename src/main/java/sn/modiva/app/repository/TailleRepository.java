package sn.modiva.app.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.modiva.app.domain.Taille;

/**
 * Spring Data JPA repository for the Taille entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TailleRepository extends JpaRepository<Taille, Long> {}
