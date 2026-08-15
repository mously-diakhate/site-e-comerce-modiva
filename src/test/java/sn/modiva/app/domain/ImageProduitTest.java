package sn.modiva.app.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.modiva.app.domain.ImageProduitTestSamples.*;
import static sn.modiva.app.domain.ProduitTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class ImageProduitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ImageProduit.class);
        ImageProduit imageProduit1 = getImageProduitSample1();
        ImageProduit imageProduit2 = new ImageProduit();
        assertThat(imageProduit1).isNotEqualTo(imageProduit2);

        imageProduit2.setId(imageProduit1.getId());
        assertThat(imageProduit1).isEqualTo(imageProduit2);

        imageProduit2 = getImageProduitSample2();
        assertThat(imageProduit1).isNotEqualTo(imageProduit2);
    }

    @Test
    void produitTest() {
        ImageProduit imageProduit = getImageProduitRandomSampleGenerator();
        Produit produitBack = getProduitRandomSampleGenerator();

        imageProduit.setProduit(produitBack);
        assertThat(imageProduit.getProduit()).isEqualTo(produitBack);

        imageProduit.produit(null);
        assertThat(imageProduit.getProduit()).isNull();
    }
}
