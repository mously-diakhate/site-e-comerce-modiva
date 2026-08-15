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
import sn.modiva.app.domain.Paiement;
import sn.modiva.app.repository.PaiementRepository;
import sn.modiva.app.service.PaiementService;
import sn.modiva.app.service.dto.PaiementDTO;
import sn.modiva.app.service.mapper.PaiementMapper;

/**
 * Service Implementation for managing {@link sn.modiva.app.domain.Paiement}.
 */
@Service
@Transactional
public class PaiementServiceImpl implements PaiementService {

    private static final Logger LOG = LoggerFactory.getLogger(PaiementServiceImpl.class);

    private final PaiementRepository paiementRepository;

    private final PaiementMapper paiementMapper;

    public PaiementServiceImpl(PaiementRepository paiementRepository, PaiementMapper paiementMapper) {
        this.paiementRepository = paiementRepository;
        this.paiementMapper = paiementMapper;
    }

    @Override
    public PaiementDTO save(PaiementDTO paiementDTO) {
        LOG.debug("Request to save Paiement : {}", paiementDTO);
        Paiement paiement = paiementMapper.toEntity(paiementDTO);
        paiement = paiementRepository.save(paiement);
        return paiementMapper.toDto(paiement);
    }

    @Override
    public PaiementDTO update(PaiementDTO paiementDTO) {
        LOG.debug("Request to update Paiement : {}", paiementDTO);
        Paiement paiement = paiementMapper.toEntity(paiementDTO);
        paiement = paiementRepository.save(paiement);
        return paiementMapper.toDto(paiement);
    }

    @Override
    public Optional<PaiementDTO> partialUpdate(PaiementDTO paiementDTO) {
        LOG.debug("Request to partially update Paiement : {}", paiementDTO);

        return paiementRepository
            .findById(paiementDTO.getId())
            .map(existingPaiement -> {
                paiementMapper.partialUpdate(existingPaiement, paiementDTO);

                return existingPaiement;
            })
            .map(paiementRepository::save)
            .map(paiementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementDTO> findAll() {
        LOG.debug("Request to get all Paiements");
        return paiementRepository.findAll().stream().map(paiementMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get all the paiements where Commande is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PaiementDTO> findAllWhereCommandeIsNull() {
        LOG.debug("Request to get all paiements where Commande is null");
        return StreamSupport.stream(paiementRepository.findAll().spliterator(), false)
            .filter(paiement -> paiement.getCommande() == null)
            .map(paiementMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaiementDTO> findOne(Long id) {
        LOG.debug("Request to get Paiement : {}", id);
        return paiementRepository.findById(id).map(paiementMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Paiement : {}", id);
        paiementRepository.deleteById(id);
    }
}
