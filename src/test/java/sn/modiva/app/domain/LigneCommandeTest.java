package sn.modiva.app.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.modiva.app.domain.CommandeTestSamples.*;
import static sn.modiva.app.domain.LigneCommandeTestSamples.*;
import static sn.modiva.app.domain.VarianteProduitTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class LigneCommandeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LigneCommande.class);
        LigneCommande ligneCommande1 = getLigneCommandeSample1();
        LigneCommande ligneCommande2 = new LigneCommande();
        assertThat(ligneCommande1).isNotEqualTo(ligneCommande2);

        ligneCommande2.setId(ligneCommande1.getId());
        assertThat(ligneCommande1).isEqualTo(ligneCommande2);

        ligneCommande2 = getLigneCommandeSample2();
        assertThat(ligneCommande1).isNotEqualTo(ligneCommande2);
    }

    @Test
    void varianteTest() {
        LigneCommande ligneCommande = getLigneCommandeRandomSampleGenerator();
        VarianteProduit varianteProduitBack = getVarianteProduitRandomSampleGenerator();

        ligneCommande.setVariante(varianteProduitBack);
        assertThat(ligneCommande.getVariante()).isEqualTo(varianteProduitBack);

        ligneCommande.variante(null);
        assertThat(ligneCommande.getVariante()).isNull();
    }

    @Test
    void commandeTest() {
        LigneCommande ligneCommande = getLigneCommandeRandomSampleGenerator();
        Commande commandeBack = getCommandeRandomSampleGenerator();

        ligneCommande.setCommande(commandeBack);
        assertThat(ligneCommande.getCommande()).isEqualTo(commandeBack);

        ligneCommande.commande(null);
        assertThat(ligneCommande.getCommande()).isNull();
    }
}
