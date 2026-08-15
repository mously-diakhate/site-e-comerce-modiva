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
import sn.modiva.app.domain.ClienteProfile;
import sn.modiva.app.repository.ClienteProfileRepository;
import sn.modiva.app.service.criteria.ClienteProfileCriteria;
import sn.modiva.app.service.dto.ClienteProfileDTO;
import sn.modiva.app.service.mapper.ClienteProfileMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ClienteProfile} entities in the database.
 * The main input is a {@link ClienteProfileCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ClienteProfileDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ClienteProfileQueryService extends QueryService<ClienteProfile> {

    private static final Logger LOG = LoggerFactory.getLogger(ClienteProfileQueryService.class);

    private final ClienteProfileRepository clienteProfileRepository;

    private final ClienteProfileMapper clienteProfileMapper;

    public ClienteProfileQueryService(ClienteProfileRepository clienteProfileRepository, ClienteProfileMapper clienteProfileMapper) {
        this.clienteProfileRepository = clienteProfileRepository;
        this.clienteProfileMapper = clienteProfileMapper;
    }

    /**
     * Return a {@link Page} of {@link ClienteProfileDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ClienteProfileDTO> findByCriteria(ClienteProfileCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ClienteProfile> specification = createSpecification(criteria);
        return clienteProfileRepository.findAll(specification, page).map(clienteProfileMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ClienteProfileCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ClienteProfile> specification = createSpecification(criteria);
        return clienteProfileRepository.count(specification);
    }

    /**
     * Function to convert {@link ClienteProfileCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ClienteProfile> createSpecification(ClienteProfileCriteria criteria) {
        Specification<ClienteProfile> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), ClienteProfile_.id),
                buildStringSpecification(criteria.getTelephone(), ClienteProfile_.telephone),
                buildSpecification(criteria.getActif(), ClienteProfile_.actif),
                buildSpecification(criteria.getUserId(), root -> root.join(ClienteProfile_.user, JoinType.LEFT).get(User_.id)),
                buildSpecification(criteria.getPanierId(), root -> root.join(ClienteProfile_.panier, JoinType.LEFT).get(Panier_.id)),
                buildSpecification(criteria.getAdresseId(), root -> root.join(ClienteProfile_.adresses, JoinType.LEFT).get(Adresse_.id)),
                buildSpecification(criteria.getCommandeId(), root -> root.join(ClienteProfile_.commandes, JoinType.LEFT).get(Commande_.id))
            );
        }
        return specification;
    }
}
