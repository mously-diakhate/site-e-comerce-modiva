package sn.modiva.app.service.mapper;

import static sn.modiva.app.domain.PaiementAsserts.*;
import static sn.modiva.app.domain.PaiementTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaiementMapperTest {

    private PaiementMapper paiementMapper;

    @BeforeEach
    void setUp() {
        paiementMapper = new PaiementMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPaiementSample1();
        var actual = paiementMapper.toEntity(paiementMapper.toDto(expected));
        assertPaiementAllPropertiesEquals(expected, actual);
    }
}
