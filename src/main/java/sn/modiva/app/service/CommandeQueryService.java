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
import sn.modiva.app.domain.Commande;
import sn.modiva.app.repository.CommandeRepository;
import sn.modiva.app.service.criteria.CommandeCriteria;
import sn.modiva.app.service.dto.CommandeDTO;
import sn.modiva.app.service.mapper.CommandeMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Commande} entities in the database.
 * The main input is a {@link CommandeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CommandeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CommandeQueryService extends QueryService<Commande> {

    private static final Logger LOG = LoggerFactory.getLogger(CommandeQueryService.class);

    private final CommandeRepository commandeRepository;

    private final CommandeMapper commandeMapper;

    public CommandeQueryService(CommandeRepository commandeRepository, CommandeMapper commandeMapper) {
        this.commandeRepository = commandeRepository;
        this.commandeMapper = commandeMapper;
    }

    /**
     * Return a {@link Page} of {@link CommandeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CommandeDTO> findByCriteria(CommandeCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Commande> specification = createSpecification(criteria);
        return commandeRepository.findAll(specification, page).map(commandeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CommandeCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Commande> specification = createSpecification(criteria);
        return commandeRepository.count(specification);
    }

    /**
     * Function to convert {@link CommandeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Commande> createSpecification(CommandeCriteria criteria) {
        Specification<Commande> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Commande_.id),
                buildStringSpecification(criteria.getNumero(), Commande_.numero),
                buildRangeSpecification(criteria.getDateCommande(), Commande_.dateCommande),
                buildRangeSpecification(criteria.getMontantTotal(), Commande_.montantTotal),
                buildStringSpecification(criteria.getModePaiement(), Commande_.modePaiement),
                buildSpecification(criteria.getStatutCommande(), Commande_.statutCommande),
                buildSpecification(criteria.getPaiementId(), root -> root.join(Commande_.paiement, JoinType.LEFT).get(Paiement_.id)),
                buildSpecification(criteria.getLivraisonId(), root -> root.join(Commande_.livraison, JoinType.LEFT).get(Livraison_.id)),
                buildSpecification(criteria.getLigneId(), root -> root.join(Commande_.lignes, JoinType.LEFT).get(LigneCommande_.id)),
                buildSpecification(criteria.getClienteProfileId(), root ->
                    root.join(Commande_.clienteProfile, JoinType.LEFT).get(ClienteProfile_.id)
                )
            );
        }
        return specification;
    }
}
