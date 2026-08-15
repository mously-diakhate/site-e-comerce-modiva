package sn.modiva.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class LignePanierDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LignePanierDTO.class);
        LignePanierDTO lignePanierDTO1 = new LignePanierDTO();
        lignePanierDTO1.setId(1L);
        LignePanierDTO lignePanierDTO2 = new LignePanierDTO();
        assertThat(lignePanierDTO1).isNotEqualTo(lignePanierDTO2);
        lignePanierDTO2.setId(lignePanierDTO1.getId());
        assertThat(lignePanierDTO1).isEqualTo(lignePanierDTO2);
        lignePanierDTO2.setId(2L);
        assertThat(lignePanierDTO1).isNotEqualTo(lignePanierDTO2);
        lignePanierDTO1.setId(null);
        assertThat(lignePanierDTO1).isNotEqualTo(lignePanierDTO2);
    }
}
