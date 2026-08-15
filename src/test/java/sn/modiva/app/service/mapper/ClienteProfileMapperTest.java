package sn.modiva.app.service.mapper;

import static sn.modiva.app.domain.ClienteProfileAsserts.*;
import static sn.modiva.app.domain.ClienteProfileTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClienteProfileMapperTest {

    private ClienteProfileMapper clienteProfileMapper;

    @BeforeEach
    void setUp() {
        clienteProfileMapper = new ClienteProfileMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getClienteProfileSample1();
        var actual = clienteProfileMapper.toEntity(clienteProfileMapper.toDto(expected));
        assertClienteProfileAllPropertiesEquals(expected, actual);
    }
}
