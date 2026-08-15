package sn.modiva.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class VarianteProduitDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VarianteProduitDTO.class);
        VarianteProduitDTO varianteProduitDTO1 = new VarianteProduitDTO();
        varianteProduitDTO1.setId(1L);
        VarianteProduitDTO varianteProduitDTO2 = new VarianteProduitDTO();
        assertThat(varianteProduitDTO1).isNotEqualTo(varianteProduitDTO2);
        varianteProduitDTO2.setId(varianteProduitDTO1.getId());
        assertThat(varianteProduitDTO1).isEqualTo(varianteProduitDTO2);
        varianteProduitDTO2.setId(2L);
        assertThat(varianteProduitDTO1).isNotEqualTo(varianteProduitDTO2);
        varianteProduitDTO1.setId(null);
        assertThat(varianteProduitDTO1).isNotEqualTo(varianteProduitDTO2);
    }
}
