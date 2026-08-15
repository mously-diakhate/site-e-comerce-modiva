package sn.modiva.app.service.mapper;

import org.mapstruct.*;
import sn.modiva.app.domain.ClienteProfile;
import sn.modiva.app.domain.Panier;
import sn.modiva.app.domain.User;
import sn.modiva.app.service.dto.ClienteProfileDTO;
import sn.modiva.app.service.dto.PanierDTO;
import sn.modiva.app.service.dto.UserDTO;

/**
 * Mapper for the entity {@link ClienteProfile} and its DTO {@link ClienteProfileDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClienteProfileMapper extends EntityMapper<ClienteProfileDTO, ClienteProfile> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "panier", source = "panier", qualifiedByName = "panierId")
    ClienteProfileDTO toDto(ClienteProfile s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("panierId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PanierDTO toDtoPanierId(Panier panier);
}
