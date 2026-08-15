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
import sn.modiva.app.repository.LigneCommandeRepository;
import sn.modiva.app.service.LigneCommandeService;
import sn.modiva.app.service.dto.LigneCommandeDTO;
import sn.modiva.app.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.modiva.app.domain.LigneCommande}.
 */
@RestController
@RequestMapping("/api/ligne-commandes")
public class LigneCommandeResource {

    private static final Logger LOG = LoggerFactory.getLogger(LigneCommandeResource.class);

    private static final String ENTITY_NAME = "ligneCommande";

    @Value("${jhipster.clientApp.name:modiva}")
    private String applicationName;

    private final LigneCommandeService ligneCommandeService;

    private final LigneCommandeRepository ligneCommandeRepository;

    public LigneCommandeResource(LigneCommandeService ligneCommandeService, LigneCommandeRepository ligneCommandeRepository) {
        this.ligneCommandeService = ligneCommandeService;
        this.ligneCommandeRepository = ligneCommandeRepository;
    }

    /**
     * {@code POST  /ligne-commandes} : Create a new ligneCommande.
     *
     * @param ligneCommandeDTO the ligneCommandeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ligneCommandeDTO, or with status {@code 400 (Bad Request)} if the ligneCommande has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LigneCommandeDTO> createLigneCommande(@Valid @RequestBody LigneCommandeDTO ligneCommandeDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save LigneCommande : {}", ligneCommandeDTO);
        if (ligneCommandeDTO.getId() != null) {
            throw new BadRequestAlertException("A new ligneCommande cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ligneCommandeDTO = ligneCommandeService.save(ligneCommandeDTO);
        return ResponseEntity.created(new URI("/api/ligne-commandes/" + ligneCommandeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ligneCommandeDTO.getId().toString()))
            .body(ligneCommandeDTO);
    }

    /**
     * {@code PUT  /ligne-commandes/:id} : Updates an existing ligneCommande.
     *
     * @param id the id of the ligneCommandeDTO to save.
     * @param ligneCommandeDTO the ligneCommandeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ligneCommandeDTO,
     * or with status {@code 400 (Bad Request)} if the ligneCommandeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ligneCommandeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LigneCommandeDTO> updateLigneCommande(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LigneCommandeDTO ligneCommandeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LigneCommande : {}, {}", id, ligneCommandeDTO);
        if (ligneCommandeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ligneCommandeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ligneCommandeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ligneCommandeDTO = ligneCommandeService.update(ligneCommandeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ligneCommandeDTO.getId().toString()))
            .body(ligneCommandeDTO);
    }

    /**
     * {@code PATCH  /ligne-commandes/:id} : Partial updates given fields of an existing ligneCommande, field will ignore if it is null
     *
     * @param id the id of the ligneCommandeDTO to save.
     * @param ligneCommandeDTO the ligneCommandeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ligneCommandeDTO,
     * or with status {@code 400 (Bad Request)} if the ligneCommandeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ligneCommandeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ligneCommandeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LigneCommandeDTO> partialUpdateLigneCommande(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LigneCommandeDTO ligneCommandeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LigneCommande partially : {}, {}", id, ligneCommandeDTO);
        if (ligneCommandeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ligneCommandeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ligneCommandeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LigneCommandeDTO> result = ligneCommandeService.partialUpdate(ligneCommandeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ligneCommandeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ligne-commandes} : get all the Ligne Commandes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Ligne Commandes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<LigneCommandeDTO>> getAllLigneCommandes(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of LigneCommandes");
        Page<LigneCommandeDTO> page = ligneCommandeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ligne-commandes/:id} : get the "id" ligneCommande.
     *
     * @param id the id of the ligneCommandeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ligneCommandeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LigneCommandeDTO> getLigneCommande(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LigneCommande : {}", id);
        Optional<LigneCommandeDTO> ligneCommandeDTO = ligneCommandeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ligneCommandeDTO);
    }

    /**
     * {@code DELETE  /ligne-commandes/:id} : delete the "id" ligneCommande.
     *
     * @param id the id of the ligneCommandeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLigneCommande(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LigneCommande : {}", id);
        ligneCommandeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
