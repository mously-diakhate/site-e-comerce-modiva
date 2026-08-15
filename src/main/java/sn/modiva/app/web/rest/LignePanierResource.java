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
import sn.modiva.app.repository.LignePanierRepository;
import sn.modiva.app.service.LignePanierService;
import sn.modiva.app.service.dto.LignePanierDTO;
import sn.modiva.app.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.modiva.app.domain.LignePanier}.
 */
@RestController
@RequestMapping("/api/ligne-paniers")
public class LignePanierResource {

    private static final Logger LOG = LoggerFactory.getLogger(LignePanierResource.class);

    private static final String ENTITY_NAME = "lignePanier";

    @Value("${jhipster.clientApp.name:modiva}")
    private String applicationName;

    private final LignePanierService lignePanierService;

    private final LignePanierRepository lignePanierRepository;

    public LignePanierResource(LignePanierService lignePanierService, LignePanierRepository lignePanierRepository) {
        this.lignePanierService = lignePanierService;
        this.lignePanierRepository = lignePanierRepository;
    }

    /**
     * {@code POST  /ligne-paniers} : Create a new lignePanier.
     *
     * @param lignePanierDTO the lignePanierDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new lignePanierDTO, or with status {@code 400 (Bad Request)} if the lignePanier has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LignePanierDTO> createLignePanier(@Valid @RequestBody LignePanierDTO lignePanierDTO) throws URISyntaxException {
        LOG.debug("REST request to save LignePanier : {}", lignePanierDTO);
        if (lignePanierDTO.getId() != null) {
            throw new BadRequestAlertException("A new lignePanier cannot already have an ID", ENTITY_NAME, "idexists");
        }
        lignePanierDTO = lignePanierService.save(lignePanierDTO);
        return ResponseEntity.created(new URI("/api/ligne-paniers/" + lignePanierDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, lignePanierDTO.getId().toString()))
            .body(lignePanierDTO);
    }

    /**
     * {@code PUT  /ligne-paniers/:id} : Updates an existing lignePanier.
     *
     * @param id the id of the lignePanierDTO to save.
     * @param lignePanierDTO the lignePanierDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lignePanierDTO,
     * or with status {@code 400 (Bad Request)} if the lignePanierDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lignePanierDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LignePanierDTO> updateLignePanier(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LignePanierDTO lignePanierDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LignePanier : {}, {}", id, lignePanierDTO);
        if (lignePanierDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lignePanierDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lignePanierRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        lignePanierDTO = lignePanierService.update(lignePanierDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lignePanierDTO.getId().toString()))
            .body(lignePanierDTO);
    }

    /**
     * {@code PATCH  /ligne-paniers/:id} : Partial updates given fields of an existing lignePanier, field will ignore if it is null
     *
     * @param id the id of the lignePanierDTO to save.
     * @param lignePanierDTO the lignePanierDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lignePanierDTO,
     * or with status {@code 400 (Bad Request)} if the lignePanierDTO is not valid,
     * or with status {@code 404 (Not Found)} if the lignePanierDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the lignePanierDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LignePanierDTO> partialUpdateLignePanier(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LignePanierDTO lignePanierDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LignePanier partially : {}, {}", id, lignePanierDTO);
        if (lignePanierDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lignePanierDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lignePanierRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LignePanierDTO> result = lignePanierService.partialUpdate(lignePanierDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lignePanierDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ligne-paniers} : get all the Ligne Paniers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Ligne Paniers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<LignePanierDTO>> getAllLignePaniers(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of LignePaniers");
        Page<LignePanierDTO> page = lignePanierService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ligne-paniers/:id} : get the "id" lignePanier.
     *
     * @param id the id of the lignePanierDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the lignePanierDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LignePanierDTO> getLignePanier(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LignePanier : {}", id);
        Optional<LignePanierDTO> lignePanierDTO = lignePanierService.findOne(id);
        return ResponseUtil.wrapOrNotFound(lignePanierDTO);
    }

    /**
     * {@code DELETE  /ligne-paniers/:id} : delete the "id" lignePanier.
     *
     * @param id the id of the lignePanierDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLignePanier(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LignePanier : {}", id);
        lignePanierService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
