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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.modiva.app.repository.TailleRepository;
import sn.modiva.app.service.TailleService;
import sn.modiva.app.service.dto.TailleDTO;
import sn.modiva.app.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.modiva.app.domain.Taille}.
 */
@RestController
@RequestMapping("/api/tailles")
public class TailleResource {

    private static final Logger LOG = LoggerFactory.getLogger(TailleResource.class);

    private static final String ENTITY_NAME = "taille";

    @Value("${jhipster.clientApp.name:modiva}")
    private String applicationName;

    private final TailleService tailleService;

    private final TailleRepository tailleRepository;

    public TailleResource(TailleService tailleService, TailleRepository tailleRepository) {
        this.tailleService = tailleService;
        this.tailleRepository = tailleRepository;
    }

    /**
     * {@code POST  /tailles} : Create a new taille.
     *
     * @param tailleDTO the tailleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tailleDTO, or with status {@code 400 (Bad Request)} if the taille has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TailleDTO> createTaille(@Valid @RequestBody TailleDTO tailleDTO) throws URISyntaxException {
        LOG.debug("REST request to save Taille : {}", tailleDTO);
        if (tailleDTO.getId() != null) {
            throw new BadRequestAlertException("A new taille cannot already have an ID", ENTITY_NAME, "idexists");
        }
        tailleDTO = tailleService.save(tailleDTO);
        return ResponseEntity.created(new URI("/api/tailles/" + tailleDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, tailleDTO.getId().toString()))
            .body(tailleDTO);
    }

    /**
     * {@code PUT  /tailles/:id} : Updates an existing taille.
     *
     * @param id the id of the tailleDTO to save.
     * @param tailleDTO the tailleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tailleDTO,
     * or with status {@code 400 (Bad Request)} if the tailleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tailleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TailleDTO> updateTaille(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TailleDTO tailleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Taille : {}, {}", id, tailleDTO);
        if (tailleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tailleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tailleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        tailleDTO = tailleService.update(tailleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tailleDTO.getId().toString()))
            .body(tailleDTO);
    }

    /**
     * {@code PATCH  /tailles/:id} : Partial updates given fields of an existing taille, field will ignore if it is null
     *
     * @param id the id of the tailleDTO to save.
     * @param tailleDTO the tailleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tailleDTO,
     * or with status {@code 400 (Bad Request)} if the tailleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tailleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tailleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TailleDTO> partialUpdateTaille(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TailleDTO tailleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Taille partially : {}, {}", id, tailleDTO);
        if (tailleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tailleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tailleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TailleDTO> result = tailleService.partialUpdate(tailleDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tailleDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /tailles} : get all the Tailles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Tailles in body.
     */
    @GetMapping("")
    public List<TailleDTO> getAllTailles() {
        LOG.debug("REST request to get all Tailles");
        return tailleService.findAll();
    }

    /**
     * {@code GET  /tailles/:id} : get the "id" taille.
     *
     * @param id the id of the tailleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tailleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TailleDTO> getTaille(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Taille : {}", id);
        Optional<TailleDTO> tailleDTO = tailleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tailleDTO);
    }

    /**
     * {@code DELETE  /tailles/:id} : delete the "id" taille.
     *
     * @param id the id of the tailleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaille(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Taille : {}", id);
        tailleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
