package sn.modiva.app.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.modiva.app.domain.AdresseTestSamples.*;
import static sn.modiva.app.domain.ClienteProfileTestSamples.*;
import static sn.modiva.app.domain.CommandeTestSamples.*;
import static sn.modiva.app.domain.PanierTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class ClienteProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClienteProfile.class);
        ClienteProfile clienteProfile1 = getClienteProfileSample1();
        ClienteProfile clienteProfile2 = new ClienteProfile();
        assertThat(clienteProfile1).isNotEqualTo(clienteProfile2);

        clienteProfile2.setId(clienteProfile1.getId());
        assertThat(clienteProfile1).isEqualTo(clienteProfile2);

        clienteProfile2 = getClienteProfileSample2();
        assertThat(clienteProfile1).isNotEqualTo(clienteProfile2);
    }

    @Test
    void panierTest() {
        ClienteProfile clienteProfile = getClienteProfileRandomSampleGenerator();
        Panier panierBack = getPanierRandomSampleGenerator();

        clienteProfile.setPanier(panierBack);
        assertThat(clienteProfile.getPanier()).isEqualTo(panierBack);

        clienteProfile.panier(null);
        assertThat(clienteProfile.getPanier()).isNull();
    }

    @Test
    void adresseTest() {
        ClienteProfile clienteProfile = getClienteProfileRandomSampleGenerator();
        Adresse adresseBack = getAdresseRandomSampleGenerator();

        clienteProfile.addAdresse(adresseBack);
        assertThat(clienteProfile.getAdresses()).containsOnly(adresseBack);
        assertThat(adresseBack.getClienteProfile()).isEqualTo(clienteProfile);

        clienteProfile.removeAdresse(adresseBack);
        assertThat(clienteProfile.getAdresses()).doesNotContain(adresseBack);
        assertThat(adresseBack.getClienteProfile()).isNull();

        clienteProfile.adresses(new HashSet<>(Set.of(adresseBack)));
        assertThat(clienteProfile.getAdresses()).containsOnly(adresseBack);
        assertThat(adresseBack.getClienteProfile()).isEqualTo(clienteProfile);

        clienteProfile.setAdresses(new HashSet<>());
        assertThat(clienteProfile.getAdresses()).doesNotContain(adresseBack);
        assertThat(adresseBack.getClienteProfile()).isNull();
    }

    @Test
    void commandeTest() {
        ClienteProfile clienteProfile = getClienteProfileRandomSampleGenerator();
        Commande commandeBack = getCommandeRandomSampleGenerator();

        clienteProfile.addCommande(commandeBack);
        assertThat(clienteProfile.getCommandes()).containsOnly(commandeBack);
        assertThat(commandeBack.getClienteProfile()).isEqualTo(clienteProfile);

        clienteProfile.removeCommande(commandeBack);
        assertThat(clienteProfile.getCommandes()).doesNotContain(commandeBack);
        assertThat(commandeBack.getClienteProfile()).isNull();

        clienteProfile.commandes(new HashSet<>(Set.of(commandeBack)));
        assertThat(clienteProfile.getCommandes()).containsOnly(commandeBack);
        assertThat(commandeBack.getClienteProfile()).isEqualTo(clienteProfile);

        clienteProfile.setCommandes(new HashSet<>());
        assertThat(clienteProfile.getCommandes()).doesNotContain(commandeBack);
        assertThat(commandeBack.getClienteProfile()).isNull();
    }
}
