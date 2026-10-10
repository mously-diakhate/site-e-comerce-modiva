package sn.modiva.app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.modiva.app.domain.ImageProduit;

@SuppressWarnings("unused")
@Repository
public interface ImageProduitRepository extends JpaRepository<ImageProduit, Long> {
    List<ImageProduit> findByProduitIdOrderByOrdreAsc(Long produitId);

    List<ImageProduit> findByProduitIdInOrderByOrdreAsc(List<Long> produitIds);
}
