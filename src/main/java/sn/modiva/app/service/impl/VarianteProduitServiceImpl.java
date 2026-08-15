package sn.modiva.app.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.modiva.app.domain.VarianteProduit;
import sn.modiva.app.repository.VarianteProduitRepository;
import sn.modiva.app.service.VarianteProduitService;
import sn.modiva.app.service.dto.VarianteProduitDTO;
import sn.modiva.app.service.mapper.VarianteProduitMapper;

/**
 * Service Implementation for managing {@link sn.modiva.app.domain.VarianteProduit}.
 */
@Service
@Transactional
public class VarianteProduitServiceImpl implements VarianteProduitService {

    private static final Logger LOG = LoggerFactory.getLogger(VarianteProduitServiceImpl.class);

    private final VarianteProduitRepository varianteProduitRepository;

    private final VarianteProduitMapper varianteProduitMapper;

    public VarianteProduitServiceImpl(VarianteProduitRepository varianteProduitRepository, VarianteProduitMapper varianteProduitMapper) {
        this.varianteProduitRepository = varianteProduitRepository;
        this.varianteProduitMapper = varianteProduitMapper;
    }

    @Override
    public VarianteProduitDTO save(VarianteProduitDTO varianteProduitDTO) {
        LOG.debug("Request to save VarianteProduit : {}", varianteProduitDTO);
        VarianteProduit varianteProduit = varianteProduitMapper.toEntity(varianteProduitDTO);
        varianteProduit = varianteProduitRepository.save(varianteProduit);
        return varianteProduitMapper.toDto(varianteProduit);
    }

    @Override
    public VarianteProduitDTO update(VarianteProduitDTO varianteProduitDTO) {
        LOG.debug("Request to update VarianteProduit : {}", varianteProduitDTO);
        VarianteProduit varianteProduit = varianteProduitMapper.toEntity(varianteProduitDTO);
        varianteProduit = varianteProduitRepository.save(varianteProduit);
        return varianteProduitMapper.toDto(varianteProduit);
    }

    @Override
    public Optional<VarianteProduitDTO> partialUpdate(VarianteProduitDTO varianteProduitDTO) {
        LOG.debug("Request to partially update VarianteProduit : {}", varianteProduitDTO);

        return varianteProduitRepository
            .findById(varianteProduitDTO.getId())
            .map(existingVarianteProduit -> {
                varianteProduitMapper.partialUpdate(existingVarianteProduit, varianteProduitDTO);

                return existingVarianteProduit;
            })
            .map(varianteProduitRepository::save)
            .map(varianteProduitMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VarianteProduitDTO> findOne(Long id) {
        LOG.debug("Request to get VarianteProduit : {}", id);
        return varianteProduitRepository.findById(id).map(varianteProduitMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete VarianteProduit : {}", id);
        varianteProduitRepository.deleteById(id);
    }
}
