package sn.modiva.app.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.modiva.app.domain.Couleur;

/**
 * Spring Data JPA repository for the Couleur entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CouleurRepository extends JpaRepository<Couleur, Long> {}
