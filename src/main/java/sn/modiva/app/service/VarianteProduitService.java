package sn.modiva.app.service;

import java.util.Optional;
import sn.modiva.app.service.dto.VarianteProduitDTO;

/**
 * Service Interface for managing {@link sn.modiva.app.domain.VarianteProduit}.
 */
public interface VarianteProduitService {
    /**
     * Save a varianteProduit.
     *
     * @param varianteProduitDTO the entity to save.
     * @return the persisted entity.
     */
    VarianteProduitDTO save(VarianteProduitDTO varianteProduitDTO);

    /**
     * Updates a varianteProduit.
     *
     * @param varianteProduitDTO the entity to update.
     * @return the persisted entity.
     */
    VarianteProduitDTO update(VarianteProduitDTO varianteProduitDTO);

    /**
     * Partially updates a varianteProduit.
     *
     * @param varianteProduitDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<VarianteProduitDTO> partialUpdate(VarianteProduitDTO varianteProduitDTO);

    /**
     * Get the "id" varianteProduit.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VarianteProduitDTO> findOne(Long id);

    /**
     * Delete the "id" varianteProduit.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
