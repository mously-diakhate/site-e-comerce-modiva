package sn.modiva.app.service;

import java.util.List;
import java.util.Optional;
import sn.modiva.app.service.dto.LivraisonDTO;

/**
 * Service Interface for managing {@link sn.modiva.app.domain.Livraison}.
 */
public interface LivraisonService {
    /**
     * Save a livraison.
     *
     * @param livraisonDTO the entity to save.
     * @return the persisted entity.
     */
    LivraisonDTO save(LivraisonDTO livraisonDTO);

    /**
     * Updates a livraison.
     *
     * @param livraisonDTO the entity to update.
     * @return the persisted entity.
     */
    LivraisonDTO update(LivraisonDTO livraisonDTO);

    /**
     * Partially updates a livraison.
     *
     * @param livraisonDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<LivraisonDTO> partialUpdate(LivraisonDTO livraisonDTO);

    /**
     * Get all the livraisons.
     *
     * @return the list of entities.
     */
    List<LivraisonDTO> findAll();

    /**
     * Get all the LivraisonDTO where Commande is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<LivraisonDTO> findAllWhereCommandeIsNull();

    /**
     * Get the "id" livraison.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LivraisonDTO> findOne(Long id);

    /**
     * Delete the "id" livraison.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
