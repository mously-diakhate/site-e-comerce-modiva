package sn.modiva.app.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.modiva.app.domain.LignePanier;

/**
 * Spring Data JPA repository for the LignePanier entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LignePanierRepository extends JpaRepository<LignePanier, Long> {}
