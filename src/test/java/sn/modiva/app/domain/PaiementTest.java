package sn.modiva.app.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.modiva.app.domain.CommandeTestSamples.*;
import static sn.modiva.app.domain.PaiementTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class PaiementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Paiement.class);
        Paiement paiement1 = getPaiementSample1();
        Paiement paiement2 = new Paiement();
        assertThat(paiement1).isNotEqualTo(paiement2);

        paiement2.setId(paiement1.getId());
        assertThat(paiement1).isEqualTo(paiement2);

        paiement2 = getPaiementSample2();
        assertThat(paiement1).isNotEqualTo(paiement2);
    }

    @Test
    void commandeTest() {
        Paiement paiement = getPaiementRandomSampleGenerator();
        Commande commandeBack = getCommandeRandomSampleGenerator();

        paiement.setCommande(commandeBack);
        assertThat(paiement.getCommande()).isEqualTo(commandeBack);
        assertThat(commandeBack.getPaiement()).isEqualTo(paiement);

        paiement.commande(null);
        assertThat(paiement.getCommande()).isNull();
        assertThat(commandeBack.getPaiement()).isNull();
    }
}
