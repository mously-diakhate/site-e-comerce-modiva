package sn.modiva.app.service.mapper;

import org.mapstruct.*;
import sn.modiva.app.domain.Commande;
import sn.modiva.app.domain.LigneCommande;
import sn.modiva.app.domain.VarianteProduit;
import sn.modiva.app.service.dto.CommandeDTO;
import sn.modiva.app.service.dto.LigneCommandeDTO;
import sn.modiva.app.service.dto.VarianteProduitDTO;

/**
 * Mapper for the entity {@link LigneCommande} and its DTO {@link LigneCommandeDTO}.
 */
@Mapper(componentModel = "spring")
public interface LigneCommandeMapper extends EntityMapper<LigneCommandeDTO, LigneCommande> {
    @Mapping(target = "variante", source = "variante", qualifiedByName = "varianteProduitId")
    @Mapping(target = "commande", source = "commande", qualifiedByName = "commandeId")
    LigneCommandeDTO toDto(LigneCommande s);

    @Named("varianteProduitId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    VarianteProduitDTO toDtoVarianteProduitId(VarianteProduit varianteProduit);

    @Named("commandeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CommandeDTO toDtoCommandeId(Commande commande);
}
