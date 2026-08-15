package sn.modiva.app.service.mapper;

import org.mapstruct.*;
import sn.modiva.app.domain.Couleur;
import sn.modiva.app.service.dto.CouleurDTO;

/**
 * Mapper for the entity {@link Couleur} and its DTO {@link CouleurDTO}.
 */
@Mapper(componentModel = "spring")
public interface CouleurMapper extends EntityMapper<CouleurDTO, Couleur> {}
