package sn.modiva.app.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.modiva.app.domain.CommandeTestSamples.*;
import static sn.modiva.app.domain.LivraisonTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class LivraisonTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Livraison.class);
        Livraison livraison1 = getLivraisonSample1();
        Livraison livraison2 = new Livraison();
        assertThat(livraison1).isNotEqualTo(livraison2);

        livraison2.setId(livraison1.getId());
        assertThat(livraison1).isEqualTo(livraison2);

        livraison2 = getLivraisonSample2();
        assertThat(livraison1).isNotEqualTo(livraison2);
    }

    @Test
    void commandeTest() {
        Livraison livraison = getLivraisonRandomSampleGenerator();
        Commande commandeBack = getCommandeRandomSampleGenerator();

        livraison.setCommande(commandeBack);
        assertThat(livraison.getCommande()).isEqualTo(commandeBack);
        assertThat(commandeBack.getLivraison()).isEqualTo(livraison);

        livraison.commande(null);
        assertThat(livraison.getCommande()).isNull();
        assertThat(commandeBack.getLivraison()).isNull();
    }
}
