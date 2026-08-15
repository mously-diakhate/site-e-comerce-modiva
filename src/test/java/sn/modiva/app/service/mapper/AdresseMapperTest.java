package sn.modiva.app.service.mapper;

import static sn.modiva.app.domain.AdresseAsserts.*;
import static sn.modiva.app.domain.AdresseTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdresseMapperTest {

    private AdresseMapper adresseMapper;

    @BeforeEach
    void setUp() {
        adresseMapper = new AdresseMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAdresseSample1();
        var actual = adresseMapper.toEntity(adresseMapper.toDto(expected));
        assertAdresseAllPropertiesEquals(expected, actual);
    }
}
