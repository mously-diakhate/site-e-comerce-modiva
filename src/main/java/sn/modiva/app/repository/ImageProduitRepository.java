package sn.modiva.app.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.modiva.app.domain.ImageProduit;

/**
 * Spring Data JPA repository for the ImageProduit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ImageProduitRepository extends JpaRepository<ImageProduit, Long> {}
