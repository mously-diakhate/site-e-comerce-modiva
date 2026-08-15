package sn.modiva.app.service.mapper;

import static sn.modiva.app.domain.TailleAsserts.*;
import static sn.modiva.app.domain.TailleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TailleMapperTest {

    private TailleMapper tailleMapper;

    @BeforeEach
    void setUp() {
        tailleMapper = new TailleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTailleSample1();
        var actual = tailleMapper.toEntity(tailleMapper.toDto(expected));
        assertTailleAllPropertiesEquals(expected, actual);
    }
}
