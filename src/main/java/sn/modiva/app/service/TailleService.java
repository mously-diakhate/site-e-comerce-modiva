package sn.modiva.app.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.modiva.app.domain.Taille;
import sn.modiva.app.repository.TailleRepository;
import sn.modiva.app.service.dto.TailleDTO;
import sn.modiva.app.service.mapper.TailleMapper;

/**
 * Service Implementation for managing {@link sn.modiva.app.domain.Taille}.
 */
@Service
@Transactional
public class TailleService {

    private static final Logger LOG = LoggerFactory.getLogger(TailleService.class);

    private final TailleRepository tailleRepository;

    private final TailleMapper tailleMapper;

    public TailleService(TailleRepository tailleRepository, TailleMapper tailleMapper) {
        this.tailleRepository = tailleRepository;
        this.tailleMapper = tailleMapper;
    }

    /**
     * Save a taille.
     *
     * @param tailleDTO the entity to save.
     * @return the persisted entity.
     */
    public TailleDTO save(TailleDTO tailleDTO) {
        LOG.debug("Request to save Taille : {}", tailleDTO);
        Taille taille = tailleMapper.toEntity(tailleDTO);
        taille = tailleRepository.save(taille);
        return tailleMapper.toDto(taille);
    }

    /**
     * Update a taille.
     *
     * @param tailleDTO the entity to save.
     * @return the persisted entity.
     */
    public TailleDTO update(TailleDTO tailleDTO) {
        LOG.debug("Request to update Taille : {}", tailleDTO);
        Taille taille = tailleMapper.toEntity(tailleDTO);
        taille = tailleRepository.save(taille);
        return tailleMapper.toDto(taille);
    }

    /**
     * Partially update a taille.
     *
     * @param tailleDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TailleDTO> partialUpdate(TailleDTO tailleDTO) {
        LOG.debug("Request to partially update Taille : {}", tailleDTO);

        return tailleRepository
            .findById(tailleDTO.getId())
            .map(existingTaille -> {
                tailleMapper.partialUpdate(existingTaille, tailleDTO);

                return existingTaille;
            })
            .map(tailleRepository::save)
            .map(tailleMapper::toDto);
    }

    /**
     * Get all the tailles.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TailleDTO> findAll() {
        LOG.debug("Request to get all Tailles");
        return tailleRepository.findAll().stream().map(tailleMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one taille by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TailleDTO> findOne(Long id) {
        LOG.debug("Request to get Taille : {}", id);
        return tailleRepository.findById(id).map(tailleMapper::toDto);
    }

    /**
     * Delete the taille by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Taille : {}", id);
        tailleRepository.deleteById(id);
    }
}
