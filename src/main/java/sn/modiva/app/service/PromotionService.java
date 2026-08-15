package sn.modiva.app.service;

import java.util.List;
import java.util.Optional;
import sn.modiva.app.service.dto.PromotionDTO;

/**
 * Service Interface for managing {@link sn.modiva.app.domain.Promotion}.
 */
public interface PromotionService {
    /**
     * Save a promotion.
     *
     * @param promotionDTO the entity to save.
     * @return the persisted entity.
     */
    PromotionDTO save(PromotionDTO promotionDTO);

    /**
     * Updates a promotion.
     *
     * @param promotionDTO the entity to update.
     * @return the persisted entity.
     */
    PromotionDTO update(PromotionDTO promotionDTO);

    /**
     * Partially updates a promotion.
     *
     * @param promotionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PromotionDTO> partialUpdate(PromotionDTO promotionDTO);

    /**
     * Get all the promotions.
     *
     * @return the list of entities.
     */
    List<PromotionDTO> findAll();

    /**
     * Get the "id" promotion.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PromotionDTO> findOne(Long id);

    /**
     * Delete the "id" promotion.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
