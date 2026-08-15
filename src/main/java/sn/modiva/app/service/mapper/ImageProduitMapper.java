package sn.modiva.app.service.mapper;

import org.mapstruct.*;
import sn.modiva.app.domain.ImageProduit;
import sn.modiva.app.domain.Produit;
import sn.modiva.app.service.dto.ImageProduitDTO;
import sn.modiva.app.service.dto.ProduitDTO;

/**
 * Mapper for the entity {@link ImageProduit} and its DTO {@link ImageProduitDTO}.
 */
@Mapper(componentModel = "spring")
public interface ImageProduitMapper extends EntityMapper<ImageProduitDTO, ImageProduit> {
    @Mapping(target = "produit", source = "produit", qualifiedByName = "produitId")
    ImageProduitDTO toDto(ImageProduit s);

    @Named("produitId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProduitDTO toDtoProduitId(Produit produit);
}
