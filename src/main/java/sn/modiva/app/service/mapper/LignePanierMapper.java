package sn.modiva.app.service.mapper;

import org.mapstruct.*;
import sn.modiva.app.domain.LignePanier;
import sn.modiva.app.domain.Panier;
import sn.modiva.app.domain.VarianteProduit;
import sn.modiva.app.service.dto.LignePanierDTO;
import sn.modiva.app.service.dto.PanierDTO;
import sn.modiva.app.service.dto.VarianteProduitDTO;

/**
 * Mapper for the entity {@link LignePanier} and its DTO {@link LignePanierDTO}.
 */
@Mapper(componentModel = "spring")
public interface LignePanierMapper extends EntityMapper<LignePanierDTO, LignePanier> {
    @Mapping(target = "variante", source = "variante", qualifiedByName = "varianteProduitId")
    @Mapping(target = "panier", source = "panier", qualifiedByName = "panierId")
    LignePanierDTO toDto(LignePanier s);

    @Named("varianteProduitId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    VarianteProduitDTO toDtoVarianteProduitId(VarianteProduit varianteProduit);

    @Named("panierId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PanierDTO toDtoPanierId(Panier panier);
}
