package sn.modiva.app.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import sn.modiva.app.domain.Produit;
import sn.modiva.app.domain.Promotion;
import sn.modiva.app.service.dto.ProduitDTO;
import sn.modiva.app.service.dto.PromotionDTO;

/**
 * Mapper for the entity {@link Promotion} and its DTO {@link PromotionDTO}.
 */
@Mapper(componentModel = "spring")
public interface PromotionMapper extends EntityMapper<PromotionDTO, Promotion> {
    @Mapping(target = "produits", source = "produits", qualifiedByName = "produitIdSet")
    PromotionDTO toDto(Promotion s);

    @Mapping(target = "produits", ignore = true)
    @Mapping(target = "removeProduit", ignore = true)
    Promotion toEntity(PromotionDTO promotionDTO);

    @Named("produitId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProduitDTO toDtoProduitId(Produit produit);

    @Named("produitIdSet")
    default Set<ProduitDTO> toDtoProduitIdSet(Set<Produit> produit) {
        return produit.stream().map(this::toDtoProduitId).collect(Collectors.toSet());
    }
}
