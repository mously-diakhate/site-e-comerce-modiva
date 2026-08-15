package sn.modiva.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.modiva.app.domain.VarianteProduitAsserts.*;
import static sn.modiva.app.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
import sn.modiva.app.domain.Couleur;
import sn.modiva.app.domain.Produit;
import sn.modiva.app.domain.Taille;
import sn.modiva.app.domain.VarianteProduit;
import sn.modiva.app.repository.VarianteProduitRepository;
import sn.modiva.app.service.dto.VarianteProduitDTO;
import sn.modiva.app.service.mapper.VarianteProduitMapper;

/**
 * Integration tests for the {@link VarianteProduitResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VarianteProduitResourceIT {

    private static final Integer DEFAULT_STOCK = 0;
    private static final Integer UPDATED_STOCK = 1;
    private static final Integer SMALLER_STOCK = 0 - 1;

    private static final String ENTITY_API_URL = "/api/variante-produits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VarianteProduitRepository varianteProduitRepository;

    @Autowired
    private VarianteProduitMapper varianteProduitMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVarianteProduitMockMvc;

    private VarianteProduit varianteProduit;

    private VarianteProduit insertedVarianteProduit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VarianteProduit createEntity(EntityManager em) {
        VarianteProduit varianteProduit = new VarianteProduit().stock(DEFAULT_STOCK);
        // Add required entity
        Taille taille;
        if (TestUtil.findAll(em, Taille.class).isEmpty()) {
            taille = TailleResourceIT.createEntity();
            em.persist(taille);
            em.flush();
        } else {
            taille = TestUtil.findAll(em, Taille.class).get(0);
        }
        varianteProduit.setTaille(taille);
        // Add required entity
        Couleur couleur;
        if (TestUtil.findAll(em, Couleur.class).isEmpty()) {
            couleur = CouleurResourceIT.createEntity();
            em.persist(couleur);
            em.flush();
        } else {
            couleur = TestUtil.findAll(em, Couleur.class).get(0);
        }
        varianteProduit.setCouleur(couleur);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        varianteProduit.setProduit(produit);
        return varianteProduit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VarianteProduit createUpdatedEntity(EntityManager em) {
        VarianteProduit updatedVarianteProduit = new VarianteProduit().stock(UPDATED_STOCK);
        // Add required entity
        Taille taille;
        if (TestUtil.findAll(em, Taille.class).isEmpty()) {
            taille = TailleResourceIT.createUpdatedEntity();
            em.persist(taille);
            em.flush();
        } else {
            taille = TestUtil.findAll(em, Taille.class).get(0);
        }
        updatedVarianteProduit.setTaille(taille);
        // Add required entity
        Couleur couleur;
        if (TestUtil.findAll(em, Couleur.class).isEmpty()) {
            couleur = CouleurResourceIT.createUpdatedEntity();
            em.persist(couleur);
            em.flush();
        } else {
            couleur = TestUtil.findAll(em, Couleur.class).get(0);
        }
        updatedVarianteProduit.setCouleur(couleur);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createUpdatedEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        updatedVarianteProduit.setProduit(produit);
        return updatedVarianteProduit;
    }

    @BeforeEach
    void initTest() {
        varianteProduit = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedVarianteProduit != null) {
            varianteProduitRepository.delete(insertedVarianteProduit);
            insertedVarianteProduit = null;
        }
    }

    @Test
    @Transactional
    void createVarianteProduit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the VarianteProduit
        VarianteProduitDTO varianteProduitDTO = varianteProduitMapper.toDto(varianteProduit);
        var returnedVarianteProduitDTO = om.readValue(
            restVarianteProduitMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(varianteProduitDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            VarianteProduitDTO.class
        );

        // Validate the VarianteProduit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedVarianteProduit = varianteProduitMapper.toEntity(returnedVarianteProduitDTO);
        assertVarianteProduitUpdatableFieldsEquals(returnedVarianteProduit, getPersistedVarianteProduit(returnedVarianteProduit));

        insertedVarianteProduit = returnedVarianteProduit;
    }

    @Test
    @Transactional
    void createVarianteProduitWithExistingId() throws Exception {
        // Create the VarianteProduit with an existing ID
        varianteProduit.setId(1L);
        VarianteProduitDTO varianteProduitDTO = varianteProduitMapper.toDto(varianteProduit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVarianteProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(varianteProduitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the VarianteProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStockIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        varianteProduit.setStock(null);

        // Create the VarianteProduit, which fails.
        VarianteProduitDTO varianteProduitDTO = varianteProduitMapper.toDto(varianteProduit);

        restVarianteProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(varianteProduitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVarianteProduits() throws Exception {
        // Initialize the database
        insertedVarianteProduit = varianteProduitRepository.saveAndFlush(varianteProduit);

        // Get all the varianteProduitList
        restVarianteProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(varianteProduit.getId().intValue())))
            .andExpect(jsonPath("$.[*].stock").value(hasItem(DEFAULT_STOCK)));
    }

    @Test
    @Transactional
    void getVarianteProduit() throws Exception {
        // Initialize the database
        insertedVarianteProduit = varianteProduitRepository.saveAndFlush(varianteProduit);

        // Get the varianteProduit
        restVarianteProduitMockMvc
            .perform(get(ENTITY_API_URL_ID, varianteProduit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(varianteProduit.getId().intValue()))
            .andExpect(jsonPath("$.stock").value(DEFAULT_STOCK));
    }

    @Test
    @Transactional
    void getVarianteProduitsByIdFiltering() throws Exception {
        // Initialize the database
        insertedVarianteProduit = varianteProduitRepository.saveAndFlush(varianteProduit);

        Long id = varianteProduit.getId();

        defaultVarianteProduitFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultVarianteProduitFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultVarianteProduitFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllVarianteProduitsByStockIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVarianteProduit = varianteProduitRepository.saveAndFlush(varianteProduit);

        // Get all the varianteProduitList where stock equals to
        defaultVarianteProduitFiltering("stock.equals=" + DEFAULT_STOCK, "stock.equals=" + UPDATED_STOCK);
    }

    @Test
    @Transactional
    void getAllVarianteProduitsByStockIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVarianteProduit = varianteProduitRepository.saveAndFlush(varianteProduit);

        // Get all the varianteProduitList where stock in
        defaultVarianteProduitFiltering("stock.in=" + DEFAULT_STOCK + "," + UPDATED_STOCK, "stock.in=" + UPDATED_STOCK);
    }

    @Test
    @Transactional
    void getAllVarianteProduitsByStockIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVarianteProduit = varianteProduitRepository.saveAndFlush(varianteProduit);

        // Get all the varianteProduitList where stock is not null
        defaultVarianteProduitFiltering("stock.specified=true", "stock.specified=false");
    }

    @Test
    @Transactional
    void getAllVarianteProduitsByStockIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVarianteProduit = varianteProduitRepository.saveAndFlush(varianteProduit);

        // Get all the varianteProduitList where stock is greater than or equal to
        defaultVarianteProduitFiltering("stock.greaterThanOrEqual=" + DEFAULT_STOCK, "stock.greaterThanOrEqual=" + UPDATED_STOCK);
    }

    @Test
    @Transactional
    void getAllVarianteProduitsByStockIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVarianteProduit = varianteProduitRepository.saveAndFlush(varianteProduit);

        // Get all the varianteProduitList where stock is less than or equal to
        defaultVarianteProduitFiltering("stock.lessThanOrEqual=" + DEFAULT_STOCK, "stock.lessThanOrEqual=" + SMALLER_STOCK);
    }

    @Test
    @Transactional
    void getAllVarianteProduitsByStockIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedVarianteProduit = varianteProduitRepository.saveAndFlush(varianteProduit);

        // Get all the varianteProduitList where stock is less than
        defaultVarianteProduitFiltering("stock.lessThan=" + UPDATED_STOCK, "stock.lessThan=" + DEFAULT_STOCK);
    }

    @Test
    @Transactional
    void getAllVarianteProduitsByStockIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedVarianteProduit = varianteProduitRepository.saveAndFlush(varianteProduit);

        // Get all the varianteProduitList where stock is greater than
        defaultVarianteProduitFiltering("stock.greaterThan=" + SMALLER_STOCK, "stock.greaterThan=" + DEFAULT_STOCK);
    }

    @Test
    @Transactional
    void getAllVarianteProduitsByTailleIsEqualToSomething() throws Exception {
        Taille taille;
        if (TestUtil.findAll(em, Taille.class).isEmpty()) {
            varianteProduitRepository.saveAndFlush(varianteProduit);
            taille = TailleResourceIT.createEntity();
        } else {
            taille = TestUtil.findAll(em, Taille.class).get(0);
        }
        em.persist(taille);
        em.flush();
        varianteProduit.setTaille(taille);
        varianteProduitRepository.saveAndFlush(varianteProduit);
        Long tailleId = taille.getId();
        // Get all the varianteProduitList where taille equals to tailleId
        defaultVarianteProduitShouldBeFound("tailleId.equals=" + tailleId);

        // Get all the varianteProduitList where taille equals to (tailleId + 1)
        defaultVarianteProduitShouldNotBeFound("tailleId.equals=" + (tailleId + 1));
    }

    @Test
    @Transactional
    void getAllVarianteProduitsByCouleurIsEqualToSomething() throws Exception {
        Couleur couleur;
        if (TestUtil.findAll(em, Couleur.class).isEmpty()) {
            varianteProduitRepository.saveAndFlush(varianteProduit);
            couleur = CouleurResourceIT.createEntity();
        } else {
            couleur = TestUtil.findAll(em, Couleur.class).get(0);
        }
        em.persist(couleur);
        em.flush();
        varianteProduit.setCouleur(couleur);
        varianteProduitRepository.saveAndFlush(varianteProduit);
        Long couleurId = couleur.getId();
        // Get all the varianteProduitList where couleur equals to couleurId
        defaultVarianteProduitShouldBeFound("couleurId.equals=" + couleurId);

        // Get all the varianteProduitList where couleur equals to (couleurId + 1)
        defaultVarianteProduitShouldNotBeFound("couleurId.equals=" + (couleurId + 1));
    }

    @Test
    @Transactional
    void getAllVarianteProduitsByProduitIsEqualToSomething() throws Exception {
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            varianteProduitRepository.saveAndFlush(varianteProduit);
            produit = ProduitResourceIT.createEntity(em);
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        em.persist(produit);
        em.flush();
        varianteProduit.setProduit(produit);
        varianteProduitRepository.saveAndFlush(varianteProduit);
        Long produitId = produit.getId();
        // Get all the varianteProduitList where produit equals to produitId
        defaultVarianteProduitShouldBeFound("produitId.equals=" + produitId);

        // Get all the varianteProduitList where produit equals to (produitId + 1)
        defaultVarianteProduitShouldNotBeFound("produitId.equals=" + (produitId + 1));
    }

    private void defaultVarianteProduitFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultVarianteProduitShouldBeFound(shouldBeFound);
        defaultVarianteProduitShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultVarianteProduitShouldBeFound(String filter) throws Exception {
        restVarianteProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(varianteProduit.getId().intValue())))
            .andExpect(jsonPath("$.[*].stock").value(hasItem(DEFAULT_STOCK)));

        // Check, that the count call also returns 1
        restVarianteProduitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultVarianteProduitShouldNotBeFound(String filter) throws Exception {
        restVarianteProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restVarianteProduitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingVarianteProduit() throws Exception {
        // Get the varianteProduit
        restVarianteProduitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVarianteProduit() throws Exception {
        // Initialize the database
        insertedVarianteProduit = varianteProduitRepository.saveAndFlush(varianteProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the varianteProduit
        VarianteProduit updatedVarianteProduit = varianteProduitRepository.findById(varianteProduit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVarianteProduit are not directly saved in db
        em.detach(updatedVarianteProduit);
        updatedVarianteProduit.stock(UPDATED_STOCK);
        VarianteProduitDTO varianteProduitDTO = varianteProduitMapper.toDto(updatedVarianteProduit);

        restVarianteProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, varianteProduitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(varianteProduitDTO))
            )
            .andExpect(status().isOk());

        // Validate the VarianteProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVarianteProduitToMatchAllProperties(updatedVarianteProduit);
    }

    @Test
    @Transactional
    void putNonExistingVarianteProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        varianteProduit.setId(longCount.incrementAndGet());

        // Create the VarianteProduit
        VarianteProduitDTO varianteProduitDTO = varianteProduitMapper.toDto(varianteProduit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVarianteProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, varianteProduitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(varianteProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VarianteProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVarianteProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        varianteProduit.setId(longCount.incrementAndGet());

        // Create the VarianteProduit
        VarianteProduitDTO varianteProduitDTO = varianteProduitMapper.toDto(varianteProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVarianteProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(varianteProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VarianteProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVarianteProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        varianteProduit.setId(longCount.incrementAndGet());

        // Create the VarianteProduit
        VarianteProduitDTO varianteProduitDTO = varianteProduitMapper.toDto(varianteProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVarianteProduitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(varianteProduitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the VarianteProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVarianteProduitWithPatch() throws Exception {
        // Initialize the database
        insertedVarianteProduit = varianteProduitRepository.saveAndFlush(varianteProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the varianteProduit using partial update
        VarianteProduit partialUpdatedVarianteProduit = new VarianteProduit();
        partialUpdatedVarianteProduit.setId(varianteProduit.getId());

        restVarianteProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVarianteProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVarianteProduit))
            )
            .andExpect(status().isOk());

        // Validate the VarianteProduit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVarianteProduitUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedVarianteProduit, varianteProduit),
            getPersistedVarianteProduit(varianteProduit)
        );
    }

    @Test
    @Transactional
    void fullUpdateVarianteProduitWithPatch() throws Exception {
        // Initialize the database
        insertedVarianteProduit = varianteProduitRepository.saveAndFlush(varianteProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the varianteProduit using partial update
        VarianteProduit partialUpdatedVarianteProduit = new VarianteProduit();
        partialUpdatedVarianteProduit.setId(varianteProduit.getId());

        partialUpdatedVarianteProduit.stock(UPDATED_STOCK);

        restVarianteProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVarianteProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVarianteProduit))
            )
            .andExpect(status().isOk());

        // Validate the VarianteProduit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVarianteProduitUpdatableFieldsEquals(
            partialUpdatedVarianteProduit,
            getPersistedVarianteProduit(partialUpdatedVarianteProduit)
        );
    }

    @Test
    @Transactional
    void patchNonExistingVarianteProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        varianteProduit.setId(longCount.incrementAndGet());

        // Create the VarianteProduit
        VarianteProduitDTO varianteProduitDTO = varianteProduitMapper.toDto(varianteProduit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVarianteProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, varianteProduitDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(varianteProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VarianteProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVarianteProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        varianteProduit.setId(longCount.incrementAndGet());

        // Create the VarianteProduit
        VarianteProduitDTO varianteProduitDTO = varianteProduitMapper.toDto(varianteProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVarianteProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(varianteProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VarianteProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVarianteProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        varianteProduit.setId(longCount.incrementAndGet());

        // Create the VarianteProduit
        VarianteProduitDTO varianteProduitDTO = varianteProduitMapper.toDto(varianteProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVarianteProduitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(varianteProduitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the VarianteProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVarianteProduit() throws Exception {
        // Initialize the database
        insertedVarianteProduit = varianteProduitRepository.saveAndFlush(varianteProduit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the varianteProduit
        restVarianteProduitMockMvc
            .perform(delete(ENTITY_API_URL_ID, varianteProduit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return varianteProduitRepository.count();
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

    protected VarianteProduit getPersistedVarianteProduit(VarianteProduit varianteProduit) {
        return varianteProduitRepository.findById(varianteProduit.getId()).orElseThrow();
    }

    protected void assertPersistedVarianteProduitToMatchAllProperties(VarianteProduit expectedVarianteProduit) {
        assertVarianteProduitAllPropertiesEquals(expectedVarianteProduit, getPersistedVarianteProduit(expectedVarianteProduit));
    }

    protected void assertPersistedVarianteProduitToMatchUpdatableProperties(VarianteProduit expectedVarianteProduit) {
        assertVarianteProduitAllUpdatablePropertiesEquals(expectedVarianteProduit, getPersistedVarianteProduit(expectedVarianteProduit));
    }
}
