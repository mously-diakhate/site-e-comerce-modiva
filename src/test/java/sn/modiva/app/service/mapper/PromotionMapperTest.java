package sn.modiva.app.service.mapper;

import static sn.modiva.app.domain.PromotionAsserts.*;
import static sn.modiva.app.domain.PromotionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PromotionMapperTest {

    private PromotionMapper promotionMapper;

    @BeforeEach
    void setUp() {
        promotionMapper = new PromotionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPromotionSample1();
        var actual = promotionMapper.toEntity(promotionMapper.toDto(expected));
        assertPromotionAllPropertiesEquals(expected, actual);
    }
}
