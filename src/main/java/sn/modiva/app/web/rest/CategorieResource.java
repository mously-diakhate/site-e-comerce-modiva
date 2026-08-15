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
import sn.modiva.app.repository.CategorieRepository;
import sn.modiva.app.service.CategorieService;
import sn.modiva.app.service.dto.CategorieDTO;
import sn.modiva.app.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.modiva.app.domain.Categorie}.
 */
@RestController
@RequestMapping("/api/categories")
public class CategorieResource {

    private static final Logger LOG = LoggerFactory.getLogger(CategorieResource.class);

    private static final String ENTITY_NAME = "categorie";

    @Value("${jhipster.clientApp.name:modiva}")
    private String applicationName;

    private final CategorieService categorieService;

    private final CategorieRepository categorieRepository;

    public CategorieResource(CategorieService categorieService, CategorieRepository categorieRepository) {
        this.categorieService = categorieService;
        this.categorieRepository = categorieRepository;
    }

    /**
     * {@code POST  /categories} : Create a new categorie.
     *
     * @param categorieDTO the categorieDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new categorieDTO, or with status {@code 400 (Bad Request)} if the categorie has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CategorieDTO> createCategorie(@Valid @RequestBody CategorieDTO categorieDTO) throws URISyntaxException {
        LOG.debug("REST request to save Categorie : {}", categorieDTO);
        if (categorieDTO.getId() != null) {
            throw new BadRequestAlertException("A new categorie cannot already have an ID", ENTITY_NAME, "idexists");
        }
        categorieDTO = categorieService.save(categorieDTO);
        return ResponseEntity.created(new URI("/api/categories/" + categorieDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, categorieDTO.getId().toString()))
            .body(categorieDTO);
    }

    /**
     * {@code PUT  /categories/:id} : Updates an existing categorie.
     *
     * @param id the id of the categorieDTO to save.
     * @param categorieDTO the categorieDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated categorieDTO,
     * or with status {@code 400 (Bad Request)} if the categorieDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the categorieDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategorieDTO> updateCategorie(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CategorieDTO categorieDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Categorie : {}, {}", id, categorieDTO);
        if (categorieDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, categorieDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!categorieRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        categorieDTO = categorieService.update(categorieDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, categorieDTO.getId().toString()))
            .body(categorieDTO);
    }

    /**
     * {@code PATCH  /categories/:id} : Partial updates given fields of an existing categorie, field will ignore if it is null
     *
     * @param id the id of the categorieDTO to save.
     * @param categorieDTO the categorieDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated categorieDTO,
     * or with status {@code 400 (Bad Request)} if the categorieDTO is not valid,
     * or with status {@code 404 (Not Found)} if the categorieDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the categorieDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CategorieDTO> partialUpdateCategorie(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CategorieDTO categorieDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Categorie partially : {}, {}", id, categorieDTO);
        if (categorieDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, categorieDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!categorieRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CategorieDTO> result = categorieService.partialUpdate(categorieDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, categorieDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /categories} : get all the Categories.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Categories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CategorieDTO>> getAllCategories(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Categories");
        Page<CategorieDTO> page = categorieService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /categories/:id} : get the "id" categorie.
     *
     * @param id the id of the categorieDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the categorieDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategorieDTO> getCategorie(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Categorie : {}", id);
        Optional<CategorieDTO> categorieDTO = categorieService.findOne(id);
        return ResponseUtil.wrapOrNotFound(categorieDTO);
    }

    /**
     * {@code DELETE  /categories/:id} : delete the "id" categorie.
     *
     * @param id the id of the categorieDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategorie(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Categorie : {}", id);
        categorieService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
