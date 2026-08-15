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
import sn.modiva.app.repository.ClienteProfileRepository;
import sn.modiva.app.service.ClienteProfileQueryService;
import sn.modiva.app.service.ClienteProfileService;
import sn.modiva.app.service.criteria.ClienteProfileCriteria;
import sn.modiva.app.service.dto.ClienteProfileDTO;
import sn.modiva.app.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.modiva.app.domain.ClienteProfile}.
 */
@RestController
@RequestMapping("/api/cliente-profiles")
public class ClienteProfileResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClienteProfileResource.class);

    private static final String ENTITY_NAME = "clienteProfile";

    @Value("${jhipster.clientApp.name:modiva}")
    private String applicationName;

    private final ClienteProfileService clienteProfileService;

    private final ClienteProfileRepository clienteProfileRepository;

    private final ClienteProfileQueryService clienteProfileQueryService;

    public ClienteProfileResource(
        ClienteProfileService clienteProfileService,
        ClienteProfileRepository clienteProfileRepository,
        ClienteProfileQueryService clienteProfileQueryService
    ) {
        this.clienteProfileService = clienteProfileService;
        this.clienteProfileRepository = clienteProfileRepository;
        this.clienteProfileQueryService = clienteProfileQueryService;
    }

    /**
     * {@code POST  /cliente-profiles} : Create a new clienteProfile.
     *
     * @param clienteProfileDTO the clienteProfileDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clienteProfileDTO, or with status {@code 400 (Bad Request)} if the clienteProfile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClienteProfileDTO> createClienteProfile(@Valid @RequestBody ClienteProfileDTO clienteProfileDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ClienteProfile : {}", clienteProfileDTO);
        if (clienteProfileDTO.getId() != null) {
            throw new BadRequestAlertException("A new clienteProfile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        clienteProfileDTO = clienteProfileService.save(clienteProfileDTO);
        return ResponseEntity.created(new URI("/api/cliente-profiles/" + clienteProfileDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, clienteProfileDTO.getId().toString()))
            .body(clienteProfileDTO);
    }

    /**
     * {@code PUT  /cliente-profiles/:id} : Updates an existing clienteProfile.
     *
     * @param id the id of the clienteProfileDTO to save.
     * @param clienteProfileDTO the clienteProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clienteProfileDTO,
     * or with status {@code 400 (Bad Request)} if the clienteProfileDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clienteProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteProfileDTO> updateClienteProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClienteProfileDTO clienteProfileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ClienteProfile : {}, {}", id, clienteProfileDTO);
        if (clienteProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clienteProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clienteProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        clienteProfileDTO = clienteProfileService.update(clienteProfileDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clienteProfileDTO.getId().toString()))
            .body(clienteProfileDTO);
    }

    /**
     * {@code PATCH  /cliente-profiles/:id} : Partial updates given fields of an existing clienteProfile, field will ignore if it is null
     *
     * @param id the id of the clienteProfileDTO to save.
     * @param clienteProfileDTO the clienteProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clienteProfileDTO,
     * or with status {@code 400 (Bad Request)} if the clienteProfileDTO is not valid,
     * or with status {@code 404 (Not Found)} if the clienteProfileDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the clienteProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClienteProfileDTO> partialUpdateClienteProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClienteProfileDTO clienteProfileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ClienteProfile partially : {}, {}", id, clienteProfileDTO);
        if (clienteProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clienteProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clienteProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClienteProfileDTO> result = clienteProfileService.partialUpdate(clienteProfileDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clienteProfileDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cliente-profiles} : get all the Cliente Profiles.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Cliente Profiles in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ClienteProfileDTO>> getAllClienteProfiles(
        ClienteProfileCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ClienteProfiles by criteria: {}", criteria);

        Page<ClienteProfileDTO> page = clienteProfileQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cliente-profiles/count} : count all the clienteProfiles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countClienteProfiles(ClienteProfileCriteria criteria) {
        LOG.debug("REST request to count ClienteProfiles by criteria: {}", criteria);
        return ResponseEntity.ok().body(clienteProfileQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /cliente-profiles/:id} : get the "id" clienteProfile.
     *
     * @param id the id of the clienteProfileDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clienteProfileDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteProfileDTO> getClienteProfile(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClienteProfile : {}", id);
        Optional<ClienteProfileDTO> clienteProfileDTO = clienteProfileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clienteProfileDTO);
    }

    /**
     * {@code DELETE  /cliente-profiles/:id} : delete the "id" clienteProfile.
     *
     * @param id the id of the clienteProfileDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClienteProfile(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClienteProfile : {}", id);
        clienteProfileService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
