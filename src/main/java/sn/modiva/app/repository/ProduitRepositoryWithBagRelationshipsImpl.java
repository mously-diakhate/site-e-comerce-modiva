package sn.modiva.app.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import sn.modiva.app.domain.Produit;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ProduitRepositoryWithBagRelationshipsImpl implements ProduitRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String PRODUITS_PARAMETER = "produits";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Produit> fetchBagRelationships(Optional<Produit> produit) {
        return produit.map(this::fetchPromotions);
    }

    @Override
    public Page<Produit> fetchBagRelationships(Page<Produit> produits) {
        return new PageImpl<>(fetchBagRelationships(produits.getContent()), produits.getPageable(), produits.getTotalElements());
    }

    @Override
    public List<Produit> fetchBagRelationships(List<Produit> produits) {
        return Optional.of(produits).map(this::fetchPromotions).orElse(Collections.emptyList());
    }

    Produit fetchPromotions(Produit result) {
        return entityManager
            .createQuery("select produit from Produit produit left join fetch produit.promotions where produit.id = :id", Produit.class)
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Produit> fetchPromotions(List<Produit> produits) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, produits.size()).forEach(index -> order.put(produits.get(index).getId(), index));
        List<Produit> result = entityManager
            .createQuery("select produit from Produit produit left join fetch produit.promotions where produit in :produits", Produit.class)
            .setParameter(PRODUITS_PARAMETER, produits)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
