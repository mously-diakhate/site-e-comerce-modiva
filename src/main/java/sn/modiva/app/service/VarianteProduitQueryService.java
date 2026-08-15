package sn.modiva.app.service;

import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.modiva.app.domain.*; // for static metamodels
import sn.modiva.app.domain.VarianteProduit;
import sn.modiva.app.repository.VarianteProduitRepository;
import sn.modiva.app.service.criteria.VarianteProduitCriteria;
import sn.modiva.app.service.dto.VarianteProduitDTO;
import sn.modiva.app.service.mapper.VarianteProduitMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link VarianteProduit} entities in the database.
 * The main input is a {@link VarianteProduitCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link VarianteProduitDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VarianteProduitQueryService extends QueryService<VarianteProduit> {

    private static final Logger LOG = LoggerFactory.getLogger(VarianteProduitQueryService.class);

    private final VarianteProduitRepository varianteProduitRepository;

    private final VarianteProduitMapper varianteProduitMapper;

    public VarianteProduitQueryService(VarianteProduitRepository varianteProduitRepository, VarianteProduitMapper varianteProduitMapper) {
        this.varianteProduitRepository = varianteProduitRepository;
        this.varianteProduitMapper = varianteProduitMapper;
    }

    /**
     * Return a {@link Page} of {@link VarianteProduitDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<VarianteProduitDTO> findByCriteria(VarianteProduitCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<VarianteProduit> specification = createSpecification(criteria);
        return varianteProduitRepository.findAll(specification, page).map(varianteProduitMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VarianteProduitCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<VarianteProduit> specification = createSpecification(criteria);
        return varianteProduitRepository.count(specification);
    }

    /**
     * Function to convert {@link VarianteProduitCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<VarianteProduit> createSpecification(VarianteProduitCriteria criteria) {
        Specification<VarianteProduit> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), VarianteProduit_.id),
                buildRangeSpecification(criteria.getStock(), VarianteProduit_.stock),
                buildSpecification(criteria.getTailleId(), root -> root.join(VarianteProduit_.taille, JoinType.LEFT).get(Taille_.id)),
                buildSpecification(criteria.getCouleurId(), root -> root.join(VarianteProduit_.couleur, JoinType.LEFT).get(Couleur_.id)),
                buildSpecification(criteria.getProduitId(), root -> root.join(VarianteProduit_.produit, JoinType.LEFT).get(Produit_.id))
            );
        }
        return specification;
    }
}
