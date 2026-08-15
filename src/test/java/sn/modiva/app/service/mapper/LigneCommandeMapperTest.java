package sn.modiva.app.service.mapper;

import static sn.modiva.app.domain.LigneCommandeAsserts.*;
import static sn.modiva.app.domain.LigneCommandeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LigneCommandeMapperTest {

    private LigneCommandeMapper ligneCommandeMapper;

    @BeforeEach
    void setUp() {
        ligneCommandeMapper = new LigneCommandeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLigneCommandeSample1();
        var actual = ligneCommandeMapper.toEntity(ligneCommandeMapper.toDto(expected));
        assertLigneCommandeAllPropertiesEquals(expected, actual);
    }
}
