package sn.modiva.app.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.modiva.app.domain.CouleurTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class CouleurTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Couleur.class);
        Couleur couleur1 = getCouleurSample1();
        Couleur couleur2 = new Couleur();
        assertThat(couleur1).isNotEqualTo(couleur2);

        couleur2.setId(couleur1.getId());
        assertThat(couleur1).isEqualTo(couleur2);

        couleur2 = getCouleurSample2();
        assertThat(couleur1).isNotEqualTo(couleur2);
    }
}
