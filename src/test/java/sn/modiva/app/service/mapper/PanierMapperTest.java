package sn.modiva.app.service.mapper;

import static sn.modiva.app.domain.PanierAsserts.*;
import static sn.modiva.app.domain.PanierTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PanierMapperTest {

    private PanierMapper panierMapper;

    @BeforeEach
    void setUp() {
        panierMapper = new PanierMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPanierSample1();
        var actual = panierMapper.toEntity(panierMapper.toDto(expected));
        assertPanierAllPropertiesEquals(expected, actual);
    }
}
