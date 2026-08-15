package sn.modiva.app.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.modiva.app.domain.CouleurTestSamples.*;
import static sn.modiva.app.domain.ProduitTestSamples.*;
import static sn.modiva.app.domain.TailleTestSamples.*;
import static sn.modiva.app.domain.VarianteProduitTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class VarianteProduitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VarianteProduit.class);
        VarianteProduit varianteProduit1 = getVarianteProduitSample1();
        VarianteProduit varianteProduit2 = new VarianteProduit();
        assertThat(varianteProduit1).isNotEqualTo(varianteProduit2);

        varianteProduit2.setId(varianteProduit1.getId());
        assertThat(varianteProduit1).isEqualTo(varianteProduit2);

        varianteProduit2 = getVarianteProduitSample2();
        assertThat(varianteProduit1).isNotEqualTo(varianteProduit2);
    }

    @Test
    void tailleTest() {
        VarianteProduit varianteProduit = getVarianteProduitRandomSampleGenerator();
        Taille tailleBack = getTailleRandomSampleGenerator();

        varianteProduit.setTaille(tailleBack);
        assertThat(varianteProduit.getTaille()).isEqualTo(tailleBack);

        varianteProduit.taille(null);
        assertThat(varianteProduit.getTaille()).isNull();
    }

    @Test
    void couleurTest() {
        VarianteProduit varianteProduit = getVarianteProduitRandomSampleGenerator();
        Couleur couleurBack = getCouleurRandomSampleGenerator();

        varianteProduit.setCouleur(couleurBack);
        assertThat(varianteProduit.getCouleur()).isEqualTo(couleurBack);

        varianteProduit.couleur(null);
        assertThat(varianteProduit.getCouleur()).isNull();
    }

    @Test
    void produitTest() {
        VarianteProduit varianteProduit = getVarianteProduitRandomSampleGenerator();
        Produit produitBack = getProduitRandomSampleGenerator();

        varianteProduit.setProduit(produitBack);
        assertThat(varianteProduit.getProduit()).isEqualTo(produitBack);

        varianteProduit.produit(null);
        assertThat(varianteProduit.getProduit()).isNull();
    }
}
