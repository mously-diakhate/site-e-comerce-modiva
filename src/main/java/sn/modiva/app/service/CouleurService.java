package sn.modiva.app.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.modiva.app.domain.Couleur;
import sn.modiva.app.repository.CouleurRepository;
import sn.modiva.app.service.dto.CouleurDTO;
import sn.modiva.app.service.mapper.CouleurMapper;

/**
 * Service Implementation for managing {@link sn.modiva.app.domain.Couleur}.
 */
@Service
@Transactional
public class CouleurService {

    private static final Logger LOG = LoggerFactory.getLogger(CouleurService.class);

    private final CouleurRepository couleurRepository;

    private final CouleurMapper couleurMapper;

    public CouleurService(CouleurRepository couleurRepository, CouleurMapper couleurMapper) {
        this.couleurRepository = couleurRepository;
        this.couleurMapper = couleurMapper;
    }

    /**
     * Save a couleur.
     *
     * @param couleurDTO the entity to save.
     * @return the persisted entity.
     */
    public CouleurDTO save(CouleurDTO couleurDTO) {
        LOG.debug("Request to save Couleur : {}", couleurDTO);
        Couleur couleur = couleurMapper.toEntity(couleurDTO);
        couleur = couleurRepository.save(couleur);
        return couleurMapper.toDto(couleur);
    }

    /**
     * Update a couleur.
     *
     * @param couleurDTO the entity to save.
     * @return the persisted entity.
     */
    public CouleurDTO update(CouleurDTO couleurDTO) {
        LOG.debug("Request to update Couleur : {}", couleurDTO);
        Couleur couleur = couleurMapper.toEntity(couleurDTO);
        couleur = couleurRepository.save(couleur);
        return couleurMapper.toDto(couleur);
    }

    /**
     * Partially update a couleur.
     *
     * @param couleurDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CouleurDTO> partialUpdate(CouleurDTO couleurDTO) {
        LOG.debug("Request to partially update Couleur : {}", couleurDTO);

        return couleurRepository
            .findById(couleurDTO.getId())
            .map(existingCouleur -> {
                couleurMapper.partialUpdate(existingCouleur, couleurDTO);

                return existingCouleur;
            })
            .map(couleurRepository::save)
            .map(couleurMapper::toDto);
    }

    /**
     * Get all the couleurs.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CouleurDTO> findAll() {
        LOG.debug("Request to get all Couleurs");
        return couleurRepository.findAll().stream().map(couleurMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one couleur by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CouleurDTO> findOne(Long id) {
        LOG.debug("Request to get Couleur : {}", id);
        return couleurRepository.findById(id).map(couleurMapper::toDto);
    }

    /**
     * Delete the couleur by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Couleur : {}", id);
        couleurRepository.deleteById(id);
    }
}
