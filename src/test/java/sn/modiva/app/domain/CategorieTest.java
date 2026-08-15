package sn.modiva.app.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.modiva.app.domain.CategorieTestSamples.*;
import static sn.modiva.app.domain.ProduitTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class CategorieTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Categorie.class);
        Categorie categorie1 = getCategorieSample1();
        Categorie categorie2 = new Categorie();
        assertThat(categorie1).isNotEqualTo(categorie2);

        categorie2.setId(categorie1.getId());
        assertThat(categorie1).isEqualTo(categorie2);

        categorie2 = getCategorieSample2();
        assertThat(categorie1).isNotEqualTo(categorie2);
    }

    @Test
    void produitTest() {
        Categorie categorie = getCategorieRandomSampleGenerator();
        Produit produitBack = getProduitRandomSampleGenerator();

        categorie.addProduit(produitBack);
        assertThat(categorie.getProduits()).containsOnly(produitBack);
        assertThat(produitBack.getCategorie()).isEqualTo(categorie);

        categorie.removeProduit(produitBack);
        assertThat(categorie.getProduits()).doesNotContain(produitBack);
        assertThat(produitBack.getCategorie()).isNull();

        categorie.produits(new HashSet<>(Set.of(produitBack)));
        assertThat(categorie.getProduits()).containsOnly(produitBack);
        assertThat(produitBack.getCategorie()).isEqualTo(categorie);

        categorie.setProduits(new HashSet<>());
        assertThat(categorie.getProduits()).doesNotContain(produitBack);
        assertThat(produitBack.getCategorie()).isNull();
    }
}
