package sn.modiva.app.service.mapper;

import static sn.modiva.app.domain.ImageProduitAsserts.*;
import static sn.modiva.app.domain.ImageProduitTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImageProduitMapperTest {

    private ImageProduitMapper imageProduitMapper;

    @BeforeEach
    void setUp() {
        imageProduitMapper = new ImageProduitMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getImageProduitSample1();
        var actual = imageProduitMapper.toEntity(imageProduitMapper.toDto(expected));
        assertImageProduitAllPropertiesEquals(expected, actual);
    }
}
