package sn.modiva.app.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.modiva.app.service.dto.ClienteProfileDTO;

/**
 * Service Interface for managing {@link sn.modiva.app.domain.ClienteProfile}.
 */
public interface ClienteProfileService {
    /**
     * Save a clienteProfile.
     *
     * @param clienteProfileDTO the entity to save.
     * @return the persisted entity.
     */
    ClienteProfileDTO save(ClienteProfileDTO clienteProfileDTO);

    /**
     * Updates a clienteProfile.
     *
     * @param clienteProfileDTO the entity to update.
     * @return the persisted entity.
     */
    ClienteProfileDTO update(ClienteProfileDTO clienteProfileDTO);

    /**
     * Partially updates a clienteProfile.
     *
     * @param clienteProfileDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ClienteProfileDTO> partialUpdate(ClienteProfileDTO clienteProfileDTO);

    /**
     * Get all the clienteProfiles with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ClienteProfileDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" clienteProfile.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ClienteProfileDTO> findOne(Long id);

    /**
     * Delete the "id" clienteProfile.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
