package sn.modiva.app.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.modiva.app.domain.Produit;
import sn.modiva.app.repository.ImageProduitRepository; // NEW
import sn.modiva.app.repository.ProduitRepository;
import sn.modiva.app.repository.VarianteProduitRepository; // NEW
import sn.modiva.app.service.ProduitService;
import sn.modiva.app.service.dto.ProduitDTO;
import sn.modiva.app.service.mapper.ProduitMapper;

/**
 * Service Implementation for managing {@link sn.modiva.app.domain.Produit}.
 */
@Service
@Transactional
public class ProduitServiceImpl implements ProduitService {

    private static final Logger LOG = LoggerFactory.getLogger(ProduitServiceImpl.class);

    private final ProduitRepository produitRepository;

    private final ProduitMapper produitMapper;

    private final ImageProduitRepository imageProduitRepository; // NEW
    private final VarianteProduitRepository varianteProduitRepository; // NEW

    public ProduitServiceImpl(
        ProduitRepository produitRepository,
        ProduitMapper produitMapper,
        ImageProduitRepository imageProduitRepository, // NEW
        VarianteProduitRepository varianteProduitRepository // NEW
    ) {
        this.produitRepository = produitRepository;
        this.produitMapper = produitMapper;
        this.imageProduitRepository = imageProduitRepository;
        this.varianteProduitRepository = varianteProduitRepository;
    }

    @Override
    public ProduitDTO save(ProduitDTO produitDTO) {
        LOG.debug("Request to save Produit : {}", produitDTO);
        Produit produit = produitMapper.toEntity(produitDTO);
        produit = produitRepository.save(produit);
        return produitMapper.toDto(produit);
    }

    @Override
    public ProduitDTO update(ProduitDTO produitDTO) {
        LOG.debug("Request to update Produit : {}", produitDTO);
        Produit produit = produitMapper.toEntity(produitDTO);
        produit = produitRepository.save(produit);
        return produitMapper.toDto(produit);
    }

    @Override
    public Optional<ProduitDTO> partialUpdate(ProduitDTO produitDTO) {
        LOG.debug("Request to partially update Produit : {}", produitDTO);

        return produitRepository
            .findById(produitDTO.getId())
            .map(existingProduit -> {
                produitMapper.partialUpdate(existingProduit, produitDTO);

                return existingProduit;
            })
            .map(produitRepository::save)
            .map(produitMapper::toDto);
    }

    public Page<ProduitDTO> findAllWithEagerRelationships(Pageable pageable) {
        return produitRepository.findAllWithEagerRelationships(pageable).map(produitMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProduitDTO> findOne(Long id) {
        LOG.debug("Request to get Produit : {}", id);
        return produitRepository.findOneWithEagerRelationships(id).map(produitMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Produit : {}", id);

        // NEW : supprimer d'abord les entités enfants pour respecter les contraintes de clé étrangère
        varianteProduitRepository.deleteAll(varianteProduitRepository.findByProduitId(id));
        imageProduitRepository.deleteAll(imageProduitRepository.findByProduitIdOrderByOrdreAsc(id));

        produitRepository.deleteById(id);
    }
}
