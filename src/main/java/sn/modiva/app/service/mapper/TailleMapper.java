package sn.modiva.app.service.mapper;

import org.mapstruct.*;
import sn.modiva.app.domain.Taille;
import sn.modiva.app.service.dto.TailleDTO;

/**
 * Mapper for the entity {@link Taille} and its DTO {@link TailleDTO}.
 */
@Mapper(componentModel = "spring")
public interface TailleMapper extends EntityMapper<TailleDTO, Taille> {}
