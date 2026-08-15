package sn.modiva.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.modiva.app.domain.ProduitAsserts.*;
import static sn.modiva.app.web.rest.TestUtil.createUpdateProxyForBean;
import static sn.modiva.app.web.rest.TestUtil.sameNumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sn.modiva.app.IntegrationTest;
import sn.modiva.app.domain.Categorie;
import sn.modiva.app.domain.Produit;
import sn.modiva.app.domain.Promotion;
import sn.modiva.app.domain.enumeration.StatutProduit;
import sn.modiva.app.repository.ProduitRepository;
import sn.modiva.app.service.ProduitService;
import sn.modiva.app.service.dto.ProduitDTO;
import sn.modiva.app.service.mapper.ProduitMapper;

/**
 * Integration tests for the {@link ProduitResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProduitResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRIX = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRIX = new BigDecimal(1);
    private static final BigDecimal SMALLER_PRIX = new BigDecimal(0 - 1);

    private static final StatutProduit DEFAULT_STATUT = StatutProduit.ACTIF;
    private static final StatutProduit UPDATED_STATUT = StatutProduit.INACTIF;

    private static final String ENTITY_API_URL = "/api/produits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProduitRepository produitRepository;

    @Mock
    private ProduitRepository produitRepositoryMock;

    @Autowired
    private ProduitMapper produitMapper;

    @Mock
    private ProduitService produitServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProduitMockMvc;

    private Produit produit;

    private Produit insertedProduit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produit createEntity(EntityManager em) {
        Produit produit = new Produit().nom(DEFAULT_NOM).description(DEFAULT_DESCRIPTION).prix(DEFAULT_PRIX).statut(DEFAULT_STATUT);
        // Add required entity
        Categorie categorie;
        if (TestUtil.findAll(em, Categorie.class).isEmpty()) {
            categorie = CategorieResourceIT.createEntity();
            em.persist(categorie);
            em.flush();
        } else {
            categorie = TestUtil.findAll(em, Categorie.class).get(0);
        }
        produit.setCategorie(categorie);
        return produit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produit createUpdatedEntity(EntityManager em) {
        Produit updatedProduit = new Produit().nom(UPDATED_NOM).description(UPDATED_DESCRIPTION).prix(UPDATED_PRIX).statut(UPDATED_STATUT);
        // Add required entity
        Categorie categorie;
        if (TestUtil.findAll(em, Categorie.class).isEmpty()) {
            categorie = CategorieResourceIT.createUpdatedEntity();
            em.persist(categorie);
            em.flush();
        } else {
            categorie = TestUtil.findAll(em, Categorie.class).get(0);
        }
        updatedProduit.setCategorie(categorie);
        return updatedProduit;
    }

    @BeforeEach
    void initTest() {
        produit = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedProduit != null) {
            produitRepository.delete(insertedProduit);
            insertedProduit = null;
        }
    }

    @Test
    @Transactional
    void createProduit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);
        var returnedProduitDTO = om.readValue(
            restProduitMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produitDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProduitDTO.class
        );

        // Validate the Produit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProduit = produitMapper.toEntity(returnedProduitDTO);
        assertProduitUpdatableFieldsEquals(returnedProduit, getPersistedProduit(returnedProduit));

        insertedProduit = returnedProduit;
    }

    @Test
    @Transactional
    void createProduitWithExistingId() throws Exception {
        // Create the Produit with an existing ID
        produit.setId(1L);
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        produit.setNom(null);

        // Create the Produit, which fails.
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        restProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrixIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        produit.setPrix(null);

        // Create the Produit, which fails.
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        restProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        produit.setStatut(null);

        // Create the Produit, which fails.
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        restProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProduits() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList
        restProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(produit.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].prix").value(hasItem(sameNumber(DEFAULT_PRIX))))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProduitsWithEagerRelationshipsIsEnabled() throws Exception {
        when(produitServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProduitMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(produitServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProduitsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(produitServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProduitMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(produitRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProduit() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get the produit
        restProduitMockMvc
            .perform(get(ENTITY_API_URL_ID, produit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(produit.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.prix").value(sameNumber(DEFAULT_PRIX)))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()));
    }

    @Test
    @Transactional
    void getProduitsByIdFiltering() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        Long id = produit.getId();

        defaultProduitFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProduitFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProduitFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProduitsByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where nom equals to
        defaultProduitFiltering("nom.equals=" + DEFAULT_NOM, "nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllProduitsByNomIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where nom in
        defaultProduitFiltering("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM, "nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllProduitsByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where nom is not null
        defaultProduitFiltering("nom.specified=true", "nom.specified=false");
    }

    @Test
    @Transactional
    void getAllProduitsByNomContainsSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where nom contains
        defaultProduitFiltering("nom.contains=" + DEFAULT_NOM, "nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllProduitsByNomNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where nom does not contain
        defaultProduitFiltering("nom.doesNotContain=" + UPDATED_NOM, "nom.doesNotContain=" + DEFAULT_NOM);
    }

    @Test
    @Transactional
    void getAllProduitsByPrixIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where prix equals to
        defaultProduitFiltering("prix.equals=" + DEFAULT_PRIX, "prix.equals=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    void getAllProduitsByPrixIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where prix in
        defaultProduitFiltering("prix.in=" + DEFAULT_PRIX + "," + UPDATED_PRIX, "prix.in=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    void getAllProduitsByPrixIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where prix is not null
        defaultProduitFiltering("prix.specified=true", "prix.specified=false");
    }

    @Test
    @Transactional
    void getAllProduitsByPrixIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where prix is greater than or equal to
        defaultProduitFiltering("prix.greaterThanOrEqual=" + DEFAULT_PRIX, "prix.greaterThanOrEqual=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    void getAllProduitsByPrixIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where prix is less than or equal to
        defaultProduitFiltering("prix.lessThanOrEqual=" + DEFAULT_PRIX, "prix.lessThanOrEqual=" + SMALLER_PRIX);
    }

    @Test
    @Transactional
    void getAllProduitsByPrixIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where prix is less than
        defaultProduitFiltering("prix.lessThan=" + UPDATED_PRIX, "prix.lessThan=" + DEFAULT_PRIX);
    }

    @Test
    @Transactional
    void getAllProduitsByPrixIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where prix is greater than
        defaultProduitFiltering("prix.greaterThan=" + SMALLER_PRIX, "prix.greaterThan=" + DEFAULT_PRIX);
    }

    @Test
    @Transactional
    void getAllProduitsByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where statut equals to
        defaultProduitFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllProduitsByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where statut in
        defaultProduitFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllProduitsByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where statut is not null
        defaultProduitFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllProduitsByPromotionIsEqualToSomething() throws Exception {
        Promotion promotion;
        if (TestUtil.findAll(em, Promotion.class).isEmpty()) {
            produitRepository.saveAndFlush(produit);
            promotion = PromotionResourceIT.createEntity();
        } else {
            promotion = TestUtil.findAll(em, Promotion.class).get(0);
        }
        em.persist(promotion);
        em.flush();
        produit.addPromotion(promotion);
        produitRepository.saveAndFlush(produit);
        Long promotionId = promotion.getId();
        // Get all the produitList where promotion equals to promotionId
        defaultProduitShouldBeFound("promotionId.equals=" + promotionId);

        // Get all the produitList where promotion equals to (promotionId + 1)
        defaultProduitShouldNotBeFound("promotionId.equals=" + (promotionId + 1));
    }

    @Test
    @Transactional
    void getAllProduitsByCategorieIsEqualToSomething() throws Exception {
        Categorie categorie;
        if (TestUtil.findAll(em, Categorie.class).isEmpty()) {
            produitRepository.saveAndFlush(produit);
            categorie = CategorieResourceIT.createEntity();
        } else {
            categorie = TestUtil.findAll(em, Categorie.class).get(0);
        }
        em.persist(categorie);
        em.flush();
        produit.setCategorie(categorie);
        produitRepository.saveAndFlush(produit);
        Long categorieId = categorie.getId();
        // Get all the produitList where categorie equals to categorieId
        defaultProduitShouldBeFound("categorieId.equals=" + categorieId);

        // Get all the produitList where categorie equals to (categorieId + 1)
        defaultProduitShouldNotBeFound("categorieId.equals=" + (categorieId + 1));
    }

    private void defaultProduitFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultProduitShouldBeFound(shouldBeFound);
        defaultProduitShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProduitShouldBeFound(String filter) throws Exception {
        restProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(produit.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].prix").value(hasItem(sameNumber(DEFAULT_PRIX))))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())));

        // Check, that the count call also returns 1
        restProduitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProduitShouldNotBeFound(String filter) throws Exception {
        restProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProduitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProduit() throws Exception {
        // Get the produit
        restProduitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProduit() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the produit
        Produit updatedProduit = produitRepository.findById(produit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProduit are not directly saved in db
        em.detach(updatedProduit);
        updatedProduit.nom(UPDATED_NOM).description(UPDATED_DESCRIPTION).prix(UPDATED_PRIX).statut(UPDATED_STATUT);
        ProduitDTO produitDTO = produitMapper.toDto(updatedProduit);

        restProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, produitDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produitDTO))
            )
            .andExpect(status().isOk());

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProduitToMatchAllProperties(updatedProduit);
    }

    @Test
    @Transactional
    void putNonExistingProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        produit.setId(longCount.incrementAndGet());

        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, produitDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        produit.setId(longCount.incrementAndGet());

        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(produitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        produit.setId(longCount.incrementAndGet());

        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProduitWithPatch() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the produit using partial update
        Produit partialUpdatedProduit = new Produit();
        partialUpdatedProduit.setId(produit.getId());

        partialUpdatedProduit.description(UPDATED_DESCRIPTION).prix(UPDATED_PRIX).statut(UPDATED_STATUT);

        restProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProduit))
            )
            .andExpect(status().isOk());

        // Validate the Produit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProduitUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProduit, produit), getPersistedProduit(produit));
    }

    @Test
    @Transactional
    void fullUpdateProduitWithPatch() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the produit using partial update
        Produit partialUpdatedProduit = new Produit();
        partialUpdatedProduit.setId(produit.getId());

        partialUpdatedProduit.nom(UPDATED_NOM).description(UPDATED_DESCRIPTION).prix(UPDATED_PRIX).statut(UPDATED_STATUT);

        restProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProduit))
            )
            .andExpect(status().isOk());

        // Validate the Produit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProduitUpdatableFieldsEquals(partialUpdatedProduit, getPersistedProduit(partialUpdatedProduit));
    }

    @Test
    @Transactional
    void patchNonExistingProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        produit.setId(longCount.incrementAndGet());

        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, produitDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(produitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        produit.setId(longCount.incrementAndGet());

        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(produitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        produit.setId(longCount.incrementAndGet());

        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(produitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProduit() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the produit
        restProduitMockMvc
            .perform(delete(ENTITY_API_URL_ID, produit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return produitRepository.count();
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

    protected Produit getPersistedProduit(Produit produit) {
        return produitRepository.findById(produit.getId()).orElseThrow();
    }

    protected void assertPersistedProduitToMatchAllProperties(Produit expectedProduit) {
        assertProduitAllPropertiesEquals(expectedProduit, getPersistedProduit(expectedProduit));
    }

    protected void assertPersistedProduitToMatchUpdatableProperties(Produit expectedProduit) {
        assertProduitAllUpdatablePropertiesEquals(expectedProduit, getPersistedProduit(expectedProduit));
    }
}
