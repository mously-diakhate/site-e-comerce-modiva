package sn.modiva.app.service.mapper;

import org.mapstruct.*;
import sn.modiva.app.domain.Categorie;
import sn.modiva.app.service.dto.CategorieDTO;

/**
 * Mapper for the entity {@link Categorie} and its DTO {@link CategorieDTO}.
 */
@Mapper(componentModel = "spring")
public interface CategorieMapper extends EntityMapper<CategorieDTO, Categorie> {}
