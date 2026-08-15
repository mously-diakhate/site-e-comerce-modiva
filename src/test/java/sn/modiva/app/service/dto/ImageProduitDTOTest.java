package sn.modiva.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class ImageProduitDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ImageProduitDTO.class);
        ImageProduitDTO imageProduitDTO1 = new ImageProduitDTO();
        imageProduitDTO1.setId(1L);
        ImageProduitDTO imageProduitDTO2 = new ImageProduitDTO();
        assertThat(imageProduitDTO1).isNotEqualTo(imageProduitDTO2);
        imageProduitDTO2.setId(imageProduitDTO1.getId());
        assertThat(imageProduitDTO1).isEqualTo(imageProduitDTO2);
        imageProduitDTO2.setId(2L);
        assertThat(imageProduitDTO1).isNotEqualTo(imageProduitDTO2);
        imageProduitDTO1.setId(null);
        assertThat(imageProduitDTO1).isNotEqualTo(imageProduitDTO2);
    }
}
