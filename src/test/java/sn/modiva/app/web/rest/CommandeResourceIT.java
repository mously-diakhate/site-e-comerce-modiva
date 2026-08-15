package sn.modiva.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.modiva.app.domain.CommandeAsserts.*;
import static sn.modiva.app.web.rest.TestUtil.createUpdateProxyForBean;
import static sn.modiva.app.web.rest.TestUtil.sameNumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sn.modiva.app.IntegrationTest;
import sn.modiva.app.domain.ClienteProfile;
import sn.modiva.app.domain.Commande;
import sn.modiva.app.domain.Livraison;
import sn.modiva.app.domain.Paiement;
import sn.modiva.app.domain.enumeration.StatutCommande;
import sn.modiva.app.repository.CommandeRepository;
import sn.modiva.app.service.dto.CommandeDTO;
import sn.modiva.app.service.mapper.CommandeMapper;

/**
 * Integration tests for the {@link CommandeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CommandeResourceIT {

    private static final String DEFAULT_NUMERO = "AAAAAAAAAA";
    private static final String UPDATED_NUMERO = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_COMMANDE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_COMMANDE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_MONTANT_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_MONTANT_TOTAL = new BigDecimal(2);
    private static final BigDecimal SMALLER_MONTANT_TOTAL = new BigDecimal(1 - 1);

    private static final String DEFAULT_MODE_PAIEMENT = "AAAAAAAAAA";
    private static final String UPDATED_MODE_PAIEMENT = "BBBBBBBBBB";

    private static final StatutCommande DEFAULT_STATUT_COMMANDE = StatutCommande.EN_ATTENTE;
    private static final StatutCommande UPDATED_STATUT_COMMANDE = StatutCommande.CONFIRMEE;

    private static final String ENTITY_API_URL = "/api/commandes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private CommandeMapper commandeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCommandeMockMvc;

    private Commande commande;

    private Commande insertedCommande;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Commande createEntity(EntityManager em) {
        Commande commande = new Commande()
            .numero(DEFAULT_NUMERO)
            .dateCommande(DEFAULT_DATE_COMMANDE)
            .montantTotal(DEFAULT_MONTANT_TOTAL)
            .modePaiement(DEFAULT_MODE_PAIEMENT)
            .statutCommande(DEFAULT_STATUT_COMMANDE);
        // Add required entity
        Livraison livraison;
        if (TestUtil.findAll(em, Livraison.class).isEmpty()) {
            livraison = LivraisonResourceIT.createEntity();
            em.persist(livraison);
            em.flush();
        } else {
            livraison = TestUtil.findAll(em, Livraison.class).get(0);
        }
        commande.setLivraison(livraison);
        // Add required entity
        ClienteProfile clienteProfile;
        if (TestUtil.findAll(em, ClienteProfile.class).isEmpty()) {
            clienteProfile = ClienteProfileResourceIT.createEntity(em);
            em.persist(clienteProfile);
            em.flush();
        } else {
            clienteProfile = TestUtil.findAll(em, ClienteProfile.class).get(0);
        }
        commande.setClienteProfile(clienteProfile);
        return commande;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Commande createUpdatedEntity(EntityManager em) {
        Commande updatedCommande = new Commande()
            .numero(UPDATED_NUMERO)
            .dateCommande(UPDATED_DATE_COMMANDE)
            .montantTotal(UPDATED_MONTANT_TOTAL)
            .modePaiement(UPDATED_MODE_PAIEMENT)
            .statutCommande(UPDATED_STATUT_COMMANDE);
        // Add required entity
        Livraison livraison;
        if (TestUtil.findAll(em, Livraison.class).isEmpty()) {
            livraison = LivraisonResourceIT.createUpdatedEntity();
            em.persist(livraison);
            em.flush();
        } else {
            livraison = TestUtil.findAll(em, Livraison.class).get(0);
        }
        updatedCommande.setLivraison(livraison);
        // Add required entity
        ClienteProfile clienteProfile;
        if (TestUtil.findAll(em, ClienteProfile.class).isEmpty()) {
            clienteProfile = ClienteProfileResourceIT.createUpdatedEntity(em);
            em.persist(clienteProfile);
            em.flush();
        } else {
            clienteProfile = TestUtil.findAll(em, ClienteProfile.class).get(0);
        }
        updatedCommande.setClienteProfile(clienteProfile);
        return updatedCommande;
    }

    @BeforeEach
    void initTest() {
        commande = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCommande != null) {
            commandeRepository.delete(insertedCommande);
            insertedCommande = null;
        }
    }

    @Test
    @Transactional
    void createCommande() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Commande
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);
        var returnedCommandeDTO = om.readValue(
            restCommandeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(commandeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CommandeDTO.class
        );

        // Validate the Commande in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCommande = commandeMapper.toEntity(returnedCommandeDTO);
        assertCommandeUpdatableFieldsEquals(returnedCommande, getPersistedCommande(returnedCommande));

        insertedCommande = returnedCommande;
    }

    @Test
    @Transactional
    void createCommandeWithExistingId() throws Exception {
        // Create the Commande with an existing ID
        commande.setId(1L);
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(commandeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Commande in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNumeroIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        commande.setNumero(null);

        // Create the Commande, which fails.
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);

        restCommandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(commandeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateCommandeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        commande.setDateCommande(null);

        // Create the Commande, which fails.
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);

        restCommandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(commandeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMontantTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        commande.setMontantTotal(null);

        // Create the Commande, which fails.
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);

        restCommandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(commandeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutCommandeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        commande.setStatutCommande(null);

        // Create the Commande, which fails.
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);

        restCommandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(commandeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCommandes() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList
        restCommandeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commande.getId().intValue())))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO)))
            .andExpect(jsonPath("$.[*].dateCommande").value(hasItem(DEFAULT_DATE_COMMANDE.toString())))
            .andExpect(jsonPath("$.[*].montantTotal").value(hasItem(sameNumber(DEFAULT_MONTANT_TOTAL))))
            .andExpect(jsonPath("$.[*].modePaiement").value(hasItem(DEFAULT_MODE_PAIEMENT)))
            .andExpect(jsonPath("$.[*].statutCommande").value(hasItem(DEFAULT_STATUT_COMMANDE.toString())));
    }

    @Test
    @Transactional
    void getCommande() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get the commande
        restCommandeMockMvc
            .perform(get(ENTITY_API_URL_ID, commande.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(commande.getId().intValue()))
            .andExpect(jsonPath("$.numero").value(DEFAULT_NUMERO))
            .andExpect(jsonPath("$.dateCommande").value(DEFAULT_DATE_COMMANDE.toString()))
            .andExpect(jsonPath("$.montantTotal").value(sameNumber(DEFAULT_MONTANT_TOTAL)))
            .andExpect(jsonPath("$.modePaiement").value(DEFAULT_MODE_PAIEMENT))
            .andExpect(jsonPath("$.statutCommande").value(DEFAULT_STATUT_COMMANDE.toString()));
    }

    @Test
    @Transactional
    void getCommandesByIdFiltering() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        Long id = commande.getId();

        defaultCommandeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCommandeFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCommandeFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCommandesByNumeroIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where numero equals to
        defaultCommandeFiltering("numero.equals=" + DEFAULT_NUMERO, "numero.equals=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllCommandesByNumeroIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where numero in
        defaultCommandeFiltering("numero.in=" + DEFAULT_NUMERO + "," + UPDATED_NUMERO, "numero.in=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllCommandesByNumeroIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where numero is not null
        defaultCommandeFiltering("numero.specified=true", "numero.specified=false");
    }

    @Test
    @Transactional
    void getAllCommandesByNumeroContainsSomething() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where numero contains
        defaultCommandeFiltering("numero.contains=" + DEFAULT_NUMERO, "numero.contains=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllCommandesByNumeroNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where numero does not contain
        defaultCommandeFiltering("numero.doesNotContain=" + UPDATED_NUMERO, "numero.doesNotContain=" + DEFAULT_NUMERO);
    }

    @Test
    @Transactional
    void getAllCommandesByDateCommandeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where dateCommande equals to
        defaultCommandeFiltering("dateCommande.equals=" + DEFAULT_DATE_COMMANDE, "dateCommande.equals=" + UPDATED_DATE_COMMANDE);
    }

    @Test
    @Transactional
    void getAllCommandesByDateCommandeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where dateCommande in
        defaultCommandeFiltering(
            "dateCommande.in=" + DEFAULT_DATE_COMMANDE + "," + UPDATED_DATE_COMMANDE,
            "dateCommande.in=" + UPDATED_DATE_COMMANDE
        );
    }

    @Test
    @Transactional
    void getAllCommandesByDateCommandeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where dateCommande is not null
        defaultCommandeFiltering("dateCommande.specified=true", "dateCommande.specified=false");
    }

    @Test
    @Transactional
    void getAllCommandesByMontantTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where montantTotal equals to
        defaultCommandeFiltering("montantTotal.equals=" + DEFAULT_MONTANT_TOTAL, "montantTotal.equals=" + UPDATED_MONTANT_TOTAL);
    }

    @Test
    @Transactional
    void getAllCommandesByMontantTotalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where montantTotal in
        defaultCommandeFiltering(
            "montantTotal.in=" + DEFAULT_MONTANT_TOTAL + "," + UPDATED_MONTANT_TOTAL,
            "montantTotal.in=" + UPDATED_MONTANT_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllCommandesByMontantTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where montantTotal is not null
        defaultCommandeFiltering("montantTotal.specified=true", "montantTotal.specified=false");
    }

    @Test
    @Transactional
    void getAllCommandesByMontantTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where montantTotal is greater than or equal to
        defaultCommandeFiltering(
            "montantTotal.greaterThanOrEqual=" + DEFAULT_MONTANT_TOTAL,
            "montantTotal.greaterThanOrEqual=" + UPDATED_MONTANT_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllCommandesByMontantTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where montantTotal is less than or equal to
        defaultCommandeFiltering(
            "montantTotal.lessThanOrEqual=" + DEFAULT_MONTANT_TOTAL,
            "montantTotal.lessThanOrEqual=" + SMALLER_MONTANT_TOTAL
        );
    }

    @Test
    @Transactional
    void getAllCommandesByMontantTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where montantTotal is less than
        defaultCommandeFiltering("montantTotal.lessThan=" + UPDATED_MONTANT_TOTAL, "montantTotal.lessThan=" + DEFAULT_MONTANT_TOTAL);
    }

    @Test
    @Transactional
    void getAllCommandesByMontantTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where montantTotal is greater than
        defaultCommandeFiltering("montantTotal.greaterThan=" + SMALLER_MONTANT_TOTAL, "montantTotal.greaterThan=" + DEFAULT_MONTANT_TOTAL);
    }

    @Test
    @Transactional
    void getAllCommandesByModePaiementIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where modePaiement equals to
        defaultCommandeFiltering("modePaiement.equals=" + DEFAULT_MODE_PAIEMENT, "modePaiement.equals=" + UPDATED_MODE_PAIEMENT);
    }

    @Test
    @Transactional
    void getAllCommandesByModePaiementIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where modePaiement in
        defaultCommandeFiltering(
            "modePaiement.in=" + DEFAULT_MODE_PAIEMENT + "," + UPDATED_MODE_PAIEMENT,
            "modePaiement.in=" + UPDATED_MODE_PAIEMENT
        );
    }

    @Test
    @Transactional
    void getAllCommandesByModePaiementIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where modePaiement is not null
        defaultCommandeFiltering("modePaiement.specified=true", "modePaiement.specified=false");
    }

    @Test
    @Transactional
    void getAllCommandesByModePaiementContainsSomething() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where modePaiement contains
        defaultCommandeFiltering("modePaiement.contains=" + DEFAULT_MODE_PAIEMENT, "modePaiement.contains=" + UPDATED_MODE_PAIEMENT);
    }

    @Test
    @Transactional
    void getAllCommandesByModePaiementNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where modePaiement does not contain
        defaultCommandeFiltering(
            "modePaiement.doesNotContain=" + UPDATED_MODE_PAIEMENT,
            "modePaiement.doesNotContain=" + DEFAULT_MODE_PAIEMENT
        );
    }

    @Test
    @Transactional
    void getAllCommandesByStatutCommandeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where statutCommande equals to
        defaultCommandeFiltering("statutCommande.equals=" + DEFAULT_STATUT_COMMANDE, "statutCommande.equals=" + UPDATED_STATUT_COMMANDE);
    }

    @Test
    @Transactional
    void getAllCommandesByStatutCommandeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where statutCommande in
        defaultCommandeFiltering(
            "statutCommande.in=" + DEFAULT_STATUT_COMMANDE + "," + UPDATED_STATUT_COMMANDE,
            "statutCommande.in=" + UPDATED_STATUT_COMMANDE
        );
    }

    @Test
    @Transactional
    void getAllCommandesByStatutCommandeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where statutCommande is not null
        defaultCommandeFiltering("statutCommande.specified=true", "statutCommande.specified=false");
    }

    @Test
    @Transactional
    void getAllCommandesByPaiementIsEqualToSomething() throws Exception {
        Paiement paiement;
        if (TestUtil.findAll(em, Paiement.class).isEmpty()) {
            commandeRepository.saveAndFlush(commande);
            paiement = PaiementResourceIT.createEntity();
        } else {
            paiement = TestUtil.findAll(em, Paiement.class).get(0);
        }
        em.persist(paiement);
        em.flush();
        commande.setPaiement(paiement);
        commandeRepository.saveAndFlush(commande);
        Long paiementId = paiement.getId();
        // Get all the commandeList where paiement equals to paiementId
        defaultCommandeShouldBeFound("paiementId.equals=" + paiementId);

        // Get all the commandeList where paiement equals to (paiementId + 1)
        defaultCommandeShouldNotBeFound("paiementId.equals=" + (paiementId + 1));
    }

    @Test
    @Transactional
    void getAllCommandesByLivraisonIsEqualToSomething() throws Exception {
        // Get already existing entity
        Livraison livraison = commande.getLivraison();
        commandeRepository.saveAndFlush(commande);
        Long livraisonId = livraison.getId();
        // Get all the commandeList where livraison equals to livraisonId
        defaultCommandeShouldBeFound("livraisonId.equals=" + livraisonId);

        // Get all the commandeList where livraison equals to (livraisonId + 1)
        defaultCommandeShouldNotBeFound("livraisonId.equals=" + (livraisonId + 1));
    }

    @Test
    @Transactional
    void getAllCommandesByClienteProfileIsEqualToSomething() throws Exception {
        ClienteProfile clienteProfile;
        if (TestUtil.findAll(em, ClienteProfile.class).isEmpty()) {
            commandeRepository.saveAndFlush(commande);
            clienteProfile = ClienteProfileResourceIT.createEntity(em);
        } else {
            clienteProfile = TestUtil.findAll(em, ClienteProfile.class).get(0);
        }
        em.persist(clienteProfile);
        em.flush();
        commande.setClienteProfile(clienteProfile);
        commandeRepository.saveAndFlush(commande);
        Long clienteProfileId = clienteProfile.getId();
        // Get all the commandeList where clienteProfile equals to clienteProfileId
        defaultCommandeShouldBeFound("clienteProfileId.equals=" + clienteProfileId);

        // Get all the commandeList where clienteProfile equals to (clienteProfileId + 1)
        defaultCommandeShouldNotBeFound("clienteProfileId.equals=" + (clienteProfileId + 1));
    }

    private void defaultCommandeFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCommandeShouldBeFound(shouldBeFound);
        defaultCommandeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCommandeShouldBeFound(String filter) throws Exception {
        restCommandeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commande.getId().intValue())))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO)))
            .andExpect(jsonPath("$.[*].dateCommande").value(hasItem(DEFAULT_DATE_COMMANDE.toString())))
            .andExpect(jsonPath("$.[*].montantTotal").value(hasItem(sameNumber(DEFAULT_MONTANT_TOTAL))))
            .andExpect(jsonPath("$.[*].modePaiement").value(hasItem(DEFAULT_MODE_PAIEMENT)))
            .andExpect(jsonPath("$.[*].statutCommande").value(hasItem(DEFAULT_STATUT_COMMANDE.toString())));

        // Check, that the count call also returns 1
        restCommandeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCommandeShouldNotBeFound(String filter) throws Exception {
        restCommandeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCommandeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCommande() throws Exception {
        // Get the commande
        restCommandeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCommande() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the commande
        Commande updatedCommande = commandeRepository.findById(commande.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCommande are not directly saved in db
        em.detach(updatedCommande);
        updatedCommande
            .numero(UPDATED_NUMERO)
            .dateCommande(UPDATED_DATE_COMMANDE)
            .montantTotal(UPDATED_MONTANT_TOTAL)
            .modePaiement(UPDATED_MODE_PAIEMENT)
            .statutCommande(UPDATED_STATUT_COMMANDE);
        CommandeDTO commandeDTO = commandeMapper.toDto(updatedCommande);

        restCommandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, commandeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(commandeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Commande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCommandeToMatchAllProperties(updatedCommande);
    }

    @Test
    @Transactional
    void putNonExistingCommande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        commande.setId(longCount.incrementAndGet());

        // Create the Commande
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, commandeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(commandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Commande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCommande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        commande.setId(longCount.incrementAndGet());

        // Create the Commande
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(commandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Commande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCommande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        commande.setId(longCount.incrementAndGet());

        // Create the Commande
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(commandeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Commande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCommandeWithPatch() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the commande using partial update
        Commande partialUpdatedCommande = new Commande();
        partialUpdatedCommande.setId(commande.getId());

        partialUpdatedCommande.statutCommande(UPDATED_STATUT_COMMANDE);

        restCommandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommande.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCommande))
            )
            .andExpect(status().isOk());

        // Validate the Commande in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCommandeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCommande, commande), getPersistedCommande(commande));
    }

    @Test
    @Transactional
    void fullUpdateCommandeWithPatch() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the commande using partial update
        Commande partialUpdatedCommande = new Commande();
        partialUpdatedCommande.setId(commande.getId());

        partialUpdatedCommande
            .numero(UPDATED_NUMERO)
            .dateCommande(UPDATED_DATE_COMMANDE)
            .montantTotal(UPDATED_MONTANT_TOTAL)
            .modePaiement(UPDATED_MODE_PAIEMENT)
            .statutCommande(UPDATED_STATUT_COMMANDE);

        restCommandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommande.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCommande))
            )
            .andExpect(status().isOk());

        // Validate the Commande in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCommandeUpdatableFieldsEquals(partialUpdatedCommande, getPersistedCommande(partialUpdatedCommande));
    }

    @Test
    @Transactional
    void patchNonExistingCommande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        commande.setId(longCount.incrementAndGet());

        // Create the Commande
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, commandeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(commandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Commande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCommande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        commande.setId(longCount.incrementAndGet());

        // Create the Commande
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(commandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Commande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCommande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        commande.setId(longCount.incrementAndGet());

        // Create the Commande
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(commandeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Commande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCommande() throws Exception {
        // Initialize the database
        insertedCommande = commandeRepository.saveAndFlush(commande);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the commande
        restCommandeMockMvc
            .perform(delete(ENTITY_API_URL_ID, commande.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return commandeRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Commande getPersistedCommande(Commande commande) {
        return commandeRepository.findById(commande.getId()).orElseThrow();
    }

    protected void assertPersistedCommandeToMatchAllProperties(Commande expectedCommande) {
        assertCommandeAllPropertiesEquals(expectedCommande, getPersistedCommande(expectedCommande));
    }

    protected void assertPersistedCommandeToMatchUpdatableProperties(Commande expectedCommande) {
        assertCommandeAllUpdatablePropertiesEquals(expectedCommande, getPersistedCommande(expectedCommande));
    }
}
