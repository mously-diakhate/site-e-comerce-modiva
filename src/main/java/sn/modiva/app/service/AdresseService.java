package sn.modiva.app.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.modiva.app.domain.Adresse;
import sn.modiva.app.repository.AdresseRepository;
import sn.modiva.app.service.dto.AdresseDTO;
import sn.modiva.app.service.mapper.AdresseMapper;

/**
 * Service Implementation for managing {@link sn.modiva.app.domain.Adresse}.
 */
@Service
@Transactional
public class AdresseService {

    private static final Logger LOG = LoggerFactory.getLogger(AdresseService.class);

    private final AdresseRepository adresseRepository;

    private final AdresseMapper adresseMapper;

    public AdresseService(AdresseRepository adresseRepository, AdresseMapper adresseMapper) {
        this.adresseRepository = adresseRepository;
        this.adresseMapper = adresseMapper;
    }

    /**
     * Save a adresse.
     *
     * @param adresseDTO the entity to save.
     * @return the persisted entity.
     */
    public AdresseDTO save(AdresseDTO adresseDTO) {
        LOG.debug("Request to save Adresse : {}", adresseDTO);
        Adresse adresse = adresseMapper.toEntity(adresseDTO);
        adresse = adresseRepository.save(adresse);
        return adresseMapper.toDto(adresse);
    }

    /**
     * Update a adresse.
     *
     * @param adresseDTO the entity to save.
     * @return the persisted entity.
     */
    public AdresseDTO update(AdresseDTO adresseDTO) {
        LOG.debug("Request to update Adresse : {}", adresseDTO);
        Adresse adresse = adresseMapper.toEntity(adresseDTO);
        adresse = adresseRepository.save(adresse);
        return adresseMapper.toDto(adresse);
    }

    /**
     * Partially update a adresse.
     *
     * @param adresseDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AdresseDTO> partialUpdate(AdresseDTO adresseDTO) {
        LOG.debug("Request to partially update Adresse : {}", adresseDTO);

        return adresseRepository
            .findById(adresseDTO.getId())
            .map(existingAdresse -> {
                adresseMapper.partialUpdate(existingAdresse, adresseDTO);

                return existingAdresse;
            })
            .map(adresseRepository::save)
            .map(adresseMapper::toDto);
    }

    /**
     * Get all the adresses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AdresseDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Adresses");
        return adresseRepository.findAll(pageable).map(adresseMapper::toDto);
    }

    /**
     * Get one adresse by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AdresseDTO> findOne(Long id) {
        LOG.debug("Request to get Adresse : {}", id);
        return adresseRepository.findById(id).map(adresseMapper::toDto);
    }

    /**
     * Delete the adresse by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Adresse : {}", id);
        adresseRepository.deleteById(id);
    }
}
