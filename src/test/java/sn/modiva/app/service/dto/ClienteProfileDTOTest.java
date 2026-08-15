package sn.modiva.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.modiva.app.web.rest.TestUtil;

class ClienteProfileDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClienteProfileDTO.class);
        ClienteProfileDTO clienteProfileDTO1 = new ClienteProfileDTO();
        clienteProfileDTO1.setId(1L);
        ClienteProfileDTO clienteProfileDTO2 = new ClienteProfileDTO();
        assertThat(clienteProfileDTO1).isNotEqualTo(clienteProfileDTO2);
        clienteProfileDTO2.setId(clienteProfileDTO1.getId());
        assertThat(clienteProfileDTO1).isEqualTo(clienteProfileDTO2);
        clienteProfileDTO2.setId(2L);
        assertThat(clienteProfileDTO1).isNotEqualTo(clienteProfileDTO2);
        clienteProfileDTO1.setId(null);
        assertThat(clienteProfileDTO1).isNotEqualTo(clienteProfileDTO2);
    }
}
