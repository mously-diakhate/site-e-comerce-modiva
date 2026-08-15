package sn.modiva.app.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.modiva.app.domain.ProduitTestSamples.*;
import static sn.modiva.app.domain.PromotionTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class PromotionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Promotion.class);
        Promotion promotion1 = getPromotionSample1();
        Promotion promotion2 = new Promotion();
        assertThat(promotion1).isNotEqualTo(promotion2);

        promotion2.setId(promotion1.getId());
        assertThat(promotion1).isEqualTo(promotion2);

        promotion2 = getPromotionSample2();
        assertThat(promotion1).isNotEqualTo(promotion2);
    }

    @Test
    void produitTest() {
        Promotion promotion = getPromotionRandomSampleGenerator();
        Produit produitBack = getProduitRandomSampleGenerator();

        promotion.addProduit(produitBack);
        assertThat(promotion.getProduits()).containsOnly(produitBack);
        assertThat(produitBack.getPromotions()).containsOnly(promotion);

        promotion.removeProduit(produitBack);
        assertThat(promotion.getProduits()).doesNotContain(produitBack);
        assertThat(produitBack.getPromotions()).doesNotContain(promotion);

        promotion.produits(new HashSet<>(Set.of(produitBack)));
        assertThat(promotion.getProduits()).containsOnly(produitBack);
        assertThat(produitBack.getPromotions()).containsOnly(promotion);

        promotion.setProduits(new HashSet<>());
        assertThat(promotion.getProduits()).doesNotContain(produitBack);
        assertThat(produitBack.getPromotions()).doesNotContain(promotion);
    }
}
