package sn.modiva.app.service;

import java.util.List;
import java.util.Optional;
import sn.modiva.app.service.dto.PaiementDTO;

/**
 * Service Interface for managing {@link sn.modiva.app.domain.Paiement}.
 */
public interface PaiementService {
    /**
     * Save a paiement.
     *
     * @param paiementDTO the entity to save.
     * @return the persisted entity.
     */
    PaiementDTO save(PaiementDTO paiementDTO);

    /**
     * Updates a paiement.
     *
     * @param paiementDTO the entity to update.
     * @return the persisted entity.
     */
    PaiementDTO update(PaiementDTO paiementDTO);

    /**
     * Partially updates a paiement.
     *
     * @param paiementDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PaiementDTO> partialUpdate(PaiementDTO paiementDTO);

    /**
     * Get all the paiements.
     *
     * @return the list of entities.
     */
    List<PaiementDTO> findAll();

    /**
     * Get all the PaiementDTO where Commande is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<PaiementDTO> findAllWhereCommandeIsNull();

    /**
     * Get the "id" paiement.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PaiementDTO> findOne(Long id);

    /**
     * Delete the "id" paiement.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
