package sn.modiva.app.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.modiva.app.domain.Livraison;
import sn.modiva.app.repository.LivraisonRepository;
import sn.modiva.app.service.LivraisonService;
import sn.modiva.app.service.dto.LivraisonDTO;
import sn.modiva.app.service.mapper.LivraisonMapper;

/**
 * Service Implementation for managing {@link sn.modiva.app.domain.Livraison}.
 */
@Service
@Transactional
public class LivraisonServiceImpl implements LivraisonService {

    private static final Logger LOG = LoggerFactory.getLogger(LivraisonServiceImpl.class);

    private final LivraisonRepository livraisonRepository;

    private final LivraisonMapper livraisonMapper;

    public LivraisonServiceImpl(LivraisonRepository livraisonRepository, LivraisonMapper livraisonMapper) {
        this.livraisonRepository = livraisonRepository;
        this.livraisonMapper = livraisonMapper;
    }

    @Override
    public LivraisonDTO save(LivraisonDTO livraisonDTO) {
        LOG.debug("Request to save Livraison : {}", livraisonDTO);
        Livraison livraison = livraisonMapper.toEntity(livraisonDTO);
        livraison = livraisonRepository.save(livraison);
        return livraisonMapper.toDto(livraison);
    }

    @Override
    public LivraisonDTO update(LivraisonDTO livraisonDTO) {
        LOG.debug("Request to update Livraison : {}", livraisonDTO);
        Livraison livraison = livraisonMapper.toEntity(livraisonDTO);
        livraison = livraisonRepository.save(livraison);
        return livraisonMapper.toDto(livraison);
    }

    @Override
    public Optional<LivraisonDTO> partialUpdate(LivraisonDTO livraisonDTO) {
        LOG.debug("Request to partially update Livraison : {}", livraisonDTO);

        return livraisonRepository
            .findById(livraisonDTO.getId())
            .map(existingLivraison -> {
                livraisonMapper.partialUpdate(existingLivraison, livraisonDTO);

                return existingLivraison;
            })
            .map(livraisonRepository::save)
            .map(livraisonMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LivraisonDTO> findAll() {
        LOG.debug("Request to get all Livraisons");
        return livraisonRepository.findAll().stream().map(livraisonMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get all the livraisons where Commande is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<LivraisonDTO> findAllWhereCommandeIsNull() {
        LOG.debug("Request to get all livraisons where Commande is null");
        return StreamSupport.stream(livraisonRepository.findAll().spliterator(), false)
            .filter(livraison -> livraison.getCommande() == null)
            .map(livraisonMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LivraisonDTO> findOne(Long id) {
        LOG.debug("Request to get Livraison : {}", id);
        return livraisonRepository.findById(id).map(livraisonMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Livraison : {}", id);
        livraisonRepository.deleteById(id);
    }
}
