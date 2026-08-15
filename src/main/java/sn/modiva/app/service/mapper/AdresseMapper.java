package sn.modiva.app.service.mapper;

import org.mapstruct.*;
import sn.modiva.app.domain.Adresse;
import sn.modiva.app.domain.ClienteProfile;
import sn.modiva.app.service.dto.AdresseDTO;
import sn.modiva.app.service.dto.ClienteProfileDTO;

/**
 * Mapper for the entity {@link Adresse} and its DTO {@link AdresseDTO}.
 */
@Mapper(componentModel = "spring")
public interface AdresseMapper extends EntityMapper<AdresseDTO, Adresse> {
    @Mapping(target = "clienteProfile", source = "clienteProfile", qualifiedByName = "clienteProfileId")
    AdresseDTO toDto(Adresse s);

    @Named("clienteProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClienteProfileDTO toDtoClienteProfileId(ClienteProfile clienteProfile);
}
