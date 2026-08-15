package sn.modiva.app.service.mapper;

import static sn.modiva.app.domain.LivraisonAsserts.*;
import static sn.modiva.app.domain.LivraisonTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LivraisonMapperTest {

    private LivraisonMapper livraisonMapper;

    @BeforeEach
    void setUp() {
        livraisonMapper = new LivraisonMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLivraisonSample1();
        var actual = livraisonMapper.toEntity(livraisonMapper.toDto(expected));
        assertLivraisonAllPropertiesEquals(expected, actual);
    }
}
