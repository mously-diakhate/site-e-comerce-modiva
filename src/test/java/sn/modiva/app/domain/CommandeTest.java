package sn.modiva.app.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.modiva.app.domain.ClienteProfileTestSamples.*;
import static sn.modiva.app.domain.CommandeTestSamples.*;
import static sn.modiva.app.domain.LigneCommandeTestSamples.*;
import static sn.modiva.app.domain.LivraisonTestSamples.*;
import static sn.modiva.app.domain.PaiementTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class CommandeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Commande.class);
        Commande commande1 = getCommandeSample1();
        Commande commande2 = new Commande();
        assertThat(commande1).isNotEqualTo(commande2);

        commande2.setId(commande1.getId());
        assertThat(commande1).isEqualTo(commande2);

        commande2 = getCommandeSample2();
        assertThat(commande1).isNotEqualTo(commande2);
    }

    @Test
    void paiementTest() {
        Commande commande = getCommandeRandomSampleGenerator();
        Paiement paiementBack = getPaiementRandomSampleGenerator();

        commande.setPaiement(paiementBack);
        assertThat(commande.getPaiement()).isEqualTo(paiementBack);

        commande.paiement(null);
        assertThat(commande.getPaiement()).isNull();
    }

    @Test
    void livraisonTest() {
        Commande commande = getCommandeRandomSampleGenerator();
        Livraison livraisonBack = getLivraisonRandomSampleGenerator();

        commande.setLivraison(livraisonBack);
        assertThat(commande.getLivraison()).isEqualTo(livraisonBack);

        commande.livraison(null);
        assertThat(commande.getLivraison()).isNull();
    }

    @Test
    void ligneTest() {
        Commande commande = getCommandeRandomSampleGenerator();
        LigneCommande ligneCommandeBack = getLigneCommandeRandomSampleGenerator();

        commande.addLigne(ligneCommandeBack);
        assertThat(commande.getLignes()).containsOnly(ligneCommandeBack);
        assertThat(ligneCommandeBack.getCommande()).isEqualTo(commande);

        commande.removeLigne(ligneCommandeBack);
        assertThat(commande.getLignes()).doesNotContain(ligneCommandeBack);
        assertThat(ligneCommandeBack.getCommande()).isNull();

        commande.lignes(new HashSet<>(Set.of(ligneCommandeBack)));
        assertThat(commande.getLignes()).containsOnly(ligneCommandeBack);
        assertThat(ligneCommandeBack.getCommande()).isEqualTo(commande);

        commande.setLignes(new HashSet<>());
        assertThat(commande.getLignes()).doesNotContain(ligneCommandeBack);
        assertThat(ligneCommandeBack.getCommande()).isNull();
    }

    @Test
    void clienteProfileTest() {
        Commande commande = getCommandeRandomSampleGenerator();
        ClienteProfile clienteProfileBack = getClienteProfileRandomSampleGenerator();

        commande.setClienteProfile(clienteProfileBack);
        assertThat(commande.getClienteProfile()).isEqualTo(clienteProfileBack);

        commande.clienteProfile(null);
        assertThat(commande.getClienteProfile()).isNull();
    }
}
