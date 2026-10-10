package sn.modiva.app.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.modiva.app.domain.Produit;
import sn.modiva.app.domain.Promotion;
import sn.modiva.app.repository.ProduitRepository;
import sn.modiva.app.repository.PromotionRepository;
import sn.modiva.app.service.PromotionService;
import sn.modiva.app.service.dto.ProduitDTO;
import sn.modiva.app.service.dto.PromotionDTO;
import sn.modiva.app.service.mapper.PromotionMapper;

/**
 * Service Implementation for managing {@link sn.modiva.app.domain.Promotion}.
 */
@Service
@Transactional
public class PromotionServiceImpl implements PromotionService {

    private static final Logger LOG = LoggerFactory.getLogger(PromotionServiceImpl.class);

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;
    private final ProduitRepository produitRepository; // NEW

    public PromotionServiceImpl(
        PromotionRepository promotionRepository,
        PromotionMapper promotionMapper,
        ProduitRepository produitRepository // NEW
    ) {
        this.promotionRepository = promotionRepository;
        this.promotionMapper = promotionMapper;
        this.produitRepository = produitRepository;
    }

    @Override
    public PromotionDTO save(PromotionDTO promotionDTO) {
        LOG.debug("Request to save Promotion : {}", promotionDTO);
        Promotion promotion = promotionMapper.toEntity(promotionDTO);
        promotion = promotionRepository.save(promotion);
        syncProduitsPromotions(promotion, promotionDTO); // NEW
        return promotionMapper.toDto(promotion);
    }

    @Override
    public PromotionDTO update(PromotionDTO promotionDTO) {
        LOG.debug("Request to update Promotion : {}", promotionDTO);
        Promotion promotion = promotionMapper.toEntity(promotionDTO);
        promotion = promotionRepository.save(promotion);
        syncProduitsPromotions(promotion, promotionDTO); // NEW
        return promotionMapper.toDto(promotion);
    }

    /**
     * Le côté propriétaire de la relation many-to-many est Produit (via @JoinTable),
     * donc on doit explicitement mettre à jour Produit.promotions pour que la table
     * de jointure rel_produit__promotion soit réellement écrite.
     */
    private void syncProduitsPromotions(Promotion promotion, PromotionDTO promotionDTO) {
        // NEW
        Set<Long> newProduitIds =
            promotionDTO.getProduits() == null
                ? Set.of()
                : promotionDTO.getProduits().stream().map(ProduitDTO::getId).collect(Collectors.toSet());

        // Retire cette promotion de tous les produits qui l'avaient avant (cas d'une désélection).
        List<Produit> currentlyLinked = produitRepository.findAllByPromotionsId(promotion.getId());
        for (Produit p : currentlyLinked) {
            if (!newProduitIds.contains(p.getId())) {
                p.getPromotions().remove(promotion);
                produitRepository.save(p);
            }
        }

        // Ajoute la promotion aux produits nouvellement sélectionnés.
        for (Long produitId : newProduitIds) {
            produitRepository
                .findById(produitId)
                .ifPresent(p -> {
                    if (!p.getPromotions().contains(promotion)) {
                        p.getPromotions().add(promotion);
                        produitRepository.save(p);
                    }
                });
        }
    }

    @Override
    public Optional<PromotionDTO> partialUpdate(PromotionDTO promotionDTO) {
        LOG.debug("Request to partially update Promotion : {}", promotionDTO);

        return promotionRepository
            .findById(promotionDTO.getId())
            .map(existingPromotion -> {
                promotionMapper.partialUpdate(existingPromotion, promotionDTO);
                return existingPromotion;
            })
            .map(promotionRepository::save)
            .map(promotion -> {
                syncProduitsPromotions(promotion, promotionDTO); // NEW
                return promotion;
            })
            .map(promotionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionDTO> findAll() {
        LOG.debug("Request to get all Promotions");
        return promotionRepository.findAll().stream().map(promotionMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PromotionDTO> findOne(Long id) {
        LOG.debug("Request to get Promotion : {}", id);
        return promotionRepository.findById(id).map(promotionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Promotion : {}", id);
        promotionRepository.deleteById(id);
    }
}
