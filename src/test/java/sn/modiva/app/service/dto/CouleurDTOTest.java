package sn.modiva.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class CouleurDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CouleurDTO.class);
        CouleurDTO couleurDTO1 = new CouleurDTO();
        couleurDTO1.setId(1L);
        CouleurDTO couleurDTO2 = new CouleurDTO();
        assertThat(couleurDTO1).isNotEqualTo(couleurDTO2);
        couleurDTO2.setId(couleurDTO1.getId());
        assertThat(couleurDTO1).isEqualTo(couleurDTO2);
        couleurDTO2.setId(2L);
        assertThat(couleurDTO1).isNotEqualTo(couleurDTO2);
        couleurDTO1.setId(null);
        assertThat(couleurDTO1).isNotEqualTo(couleurDTO2);
    }
}
