package sn.modiva.app.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.modiva.app.domain.LigneCommande;
import sn.modiva.app.repository.LigneCommandeRepository;
import sn.modiva.app.service.dto.LigneCommandeDTO;
import sn.modiva.app.service.mapper.LigneCommandeMapper;

/**
 * Service Implementation for managing {@link sn.modiva.app.domain.LigneCommande}.
 */
@Service
@Transactional
public class LigneCommandeService {

    private static final Logger LOG = LoggerFactory.getLogger(LigneCommandeService.class);

    private final LigneCommandeRepository ligneCommandeRepository;

    private final LigneCommandeMapper ligneCommandeMapper;

    public LigneCommandeService(LigneCommandeRepository ligneCommandeRepository, LigneCommandeMapper ligneCommandeMapper) {
        this.ligneCommandeRepository = ligneCommandeRepository;
        this.ligneCommandeMapper = ligneCommandeMapper;
    }

    /**
     * Save a ligneCommande.
     *
     * @param ligneCommandeDTO the entity to save.
     * @return the persisted entity.
     */
    public LigneCommandeDTO save(LigneCommandeDTO ligneCommandeDTO) {
        LOG.debug("Request to save LigneCommande : {}", ligneCommandeDTO);
        LigneCommande ligneCommande = ligneCommandeMapper.toEntity(ligneCommandeDTO);
        ligneCommande = ligneCommandeRepository.save(ligneCommande);
        return ligneCommandeMapper.toDto(ligneCommande);
    }

    /**
     * Update a ligneCommande.
     *
     * @param ligneCommandeDTO the entity to save.
     * @return the persisted entity.
     */
    public LigneCommandeDTO update(LigneCommandeDTO ligneCommandeDTO) {
        LOG.debug("Request to update LigneCommande : {}", ligneCommandeDTO);
        LigneCommande ligneCommande = ligneCommandeMapper.toEntity(ligneCommandeDTO);
        ligneCommande = ligneCommandeRepository.save(ligneCommande);
        return ligneCommandeMapper.toDto(ligneCommande);
    }

    /**
     * Partially update a ligneCommande.
     *
     * @param ligneCommandeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LigneCommandeDTO> partialUpdate(LigneCommandeDTO ligneCommandeDTO) {
        LOG.debug("Request to partially update LigneCommande : {}", ligneCommandeDTO);

        return ligneCommandeRepository
            .findById(ligneCommandeDTO.getId())
            .map(existingLigneCommande -> {
                ligneCommandeMapper.partialUpdate(existingLigneCommande, ligneCommandeDTO);

                return existingLigneCommande;
            })
            .map(ligneCommandeRepository::save)
            .map(ligneCommandeMapper::toDto);
    }

    /**
     * Get all the ligneCommandes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LigneCommandeDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all LigneCommandes");
        return ligneCommandeRepository.findAll(pageable).map(ligneCommandeMapper::toDto);
    }

    /**
     * Get one ligneCommande by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LigneCommandeDTO> findOne(Long id) {
        LOG.debug("Request to get LigneCommande : {}", id);
        return ligneCommandeRepository.findById(id).map(ligneCommandeMapper::toDto);
    }

    /**
     * Delete the ligneCommande by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete LigneCommande : {}", id);
        ligneCommandeRepository.deleteById(id);
    }
}
