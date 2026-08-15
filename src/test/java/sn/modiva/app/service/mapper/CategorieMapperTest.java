package sn.modiva.app.service.mapper;

import static sn.modiva.app.domain.CategorieAsserts.*;
import static sn.modiva.app.domain.CategorieTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CategorieMapperTest {

    private CategorieMapper categorieMapper;

    @BeforeEach
    void setUp() {
        categorieMapper = new CategorieMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCategorieSample1();
        var actual = categorieMapper.toEntity(categorieMapper.toDto(expected));
        assertCategorieAllPropertiesEquals(expected, actual);
    }
}
