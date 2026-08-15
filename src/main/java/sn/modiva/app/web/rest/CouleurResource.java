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
import sn.modiva.app.repository.CouleurRepository;
import sn.modiva.app.service.CouleurService;
import sn.modiva.app.service.dto.CouleurDTO;
import sn.modiva.app.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.modiva.app.domain.Couleur}.
 */
@RestController
@RequestMapping("/api/couleurs")
public class CouleurResource {

    private static final Logger LOG = LoggerFactory.getLogger(CouleurResource.class);

    private static final String ENTITY_NAME = "couleur";

    @Value("${jhipster.clientApp.name:modiva}")
    private String applicationName;

    private final CouleurService couleurService;

    private final CouleurRepository couleurRepository;

    public CouleurResource(CouleurService couleurService, CouleurRepository couleurRepository) {
        this.couleurService = couleurService;
        this.couleurRepository = couleurRepository;
    }

    /**
     * {@code POST  /couleurs} : Create a new couleur.
     *
     * @param couleurDTO the couleurDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new couleurDTO, or with status {@code 400 (Bad Request)} if the couleur has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CouleurDTO> createCouleur(@Valid @RequestBody CouleurDTO couleurDTO) throws URISyntaxException {
        LOG.debug("REST request to save Couleur : {}", couleurDTO);
        if (couleurDTO.getId() != null) {
            throw new BadRequestAlertException("A new couleur cannot already have an ID", ENTITY_NAME, "idexists");
        }
        couleurDTO = couleurService.save(couleurDTO);
        return ResponseEntity.created(new URI("/api/couleurs/" + couleurDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, couleurDTO.getId().toString()))
            .body(couleurDTO);
    }

    /**
     * {@code PUT  /couleurs/:id} : Updates an existing couleur.
     *
     * @param id the id of the couleurDTO to save.
     * @param couleurDTO the couleurDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated couleurDTO,
     * or with status {@code 400 (Bad Request)} if the couleurDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the couleurDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CouleurDTO> updateCouleur(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CouleurDTO couleurDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Couleur : {}, {}", id, couleurDTO);
        if (couleurDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, couleurDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!couleurRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        couleurDTO = couleurService.update(couleurDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, couleurDTO.getId().toString()))
            .body(couleurDTO);
    }

    /**
     * {@code PATCH  /couleurs/:id} : Partial updates given fields of an existing couleur, field will ignore if it is null
     *
     * @param id the id of the couleurDTO to save.
     * @param couleurDTO the couleurDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated couleurDTO,
     * or with status {@code 400 (Bad Request)} if the couleurDTO is not valid,
     * or with status {@code 404 (Not Found)} if the couleurDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the couleurDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CouleurDTO> partialUpdateCouleur(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CouleurDTO couleurDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Couleur partially : {}, {}", id, couleurDTO);
        if (couleurDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, couleurDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!couleurRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CouleurDTO> result = couleurService.partialUpdate(couleurDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, couleurDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /couleurs} : get all the Couleurs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Couleurs in body.
     */
    @GetMapping("")
    public List<CouleurDTO> getAllCouleurs() {
        LOG.debug("REST request to get all Couleurs");
        return couleurService.findAll();
    }

    /**
     * {@code GET  /couleurs/:id} : get the "id" couleur.
     *
     * @param id the id of the couleurDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the couleurDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CouleurDTO> getCouleur(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Couleur : {}", id);
        Optional<CouleurDTO> couleurDTO = couleurService.findOne(id);
        return ResponseUtil.wrapOrNotFound(couleurDTO);
    }

    /**
     * {@code DELETE  /couleurs/:id} : delete the "id" couleur.
     *
     * @param id the id of the couleurDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCouleur(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Couleur : {}", id);
        couleurService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
