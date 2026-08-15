package sn.modiva.app.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.modiva.app.domain.TailleTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class TailleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Taille.class);
        Taille taille1 = getTailleSample1();
        Taille taille2 = new Taille();
        assertThat(taille1).isNotEqualTo(taille2);

        taille2.setId(taille1.getId());
        assertThat(taille1).isEqualTo(taille2);

        taille2 = getTailleSample2();
        assertThat(taille1).isNotEqualTo(taille2);
    }
}
