package sn.modiva.app.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.modiva.app.repository.VarianteProduitRepository;
import sn.modiva.app.service.VarianteProduitQueryService;
import sn.modiva.app.service.VarianteProduitService;
import sn.modiva.app.service.criteria.VarianteProduitCriteria;
import sn.modiva.app.service.dto.VarianteProduitDTO;
import sn.modiva.app.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.modiva.app.domain.VarianteProduit}.
 */
@RestController
@RequestMapping("/api/variante-produits")
public class VarianteProduitResource {

    private static final Logger LOG = LoggerFactory.getLogger(VarianteProduitResource.class);

    private static final String ENTITY_NAME = "varianteProduit";

    @Value("${jhipster.clientApp.name:modiva}")
    private String applicationName;

    private final VarianteProduitService varianteProduitService;

    private final VarianteProduitRepository varianteProduitRepository;

    private final VarianteProduitQueryService varianteProduitQueryService;

    public VarianteProduitResource(
        VarianteProduitService varianteProduitService,
        VarianteProduitRepository varianteProduitRepository,
        VarianteProduitQueryService varianteProduitQueryService
    ) {
        this.varianteProduitService = varianteProduitService;
        this.varianteProduitRepository = varianteProduitRepository;
        this.varianteProduitQueryService = varianteProduitQueryService;
    }

    /**
     * {@code POST  /variante-produits} : Create a new varianteProduit.
     *
     * @param varianteProduitDTO the varianteProduitDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new varianteProduitDTO, or with status {@code 400 (Bad Request)} if the varianteProduit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<VarianteProduitDTO> createVarianteProduit(@Valid @RequestBody VarianteProduitDTO varianteProduitDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save VarianteProduit : {}", varianteProduitDTO);
        if (varianteProduitDTO.getId() != null) {
            throw new BadRequestAlertException("A new varianteProduit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        varianteProduitDTO = varianteProduitService.save(varianteProduitDTO);
        return ResponseEntity.created(new URI("/api/variante-produits/" + varianteProduitDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, varianteProduitDTO.getId().toString()))
            .body(varianteProduitDTO);
    }

    /**
     * {@code PUT  /variante-produits/:id} : Updates an existing varianteProduit.
     *
     * @param id the id of the varianteProduitDTO to save.
     * @param varianteProduitDTO the varianteProduitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated varianteProduitDTO,
     * or with status {@code 400 (Bad Request)} if the varianteProduitDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the varianteProduitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VarianteProduitDTO> updateVarianteProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VarianteProduitDTO varianteProduitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update VarianteProduit : {}, {}", id, varianteProduitDTO);
        if (varianteProduitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, varianteProduitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!varianteProduitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        varianteProduitDTO = varianteProduitService.update(varianteProduitDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, varianteProduitDTO.getId().toString()))
            .body(varianteProduitDTO);
    }

    /**
     * {@code PATCH  /variante-produits/:id} : Partial updates given fields of an existing varianteProduit, field will ignore if it is null
     *
     * @param id the id of the varianteProduitDTO to save.
     * @param varianteProduitDTO the varianteProduitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated varianteProduitDTO,
     * or with status {@code 400 (Bad Request)} if the varianteProduitDTO is not valid,
     * or with status {@code 404 (Not Found)} if the varianteProduitDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the varianteProduitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VarianteProduitDTO> partialUpdateVarianteProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VarianteProduitDTO varianteProduitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update VarianteProduit partially : {}, {}", id, varianteProduitDTO);
        if (varianteProduitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, varianteProduitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!varianteProduitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VarianteProduitDTO> result = varianteProduitService.partialUpdate(varianteProduitDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, varianteProduitDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /variante-produits} : get all the Variante Produits.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Variante Produits in body.
     */
    @GetMapping("")
    public ResponseEntity<List<VarianteProduitDTO>> getAllVarianteProduits(
        VarianteProduitCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get VarianteProduits by criteria: {}", criteria);

        Page<VarianteProduitDTO> page = varianteProduitQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /variante-produits/count} : count all the varianteProduits.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countVarianteProduits(VarianteProduitCriteria criteria) {
        LOG.debug("REST request to count VarianteProduits by criteria: {}", criteria);
        return ResponseEntity.ok().body(varianteProduitQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /variante-produits/:id} : get the "id" varianteProduit.
     *
     * @param id the id of the varianteProduitDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the varianteProduitDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VarianteProduitDTO> getVarianteProduit(@PathVariable("id") Long id) {
        LOG.debug("REST request to get VarianteProduit : {}", id);
        Optional<VarianteProduitDTO> varianteProduitDTO = varianteProduitService.findOne(id);
        return ResponseUtil.wrapOrNotFound(varianteProduitDTO);
    }

    /**
     * {@code DELETE  /variante-produits/:id} : delete the "id" varianteProduit.
     *
     * @param id the id of the varianteProduitDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVarianteProduit(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete VarianteProduit : {}", id);
        varianteProduitService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
