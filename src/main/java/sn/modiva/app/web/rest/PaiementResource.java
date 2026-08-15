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
import sn.modiva.app.repository.PaiementRepository;
import sn.modiva.app.service.PaiementService;
import sn.modiva.app.service.dto.PaiementDTO;
import sn.modiva.app.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.modiva.app.domain.Paiement}.
 */
@RestController
@RequestMapping("/api/paiements")
public class PaiementResource {

    private static final Logger LOG = LoggerFactory.getLogger(PaiementResource.class);

    private static final String ENTITY_NAME = "paiement";

    @Value("${jhipster.clientApp.name:modiva}")
    private String applicationName;

    private final PaiementService paiementService;

    private final PaiementRepository paiementRepository;

    public PaiementResource(PaiementService paiementService, PaiementRepository paiementRepository) {
        this.paiementService = paiementService;
        this.paiementRepository = paiementRepository;
    }

    /**
     * {@code POST  /paiements} : Create a new paiement.
     *
     * @param paiementDTO the paiementDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paiementDTO, or with status {@code 400 (Bad Request)} if the paiement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PaiementDTO> createPaiement(@Valid @RequestBody PaiementDTO paiementDTO) throws URISyntaxException {
        LOG.debug("REST request to save Paiement : {}", paiementDTO);
        if (paiementDTO.getId() != null) {
            throw new BadRequestAlertException("A new paiement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        paiementDTO = paiementService.save(paiementDTO);
        return ResponseEntity.created(new URI("/api/paiements/" + paiementDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, paiementDTO.getId().toString()))
            .body(paiementDTO);
    }

    /**
     * {@code PUT  /paiements/:id} : Updates an existing paiement.
     *
     * @param id the id of the paiementDTO to save.
     * @param paiementDTO the paiementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paiementDTO,
     * or with status {@code 400 (Bad Request)} if the paiementDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the paiementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PaiementDTO> updatePaiement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PaiementDTO paiementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Paiement : {}, {}", id, paiementDTO);
        if (paiementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paiementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paiementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        paiementDTO = paiementService.update(paiementDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paiementDTO.getId().toString()))
            .body(paiementDTO);
    }

    /**
     * {@code PATCH  /paiements/:id} : Partial updates given fields of an existing paiement, field will ignore if it is null
     *
     * @param id the id of the paiementDTO to save.
     * @param paiementDTO the paiementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paiementDTO,
     * or with status {@code 400 (Bad Request)} if the paiementDTO is not valid,
     * or with status {@code 404 (Not Found)} if the paiementDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the paiementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PaiementDTO> partialUpdatePaiement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PaiementDTO paiementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Paiement partially : {}, {}", id, paiementDTO);
        if (paiementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paiementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paiementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PaiementDTO> result = paiementService.partialUpdate(paiementDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paiementDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /paiements} : get all the Paiements.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Paiements in body.
     */
    @GetMapping("")
    public List<PaiementDTO> getAllPaiements(@RequestParam(name = "filter", required = false) String filter) {
        if ("commande-is-null".equals(filter)) {
            LOG.debug("REST request to get all Paiements where commande is null");
            return paiementService.findAllWhereCommandeIsNull();
        }
        LOG.debug("REST request to get all Paiements");
        return paiementService.findAll();
    }

    /**
     * {@code GET  /paiements/:id} : get the "id" paiement.
     *
     * @param id the id of the paiementDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paiementDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaiementDTO> getPaiement(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Paiement : {}", id);
        Optional<PaiementDTO> paiementDTO = paiementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(paiementDTO);
    }

    /**
     * {@code DELETE  /paiements/:id} : delete the "id" paiement.
     *
     * @param id the id of the paiementDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaiement(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Paiement : {}", id);
        paiementService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
