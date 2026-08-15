package sn.modiva.app.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.modiva.app.domain.LignePanier;
import sn.modiva.app.repository.LignePanierRepository;
import sn.modiva.app.service.dto.LignePanierDTO;
import sn.modiva.app.service.mapper.LignePanierMapper;

/**
 * Service Implementation for managing {@link sn.modiva.app.domain.LignePanier}.
 */
@Service
@Transactional
public class LignePanierService {

    private static final Logger LOG = LoggerFactory.getLogger(LignePanierService.class);

    private final LignePanierRepository lignePanierRepository;

    private final LignePanierMapper lignePanierMapper;

    public LignePanierService(LignePanierRepository lignePanierRepository, LignePanierMapper lignePanierMapper) {
        this.lignePanierRepository = lignePanierRepository;
        this.lignePanierMapper = lignePanierMapper;
    }

    /**
     * Save a lignePanier.
     *
     * @param lignePanierDTO the entity to save.
     * @return the persisted entity.
     */
    public LignePanierDTO save(LignePanierDTO lignePanierDTO) {
        LOG.debug("Request to save LignePanier : {}", lignePanierDTO);
        LignePanier lignePanier = lignePanierMapper.toEntity(lignePanierDTO);
        lignePanier = lignePanierRepository.save(lignePanier);
        return lignePanierMapper.toDto(lignePanier);
    }

    /**
     * Update a lignePanier.
     *
     * @param lignePanierDTO the entity to save.
     * @return the persisted entity.
     */
    public LignePanierDTO update(LignePanierDTO lignePanierDTO) {
        LOG.debug("Request to update LignePanier : {}", lignePanierDTO);
        LignePanier lignePanier = lignePanierMapper.toEntity(lignePanierDTO);
        lignePanier = lignePanierRepository.save(lignePanier);
        return lignePanierMapper.toDto(lignePanier);
    }

    /**
     * Partially update a lignePanier.
     *
     * @param lignePanierDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LignePanierDTO> partialUpdate(LignePanierDTO lignePanierDTO) {
        LOG.debug("Request to partially update LignePanier : {}", lignePanierDTO);

        return lignePanierRepository
            .findById(lignePanierDTO.getId())
            .map(existingLignePanier -> {
                lignePanierMapper.partialUpdate(existingLignePanier, lignePanierDTO);

                return existingLignePanier;
            })
            .map(lignePanierRepository::save)
            .map(lignePanierMapper::toDto);
    }

    /**
     * Get all the lignePaniers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LignePanierDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all LignePaniers");
        return lignePanierRepository.findAll(pageable).map(lignePanierMapper::toDto);
    }

    /**
     * Get one lignePanier by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LignePanierDTO> findOne(Long id) {
        LOG.debug("Request to get LignePanier : {}", id);
        return lignePanierRepository.findById(id).map(lignePanierMapper::toDto);
    }

    /**
     * Delete the lignePanier by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete LignePanier : {}", id);
        lignePanierRepository.deleteById(id);
    }
}
