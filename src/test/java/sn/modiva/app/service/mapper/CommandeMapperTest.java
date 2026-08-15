package sn.modiva.app.service.mapper;

import static sn.modiva.app.domain.CommandeAsserts.*;
import static sn.modiva.app.domain.CommandeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommandeMapperTest {

    private CommandeMapper commandeMapper;

    @BeforeEach
    void setUp() {
        commandeMapper = new CommandeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCommandeSample1();
        var actual = commandeMapper.toEntity(commandeMapper.toDto(expected));
        assertCommandeAllPropertiesEquals(expected, actual);
    }
}
