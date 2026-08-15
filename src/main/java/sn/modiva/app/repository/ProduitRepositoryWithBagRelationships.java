package sn.modiva.app.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import sn.modiva.app.domain.Produit;

public interface ProduitRepositoryWithBagRelationships {
    Optional<Produit> fetchBagRelationships(Optional<Produit> produit);

    List<Produit> fetchBagRelationships(List<Produit> produits);

    Page<Produit> fetchBagRelationships(Page<Produit> produits);
}
