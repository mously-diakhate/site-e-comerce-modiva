package sn.modiva.app.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.modiva.app.domain.Panier;
import sn.modiva.app.repository.PanierRepository;
import sn.modiva.app.service.dto.PanierDTO;
import sn.modiva.app.service.mapper.PanierMapper;

/**
 * Service Implementation for managing {@link sn.modiva.app.domain.Panier}.
 */
@Service
@Transactional
public class PanierService {

    private static final Logger LOG = LoggerFactory.getLogger(PanierService.class);

    private final PanierRepository panierRepository;

    private final PanierMapper panierMapper;

    public PanierService(PanierRepository panierRepository, PanierMapper panierMapper) {
        this.panierRepository = panierRepository;
        this.panierMapper = panierMapper;
    }

    /**
     * Save a panier.
     *
     * @param panierDTO the entity to save.
     * @return the persisted entity.
     */
    public PanierDTO save(PanierDTO panierDTO) {
        LOG.debug("Request to save Panier : {}", panierDTO);
        Panier panier = panierMapper.toEntity(panierDTO);
        panier = panierRepository.save(panier);
        return panierMapper.toDto(panier);
    }

    /**
     * Update a panier.
     *
     * @param panierDTO the entity to save.
     * @return the persisted entity.
     */
    public PanierDTO update(PanierDTO panierDTO) {
        LOG.debug("Request to update Panier : {}", panierDTO);
        Panier panier = panierMapper.toEntity(panierDTO);
        panier = panierRepository.save(panier);
        return panierMapper.toDto(panier);
    }

    /**
     * Partially update a panier.
     *
     * @param panierDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PanierDTO> partialUpdate(PanierDTO panierDTO) {
        LOG.debug("Request to partially update Panier : {}", panierDTO);

        return panierRepository
            .findById(panierDTO.getId())
            .map(existingPanier -> {
                panierMapper.partialUpdate(existingPanier, panierDTO);

                return existingPanier;
            })
            .map(panierRepository::save)
            .map(panierMapper::toDto);
    }

    /**
     * Get all the paniers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PanierDTO> findAll() {
        LOG.debug("Request to get all Paniers");
        return panierRepository.findAll().stream().map(panierMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get all the paniers where ClienteProfile is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PanierDTO> findAllWhereClienteProfileIsNull() {
        LOG.debug("Request to get all paniers where ClienteProfile is null");
        return StreamSupport.stream(panierRepository.findAll().spliterator(), false)
            .filter(panier -> panier.getClienteProfile() == null)
            .map(panierMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one panier by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PanierDTO> findOne(Long id) {
        LOG.debug("Request to get Panier : {}", id);
        return panierRepository.findById(id).map(panierMapper::toDto);
    }

    /**
     * Delete the panier by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Panier : {}", id);
        panierRepository.deleteById(id);
    }
}
