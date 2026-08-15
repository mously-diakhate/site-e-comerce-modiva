package sn.modiva.app.service.mapper;

import org.mapstruct.*;
import sn.modiva.app.domain.Livraison;
import sn.modiva.app.service.dto.LivraisonDTO;

/**
 * Mapper for the entity {@link Livraison} and its DTO {@link LivraisonDTO}.
 */
@Mapper(componentModel = "spring")
public interface LivraisonMapper extends EntityMapper<LivraisonDTO, Livraison> {}
