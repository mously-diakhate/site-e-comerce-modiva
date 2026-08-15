package sn.modiva.app.service.mapper;

import org.mapstruct.*;
import sn.modiva.app.domain.Panier;
import sn.modiva.app.service.dto.PanierDTO;

/**
 * Mapper for the entity {@link Panier} and its DTO {@link PanierDTO}.
 */
@Mapper(componentModel = "spring")
public interface PanierMapper extends EntityMapper<PanierDTO, Panier> {}
