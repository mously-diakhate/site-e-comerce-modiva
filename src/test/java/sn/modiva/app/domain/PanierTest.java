package sn.modiva.app.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.modiva.app.domain.ClienteProfileTestSamples.*;
import static sn.modiva.app.domain.LignePanierTestSamples.*;
import static sn.modiva.app.domain.PanierTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class PanierTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Panier.class);
        Panier panier1 = getPanierSample1();
        Panier panier2 = new Panier();
        assertThat(panier1).isNotEqualTo(panier2);

        panier2.setId(panier1.getId());
        assertThat(panier1).isEqualTo(panier2);

        panier2 = getPanierSample2();
        assertThat(panier1).isNotEqualTo(panier2);
    }

    @Test
    void ligneTest() {
        Panier panier = getPanierRandomSampleGenerator();
        LignePanier lignePanierBack = getLignePanierRandomSampleGenerator();

        panier.addLigne(lignePanierBack);
        assertThat(panier.getLignes()).containsOnly(lignePanierBack);
        assertThat(lignePanierBack.getPanier()).isEqualTo(panier);

        panier.removeLigne(lignePanierBack);
        assertThat(panier.getLignes()).doesNotContain(lignePanierBack);
        assertThat(lignePanierBack.getPanier()).isNull();

        panier.lignes(new HashSet<>(Set.of(lignePanierBack)));
        assertThat(panier.getLignes()).containsOnly(lignePanierBack);
        assertThat(lignePanierBack.getPanier()).isEqualTo(panier);

        panier.setLignes(new HashSet<>());
        assertThat(panier.getLignes()).doesNotContain(lignePanierBack);
        assertThat(lignePanierBack.getPanier()).isNull();
    }

    @Test
    void clienteProfileTest() {
        Panier panier = getPanierRandomSampleGenerator();
        ClienteProfile clienteProfileBack = getClienteProfileRandomSampleGenerator();

        panier.setClienteProfile(clienteProfileBack);
        assertThat(panier.getClienteProfile()).isEqualTo(clienteProfileBack);
        assertThat(clienteProfileBack.getPanier()).isEqualTo(panier);

        panier.clienteProfile(null);
        assertThat(panier.getClienteProfile()).isNull();
        assertThat(clienteProfileBack.getPanier()).isNull();
    }
}
