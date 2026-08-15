package sn.modiva.app.service.mapper;

import static sn.modiva.app.domain.CouleurAsserts.*;
import static sn.modiva.app.domain.CouleurTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CouleurMapperTest {

    private CouleurMapper couleurMapper;

    @BeforeEach
    void setUp() {
        couleurMapper = new CouleurMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCouleurSample1();
        var actual = couleurMapper.toEntity(couleurMapper.toDto(expected));
        assertCouleurAllPropertiesEquals(expected, actual);
    }
}
