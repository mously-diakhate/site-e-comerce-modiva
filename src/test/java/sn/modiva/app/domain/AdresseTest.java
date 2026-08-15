package sn.modiva.app.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.modiva.app.domain.AdresseTestSamples.*;
import static sn.modiva.app.domain.ClienteProfileTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class AdresseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Adresse.class);
        Adresse adresse1 = getAdresseSample1();
        Adresse adresse2 = new Adresse();
        assertThat(adresse1).isNotEqualTo(adresse2);

        adresse2.setId(adresse1.getId());
        assertThat(adresse1).isEqualTo(adresse2);

        adresse2 = getAdresseSample2();
        assertThat(adresse1).isNotEqualTo(adresse2);
    }

    @Test
    void clienteProfileTest() {
        Adresse adresse = getAdresseRandomSampleGenerator();
        ClienteProfile clienteProfileBack = getClienteProfileRandomSampleGenerator();

        adresse.setClienteProfile(clienteProfileBack);
        assertThat(adresse.getClienteProfile()).isEqualTo(clienteProfileBack);

        adresse.clienteProfile(null);
        assertThat(adresse.getClienteProfile()).isNull();
    }
}
