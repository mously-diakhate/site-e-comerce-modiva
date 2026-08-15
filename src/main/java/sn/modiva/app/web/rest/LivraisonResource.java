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
import sn.modiva.app.repository.LivraisonRepository;
import sn.modiva.app.service.LivraisonService;
import sn.modiva.app.service.dto.LivraisonDTO;
import sn.modiva.app.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.modiva.app.domain.Livraison}.
 */
@RestController
@RequestMapping("/api/livraisons")
public class LivraisonResource {

    private static final Logger LOG = LoggerFactory.getLogger(LivraisonResource.class);

    private static final String ENTITY_NAME = "livraison";

    @Value("${jhipster.clientApp.name:modiva}")
    private String applicationName;

    private final LivraisonService livraisonService;

    private final LivraisonRepository livraisonRepository;

    public LivraisonResource(LivraisonService livraisonService, LivraisonRepository livraisonRepository) {
        this.livraisonService = livraisonService;
        this.livraisonRepository = livraisonRepository;
    }

    /**
     * {@code POST  /livraisons} : Create a new livraison.
     *
     * @param livraisonDTO the livraisonDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new livraisonDTO, or with status {@code 400 (Bad Request)} if the livraison has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LivraisonDTO> createLivraison(@Valid @RequestBody LivraisonDTO livraisonDTO) throws URISyntaxException {
        LOG.debug("REST request to save Livraison : {}", livraisonDTO);
        if (livraisonDTO.getId() != null) {
            throw new BadRequestAlertException("A new livraison cannot already have an ID", ENTITY_NAME, "idexists");
        }
        livraisonDTO = livraisonService.save(livraisonDTO);
        return ResponseEntity.created(new URI("/api/livraisons/" + livraisonDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, livraisonDTO.getId().toString()))
            .body(livraisonDTO);
    }

    /**
     * {@code PUT  /livraisons/:id} : Updates an existing livraison.
     *
     * @param id the id of the livraisonDTO to save.
     * @param livraisonDTO the livraisonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated livraisonDTO,
     * or with status {@code 400 (Bad Request)} if the livraisonDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the livraisonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LivraisonDTO> updateLivraison(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LivraisonDTO livraisonDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Livraison : {}, {}", id, livraisonDTO);
        if (livraisonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, livraisonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!livraisonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        livraisonDTO = livraisonService.update(livraisonDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, livraisonDTO.getId().toString()))
            .body(livraisonDTO);
    }

    /**
     * {@code PATCH  /livraisons/:id} : Partial updates given fields of an existing livraison, field will ignore if it is null
     *
     * @param id the id of the livraisonDTO to save.
     * @param livraisonDTO the livraisonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated livraisonDTO,
     * or with status {@code 400 (Bad Request)} if the livraisonDTO is not valid,
     * or with status {@code 404 (Not Found)} if the livraisonDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the livraisonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LivraisonDTO> partialUpdateLivraison(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LivraisonDTO livraisonDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Livraison partially : {}, {}", id, livraisonDTO);
        if (livraisonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, livraisonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!livraisonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LivraisonDTO> result = livraisonService.partialUpdate(livraisonDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, livraisonDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /livraisons} : get all the Livraisons.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Livraisons in body.
     */
    @GetMapping("")
    public List<LivraisonDTO> getAllLivraisons(@RequestParam(name = "filter", required = false) String filter) {
        if ("commande-is-null".equals(filter)) {
            LOG.debug("REST request to get all Livraisons where commande is null");
            return livraisonService.findAllWhereCommandeIsNull();
        }
        LOG.debug("REST request to get all Livraisons");
        return livraisonService.findAll();
    }

    /**
     * {@code GET  /livraisons/:id} : get the "id" livraison.
     *
     * @param id the id of the livraisonDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the livraisonDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LivraisonDTO> getLivraison(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Livraison : {}", id);
        Optional<LivraisonDTO> livraisonDTO = livraisonService.findOne(id);
        return ResponseUtil.wrapOrNotFound(livraisonDTO);
    }

    /**
     * {@code DELETE  /livraisons/:id} : delete the "id" livraison.
     *
     * @param id the id of the livraisonDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLivraison(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Livraison : {}", id);
        livraisonService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
