package sn.modiva.app.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.modiva.app.domain.Panier;
import sn.modiva.app.repository.PanierRepository;
import sn.modiva.app.security.SecurityUtils;
import sn.modiva.app.service.MonPanierService;
import sn.modiva.app.service.PanierService;
import sn.modiva.app.service.dto.PanierDTO;
import sn.modiva.app.service.mapper.PanierMapper;
import sn.modiva.app.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.modiva.app.domain.Panier}.
 */
@RestController
@RequestMapping("/api/paniers")
public class PanierResource {

    private static final Logger LOG = LoggerFactory.getLogger(PanierResource.class);

    private static final String ENTITY_NAME = "panier";

    @Value("${jhipster.clientApp.name:modiva}")
    private String applicationName;

    private final PanierService panierService;

    private final PanierRepository panierRepository;

    private final MonPanierService monPanierService;

    private final PanierMapper panierMapper;

    public PanierResource(
        PanierService panierService,
        PanierRepository panierRepository,
        MonPanierService monPanierService,
        PanierMapper panierMapper
    ) {
        this.panierService = panierService;
        this.panierRepository = panierRepository;
        this.monPanierService = monPanierService;
        this.panierMapper = panierMapper;
    }

    /**
     * {@code POST  /paniers} : Create a new panier. (Réservé à l'admin — voir SecurityConfiguration.)
     */
    @PostMapping("")
    public ResponseEntity<PanierDTO> createPanier(@RequestBody PanierDTO panierDTO) throws URISyntaxException {
        LOG.debug("REST request to save Panier : {}", panierDTO);
        if (panierDTO.getId() != null) {
            throw new BadRequestAlertException("A new panier cannot already have an ID", ENTITY_NAME, "idexists");
        }
        panierDTO = panierService.save(panierDTO);
        return ResponseEntity.created(new URI("/api/paniers/" + panierDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, panierDTO.getId().toString()))
            .body(panierDTO);
    }

    /**
     * {@code PUT  /paniers/:id} : Updates an existing panier.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PanierDTO> updatePanier(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PanierDTO panierDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Panier : {}, {}", id, panierDTO);
        if (panierDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, panierDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!panierRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        panierDTO = panierService.update(panierDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, panierDTO.getId().toString()))
            .body(panierDTO);
    }

    /**
     * {@code PATCH  /paniers/:id} : Partial updates given fields of an existing panier, field will ignore if it is null
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PanierDTO> partialUpdatePanier(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PanierDTO panierDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Panier partially : {}, {}", id, panierDTO);
        if (panierDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, panierDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!panierRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PanierDTO> result = panierService.partialUpdate(panierDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, panierDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /paniers/me} : renvoie le Panier de l'utilisatrice connectée, en le créant
     * (ainsi que son ClienteProfile) s'il n'existe pas encore.
     *
     * Self-service : la résolution se fait uniquement via le login du token JWT.
     * Grâce au "get-or-create", une cliente qui vient de s'inscrire a tout de suite un
     * panier serveur, donc le retrouve sur n'importe quel navigateur ou appareil.
     */
    @GetMapping("/me")
    public ResponseEntity<PanierDTO> getMyPanier() {
        LOG.debug("REST request to get, or create, the current user's own Panier");
        Optional<String> login = SecurityUtils.getCurrentUserLogin();
        if (login.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Panier panier = monPanierService.getOrCreatePanier(login.get());
        return ResponseEntity.ok(panierMapper.toDto(panier));
    }

    /**
     * {@code GET  /paniers} : get all the Paniers. (Admin uniquement.)
     */
    @GetMapping("")
    public List<PanierDTO> getAllPaniers(@RequestParam(name = "filter", required = false) String filter) {
        if ("clienteprofile-is-null".equals(filter)) {
            LOG.debug("REST request to get all Paniers where clienteProfile is null");
            return panierService.findAllWhereClienteProfileIsNull();
        }
        LOG.debug("REST request to get all Paniers");
        return panierService.findAll();
    }

    /**
     * {@code GET  /paniers/:id} : get the "id" panier. (Admin uniquement.)
     */
    @GetMapping("/{id}")
    public ResponseEntity<PanierDTO> getPanier(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Panier : {}", id);
        Optional<PanierDTO> panierDTO = panierService.findOne(id);
        return ResponseUtil.wrapOrNotFound(panierDTO);
    }

    /**
     * {@code DELETE  /paniers/:id} : delete the "id" panier. (Admin uniquement.)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePanier(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Panier : {}", id);
        panierService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
