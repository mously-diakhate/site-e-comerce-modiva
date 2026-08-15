package sn.modiva.app.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import sn.modiva.app.domain.Categorie;
import sn.modiva.app.domain.Produit;
import sn.modiva.app.domain.Promotion;
import sn.modiva.app.service.dto.CategorieDTO;
import sn.modiva.app.service.dto.ProduitDTO;
import sn.modiva.app.service.dto.PromotionDTO;

/**
 * Mapper for the entity {@link Produit} and its DTO {@link ProduitDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProduitMapper extends EntityMapper<ProduitDTO, Produit> {
    @Mapping(target = "promotions", source = "promotions", qualifiedByName = "promotionIdSet")
    @Mapping(target = "categorie", source = "categorie", qualifiedByName = "categorieId")
    ProduitDTO toDto(Produit s);

    @Mapping(target = "removePromotion", ignore = true)
    Produit toEntity(ProduitDTO produitDTO);

    @Named("promotionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PromotionDTO toDtoPromotionId(Promotion promotion);

    @Named("promotionIdSet")
    default Set<PromotionDTO> toDtoPromotionIdSet(Set<Promotion> promotion) {
        return promotion.stream().map(this::toDtoPromotionId).collect(Collectors.toSet());
    }

    @Named("categorieId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CategorieDTO toDtoCategorieId(Categorie categorie);
}
