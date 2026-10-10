package sn.modiva.app.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.modiva.app.domain.Commande;
import sn.modiva.app.domain.LigneCommande;
import sn.modiva.app.domain.Livraison;
import sn.modiva.app.domain.Produit;
import sn.modiva.app.domain.VarianteProduit;
import sn.modiva.app.repository.CommandeRepository;
import sn.modiva.app.security.SecurityUtils;
import sn.modiva.app.service.CommandeQueryService;
import sn.modiva.app.service.CommandeService;
import sn.modiva.app.service.criteria.CommandeCriteria;
import sn.modiva.app.service.dto.CommandeDTO;
import sn.modiva.app.service.dto.MaCommandeDTO;
import sn.modiva.app.service.mapper.CommandeMapper;
import sn.modiva.app.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.modiva.app.domain.Commande}.
 */
@RestController
@RequestMapping("/api/commandes")
public class CommandeResource {

    private static final Logger LOG = LoggerFactory.getLogger(CommandeResource.class);

    private static final String ENTITY_NAME = "commande";

    @Value("${jhipster.clientApp.name:modiva}")
    private String applicationName;

    private final CommandeService commandeService;

    private final CommandeRepository commandeRepository;

    private final CommandeQueryService commandeQueryService;

    private final CommandeMapper commandeMapper;

    public CommandeResource(
        CommandeService commandeService,
        CommandeRepository commandeRepository,
        CommandeQueryService commandeQueryService,
        CommandeMapper commandeMapper
    ) {
        this.commandeService = commandeService;
        this.commandeRepository = commandeRepository;
        this.commandeQueryService = commandeQueryService;
        this.commandeMapper = commandeMapper;
    }

    /**
     * {@code POST  /commandes} : Create a new commande.
     *
     * @param commandeDTO the commandeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new commandeDTO, or with status {@code 400 (Bad Request)} if the commande has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CommandeDTO> createCommande(@Valid @RequestBody CommandeDTO commandeDTO) throws URISyntaxException {
        LOG.debug("REST request to save Commande : {}", commandeDTO);
        if (commandeDTO.getId() != null) {
            throw new BadRequestAlertException("A new commande cannot already have an ID", ENTITY_NAME, "idexists");
        }
        commandeDTO = commandeService.save(commandeDTO);
        return ResponseEntity.created(new URI("/api/commandes/" + commandeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, commandeDTO.getId().toString()))
            .body(commandeDTO);
    }

    /**
     * {@code PUT  /commandes/:id} : Updates an existing commande.
     *
     * @param id the id of the commandeDTO to save.
     * @param commandeDTO the commandeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commandeDTO,
     * or with status {@code 400 (Bad Request)} if the commandeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the commandeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CommandeDTO> updateCommande(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CommandeDTO commandeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Commande : {}, {}", id, commandeDTO);
        if (commandeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commandeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!commandeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        commandeDTO = commandeService.update(commandeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, commandeDTO.getId().toString()))
            .body(commandeDTO);
    }

    /**
     * {@code PATCH  /commandes/:id} : Partial updates given fields of an existing commande, field will ignore if it is null
     *
     * @param id the id of the commandeDTO to save.
     * @param commandeDTO the commandeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commandeDTO,
     * or with status {@code 400 (Bad Request)} if the commandeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the commandeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the commandeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CommandeDTO> partialUpdateCommande(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CommandeDTO commandeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Commande partially : {}, {}", id, commandeDTO);
        if (commandeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commandeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!commandeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CommandeDTO> result = commandeService.partialUpdate(commandeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, commandeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /commandes/mes-commandes} : get the commandes belonging to
     * the currently authenticated user.
     *
     * Self-service : la résolution se fait uniquement via le login présent
     * dans le token JWT, jamais via un paramètre fourni par le client. Une
     * cliente ne peut donc jamais consulter les commandes d'une autre via cet
     * endpoint — contrairement à GET /commandes, réservé à l'admin.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of the current user's Commandes in body.
     */
    @GetMapping("/mes-commandes")
    @Transactional(readOnly = true)
    public ResponseEntity<List<MaCommandeDTO>> getMesCommandes(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get the current user's own Commandes");
        Optional<String> login = SecurityUtils.getCurrentUserLogin();
        if (login.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Page<Commande> page = commandeRepository.findAllByClienteProfile_User_Login(login.get(), pageable);
        List<MaCommandeDTO> content = page.getContent().stream().map(this::toMaCommandeDTO).toList();
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(content);
    }

    private MaCommandeDTO toMaCommandeDTO(Commande commande) {
        Livraison l = commande.getLivraison();
        MaCommandeDTO.LivraisonInfo livraison =
            l == null
                ? null
                : new MaCommandeDTO.LivraisonInfo(
                      l.getAdresse(),
                      l.getVille(),
                      l.getQuartier(),
                      l.getTelephone(),
                      l.getFraisLivraison(),
                      l.getStatutLivraison()
                  );

        List<MaCommandeDTO.Ligne> lignes = commande
            .getLignes()
            .stream()
            .map((LigneCommande lc) -> {
                VarianteProduit v = lc.getVariante();
                Produit p = v.getProduit();
                return new MaCommandeDTO.Ligne(
                    lc.getId(),
                    p.getId(),
                    p.getNom(),
                    v.getTaille().getLibelle(),
                    v.getCouleur().getLibelle(),
                    lc.getQuantite(),
                    lc.getPrixUnitaire()
                );
            })
            .sorted(Comparator.comparing(MaCommandeDTO.Ligne::id))
            .toList();

        return new MaCommandeDTO(
            commande.getId(),
            commande.getNumero(),
            commande.getDateCommande(),
            commande.getMontantTotal(),
            commande.getModePaiement(),
            commande.getStatutCommande(),
            livraison,
            lignes
        );
    }

    /**
     * {@code GET  /commandes} : get all the Commandes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Commandes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CommandeDTO>> getAllCommandes(
        CommandeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Commandes by criteria: {}", criteria);

        Page<CommandeDTO> page = commandeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /commandes/count} : count all the commandes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCommandes(CommandeCriteria criteria) {
        LOG.debug("REST request to count Commandes by criteria: {}", criteria);
        return ResponseEntity.ok().body(commandeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /commandes/:id} : get the "id" commande.
     *
     * @param id the id of the commandeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the commandeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommandeDTO> getCommande(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Commande : {}", id);
        Optional<CommandeDTO> commandeDTO = commandeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(commandeDTO);
    }

    /**
     * {@code DELETE  /commandes/:id} : delete the "id" commande.
     *
     * @param id the id of the commandeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommande(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Commande : {}", id);
        commandeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
