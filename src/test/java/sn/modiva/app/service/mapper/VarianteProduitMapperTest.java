package sn.modiva.app.service.mapper;

import static sn.modiva.app.domain.VarianteProduitAsserts.*;
import static sn.modiva.app.domain.VarianteProduitTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VarianteProduitMapperTest {

    private VarianteProduitMapper varianteProduitMapper;

    @BeforeEach
    void setUp() {
        varianteProduitMapper = new VarianteProduitMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getVarianteProduitSample1();
        var actual = varianteProduitMapper.toEntity(varianteProduitMapper.toDto(expected));
        assertVarianteProduitAllPropertiesEquals(expected, actual);
    }
}
