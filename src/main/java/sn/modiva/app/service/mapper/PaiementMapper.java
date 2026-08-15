package sn.modiva.app.service.mapper;

import org.mapstruct.*;
import sn.modiva.app.domain.Paiement;
import sn.modiva.app.service.dto.PaiementDTO;

/**
 * Mapper for the entity {@link Paiement} and its DTO {@link PaiementDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaiementMapper extends EntityMapper<PaiementDTO, Paiement> {}
