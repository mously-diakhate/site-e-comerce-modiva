package sn.modiva.app.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.modiva.app.domain.CategorieTestSamples.*;
import static sn.modiva.app.domain.ImageProduitTestSamples.*;
import static sn.modiva.app.domain.ProduitTestSamples.*;
import static sn.modiva.app.domain.PromotionTestSamples.*;
import static sn.modiva.app.domain.VarianteProduitTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class ProduitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Produit.class);
        Produit produit1 = getProduitSample1();
        Produit produit2 = new Produit();
        assertThat(produit1).isNotEqualTo(produit2);

        produit2.setId(produit1.getId());
        assertThat(produit1).isEqualTo(produit2);

        produit2 = getProduitSample2();
        assertThat(produit1).isNotEqualTo(produit2);
    }

    @Test
    void imageTest() {
        Produit produit = getProduitRandomSampleGenerator();
        ImageProduit imageProduitBack = getImageProduitRandomSampleGenerator();

        produit.addImage(imageProduitBack);
        assertThat(produit.getImages()).containsOnly(imageProduitBack);
        assertThat(imageProduitBack.getProduit()).isEqualTo(produit);

        produit.removeImage(imageProduitBack);
        assertThat(produit.getImages()).doesNotContain(imageProduitBack);
        assertThat(imageProduitBack.getProduit()).isNull();

        produit.images(new HashSet<>(Set.of(imageProduitBack)));
        assertThat(produit.getImages()).containsOnly(imageProduitBack);
        assertThat(imageProduitBack.getProduit()).isEqualTo(produit);

        produit.setImages(new HashSet<>());
        assertThat(produit.getImages()).doesNotContain(imageProduitBack);
        assertThat(imageProduitBack.getProduit()).isNull();
    }

    @Test
    void varianteTest() {
        Produit produit = getProduitRandomSampleGenerator();
        VarianteProduit varianteProduitBack = getVarianteProduitRandomSampleGenerator();

        produit.addVariante(varianteProduitBack);
        assertThat(produit.getVariantes()).containsOnly(varianteProduitBack);
        assertThat(varianteProduitBack.getProduit()).isEqualTo(produit);

        produit.removeVariante(varianteProduitBack);
        assertThat(produit.getVariantes()).doesNotContain(varianteProduitBack);
        assertThat(varianteProduitBack.getProduit()).isNull();

        produit.variantes(new HashSet<>(Set.of(varianteProduitBack)));
        assertThat(produit.getVariantes()).containsOnly(varianteProduitBack);
        assertThat(varianteProduitBack.getProduit()).isEqualTo(produit);

        produit.setVariantes(new HashSet<>());
        assertThat(produit.getVariantes()).doesNotContain(varianteProduitBack);
        assertThat(varianteProduitBack.getProduit()).isNull();
    }

    @Test
    void promotionTest() {
        Produit produit = getProduitRandomSampleGenerator();
        Promotion promotionBack = getPromotionRandomSampleGenerator();

        produit.addPromotion(promotionBack);
        assertThat(produit.getPromotions()).containsOnly(promotionBack);

        produit.removePromotion(promotionBack);
        assertThat(produit.getPromotions()).doesNotContain(promotionBack);

        produit.promotions(new HashSet<>(Set.of(promotionBack)));
        assertThat(produit.getPromotions()).containsOnly(promotionBack);

        produit.setPromotions(new HashSet<>());
        assertThat(produit.getPromotions()).doesNotContain(promotionBack);
    }

    @Test
    void categorieTest() {
        Produit produit = getProduitRandomSampleGenerator();
        Categorie categorieBack = getCategorieRandomSampleGenerator();

        produit.setCategorie(categorieBack);
        assertThat(produit.getCategorie()).isEqualTo(categorieBack);

        produit.categorie(null);
        assertThat(produit.getCategorie()).isNull();
    }
}
