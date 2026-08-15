package sn.modiva.app.service.mapper;

import static sn.modiva.app.domain.LignePanierAsserts.*;
import static sn.modiva.app.domain.LignePanierTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LignePanierMapperTest {

    private LignePanierMapper lignePanierMapper;

    @BeforeEach
    void setUp() {
        lignePanierMapper = new LignePanierMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLignePanierSample1();
        var actual = lignePanierMapper.toEntity(lignePanierMapper.toDto(expected));
        assertLignePanierAllPropertiesEquals(expected, actual);
    }
}
