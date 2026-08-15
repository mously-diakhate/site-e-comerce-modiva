package sn.modiva.app.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.modiva.app.domain.VarianteProduit;

/**
 * Spring Data JPA repository for the VarianteProduit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VarianteProduitRepository extends JpaRepository<VarianteProduit, Long>, JpaSpecificationExecutor<VarianteProduit> {}
