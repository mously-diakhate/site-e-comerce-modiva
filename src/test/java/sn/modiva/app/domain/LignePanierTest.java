package sn.modiva.app.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.modiva.app.domain.LignePanierTestSamples.*;
import static sn.modiva.app.domain.PanierTestSamples.*;
import static sn.modiva.app.domain.VarianteProduitTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class LignePanierTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LignePanier.class);
        LignePanier lignePanier1 = getLignePanierSample1();
        LignePanier lignePanier2 = new LignePanier();
        assertThat(lignePanier1).isNotEqualTo(lignePanier2);

        lignePanier2.setId(lignePanier1.getId());
        assertThat(lignePanier1).isEqualTo(lignePanier2);

        lignePanier2 = getLignePanierSample2();
        assertThat(lignePanier1).isNotEqualTo(lignePanier2);
    }

    @Test
    void varianteTest() {
        LignePanier lignePanier = getLignePanierRandomSampleGenerator();
        VarianteProduit varianteProduitBack = getVarianteProduitRandomSampleGenerator();

        lignePanier.setVariante(varianteProduitBack);
        assertThat(lignePanier.getVariante()).isEqualTo(varianteProduitBack);

        lignePanier.variante(null);
        assertThat(lignePanier.getVariante()).isNull();
    }

    @Test
    void panierTest() {
        LignePanier lignePanier = getLignePanierRandomSampleGenerator();
        Panier panierBack = getPanierRandomSampleGenerator();

        lignePanier.setPanier(panierBack);
        assertThat(lignePanier.getPanier()).isEqualTo(panierBack);

        lignePanier.panier(null);
        assertThat(lignePanier.getPanier()).isNull();
    }
}
