package sn.modiva.app.service.mapper;

import org.mapstruct.*;
import sn.modiva.app.domain.ClienteProfile;
import sn.modiva.app.domain.Commande;
import sn.modiva.app.domain.Livraison;
import sn.modiva.app.domain.Paiement;
import sn.modiva.app.service.dto.ClienteProfileDTO;
import sn.modiva.app.service.dto.CommandeDTO;
import sn.modiva.app.service.dto.LivraisonDTO;
import sn.modiva.app.service.dto.PaiementDTO;

/**
 * Mapper for the entity {@link Commande} and its DTO {@link CommandeDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommandeMapper extends EntityMapper<CommandeDTO, Commande> {
    @Mapping(target = "paiement", source = "paiement", qualifiedByName = "paiementId")
    @Mapping(target = "livraison", source = "livraison", qualifiedByName = "livraisonId")
    @Mapping(target = "clienteProfile", source = "clienteProfile", qualifiedByName = "clienteProfileId")
    CommandeDTO toDto(Commande s);

    @Named("paiementId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PaiementDTO toDtoPaiementId(Paiement paiement);

    @Named("livraisonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LivraisonDTO toDtoLivraisonId(Livraison livraison);

    @Named("clienteProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClienteProfileDTO toDtoClienteProfileId(ClienteProfile clienteProfile);
}
