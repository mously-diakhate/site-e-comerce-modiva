package sn.modiva.app.service.mapper;

import org.mapstruct.*;
import sn.modiva.app.domain.Couleur;
import sn.modiva.app.domain.Produit;
import sn.modiva.app.domain.Taille;
import sn.modiva.app.domain.VarianteProduit;
import sn.modiva.app.service.dto.CouleurDTO;
import sn.modiva.app.service.dto.ProduitDTO;
import sn.modiva.app.service.dto.TailleDTO;
import sn.modiva.app.service.dto.VarianteProduitDTO;

/**
 * Mapper for the entity {@link VarianteProduit} and its DTO {@link VarianteProduitDTO}.
 */
@Mapper(componentModel = "spring")
public interface VarianteProduitMapper extends EntityMapper<VarianteProduitDTO, VarianteProduit> {
    @Mapping(target = "taille", source = "taille", qualifiedByName = "tailleId")
    @Mapping(target = "couleur", source = "couleur", qualifiedByName = "couleurId")
    @Mapping(target = "produit", source = "produit", qualifiedByName = "produitId")
    VarianteProduitDTO toDto(VarianteProduit s);

    @Named("tailleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "libelle", source = "libelle")
    TailleDTO toDtoTailleId(Taille taille);

    @Named("couleurId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "libelle", source = "libelle")
    @Mapping(target = "codeHex", source = "codeHex")
    CouleurDTO toDtoCouleurId(Couleur couleur);

    @Named("produitId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProduitDTO toDtoProduitId(Produit produit);
}
