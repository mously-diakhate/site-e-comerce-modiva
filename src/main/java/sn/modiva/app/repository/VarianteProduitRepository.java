package sn.modiva.app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.modiva.app.domain.VarianteProduit;

@SuppressWarnings("unused")
@Repository
public interface VarianteProduitRepository extends JpaRepository<VarianteProduit, Long>, JpaSpecificationExecutor<VarianteProduit> {
    List<VarianteProduit> findByProduitId(Long produitId);

    @Query("select v from VarianteProduit v join fetch v.couleur join fetch v.taille where v.produit.id in :produitIds")
    List<VarianteProduit> findByProduitIdInWithCouleurAndTaille(@Param("produitIds") List<Long> produitIds);

    /**
     * Déduit le stock de façon atomique (utilisé par CheckoutService).
     * Renvoie 0 si le stock est insuffisant : aucune ligne n'est modifiée.
     */
    @Modifying
    @Query("update VarianteProduit v set v.stock = v.stock - :qte where v.id = :id and v.stock >= :qte")
    int decrementStock(@Param("id") Long id, @Param("qte") int qte);
}
