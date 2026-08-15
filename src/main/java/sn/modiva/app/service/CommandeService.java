package sn.modiva.app.service;

import java.util.Optional;
import sn.modiva.app.service.dto.CommandeDTO;

/**
 * Service Interface for managing {@link sn.modiva.app.domain.Commande}.
 */
public interface CommandeService {
    /**
     * Save a commande.
     *
     * @param commandeDTO the entity to save.
     * @return the persisted entity.
     */
    CommandeDTO save(CommandeDTO commandeDTO);

    /**
     * Updates a commande.
     *
     * @param commandeDTO the entity to update.
     * @return the persisted entity.
     */
    CommandeDTO update(CommandeDTO commandeDTO);

    /**
     * Partially updates a commande.
     *
     * @param commandeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CommandeDTO> partialUpdate(CommandeDTO commandeDTO);

    /**
     * Get the "id" commande.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CommandeDTO> findOne(Long id);

    /**
     * Delete the "id" commande.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
