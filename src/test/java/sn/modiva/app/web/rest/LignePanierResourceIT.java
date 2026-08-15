package sn.modiva.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.modiva.app.domain.LignePanierAsserts.*;
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
import sn.modiva.app.domain.LignePanier;
import sn.modiva.app.domain.Panier;
import sn.modiva.app.domain.VarianteProduit;
import sn.modiva.app.repository.LignePanierRepository;
import sn.modiva.app.service.dto.LignePanierDTO;
import sn.modiva.app.service.mapper.LignePanierMapper;

/**
 * Integration tests for the {@link LignePanierResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LignePanierResourceIT {

    private static final Integer DEFAULT_QUANTITE = 1;
    private static final Integer UPDATED_QUANTITE = 2;

    private static final String ENTITY_API_URL = "/api/ligne-paniers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LignePanierRepository lignePanierRepository;

    @Autowired
    private LignePanierMapper lignePanierMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLignePanierMockMvc;

    private LignePanier lignePanier;

    private LignePanier insertedLignePanier;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LignePanier createEntity(EntityManager em) {
        LignePanier lignePanier = new LignePanier().quantite(DEFAULT_QUANTITE);
        // Add required entity
        VarianteProduit varianteProduit;
        if (TestUtil.findAll(em, VarianteProduit.class).isEmpty()) {
            varianteProduit = VarianteProduitResourceIT.createEntity(em);
            em.persist(varianteProduit);
            em.flush();
        } else {
            varianteProduit = TestUtil.findAll(em, VarianteProduit.class).get(0);
        }
        lignePanier.setVariante(varianteProduit);
        // Add required entity
        Panier panier;
        if (TestUtil.findAll(em, Panier.class).isEmpty()) {
            panier = PanierResourceIT.createEntity();
            em.persist(panier);
            em.flush();
        } else {
            panier = TestUtil.findAll(em, Panier.class).get(0);
        }
        lignePanier.setPanier(panier);
        return lignePanier;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LignePanier createUpdatedEntity(EntityManager em) {
        LignePanier updatedLignePanier = new LignePanier().quantite(UPDATED_QUANTITE);
        // Add required entity
        VarianteProduit varianteProduit;
        if (TestUtil.findAll(em, VarianteProduit.class).isEmpty()) {
            varianteProduit = VarianteProduitResourceIT.createUpdatedEntity(em);
            em.persist(varianteProduit);
            em.flush();
        } else {
            varianteProduit = TestUtil.findAll(em, VarianteProduit.class).get(0);
        }
        updatedLignePanier.setVariante(varianteProduit);
        // Add required entity
        Panier panier;
        if (TestUtil.findAll(em, Panier.class).isEmpty()) {
            panier = PanierResourceIT.createUpdatedEntity();
            em.persist(panier);
            em.flush();
        } else {
            panier = TestUtil.findAll(em, Panier.class).get(0);
        }
        updatedLignePanier.setPanier(panier);
        return updatedLignePanier;
    }

    @BeforeEach
    void initTest() {
        lignePanier = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedLignePanier != null) {
            lignePanierRepository.delete(insertedLignePanier);
            insertedLignePanier = null;
        }
    }

    @Test
    @Transactional
    void createLignePanier() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LignePanier
        LignePanierDTO lignePanierDTO = lignePanierMapper.toDto(lignePanier);
        var returnedLignePanierDTO = om.readValue(
            restLignePanierMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lignePanierDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LignePanierDTO.class
        );

        // Validate the LignePanier in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLignePanier = lignePanierMapper.toEntity(returnedLignePanierDTO);
        assertLignePanierUpdatableFieldsEquals(returnedLignePanier, getPersistedLignePanier(returnedLignePanier));

        insertedLignePanier = returnedLignePanier;
    }

    @Test
    @Transactional
    void createLignePanierWithExistingId() throws Exception {
        // Create the LignePanier with an existing ID
        lignePanier.setId(1L);
        LignePanierDTO lignePanierDTO = lignePanierMapper.toDto(lignePanier);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLignePanierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lignePanierDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LignePanier in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantiteIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lignePanier.setQuantite(null);

        // Create the LignePanier, which fails.
        LignePanierDTO lignePanierDTO = lignePanierMapper.toDto(lignePanier);

        restLignePanierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lignePanierDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLignePaniers() throws Exception {
        // Initialize the database
        insertedLignePanier = lignePanierRepository.saveAndFlush(lignePanier);

        // Get all the lignePanierList
        restLignePanierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lignePanier.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantite").value(hasItem(DEFAULT_QUANTITE)));
    }

    @Test
    @Transactional
    void getLignePanier() throws Exception {
        // Initialize the database
        insertedLignePanier = lignePanierRepository.saveAndFlush(lignePanier);

        // Get the lignePanier
        restLignePanierMockMvc
            .perform(get(ENTITY_API_URL_ID, lignePanier.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(lignePanier.getId().intValue()))
            .andExpect(jsonPath("$.quantite").value(DEFAULT_QUANTITE));
    }

    @Test
    @Transactional
    void getNonExistingLignePanier() throws Exception {
        // Get the lignePanier
        restLignePanierMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLignePanier() throws Exception {
        // Initialize the database
        insertedLignePanier = lignePanierRepository.saveAndFlush(lignePanier);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lignePanier
        LignePanier updatedLignePanier = lignePanierRepository.findById(lignePanier.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLignePanier are not directly saved in db
        em.detach(updatedLignePanier);
        updatedLignePanier.quantite(UPDATED_QUANTITE);
        LignePanierDTO lignePanierDTO = lignePanierMapper.toDto(updatedLignePanier);

        restLignePanierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lignePanierDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(lignePanierDTO))
            )
            .andExpect(status().isOk());

        // Validate the LignePanier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLignePanierToMatchAllProperties(updatedLignePanier);
    }

    @Test
    @Transactional
    void putNonExistingLignePanier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lignePanier.setId(longCount.incrementAndGet());

        // Create the LignePanier
        LignePanierDTO lignePanierDTO = lignePanierMapper.toDto(lignePanier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLignePanierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lignePanierDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(lignePanierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LignePanier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLignePanier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lignePanier.setId(longCount.incrementAndGet());

        // Create the LignePanier
        LignePanierDTO lignePanierDTO = lignePanierMapper.toDto(lignePanier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLignePanierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(lignePanierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LignePanier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLignePanier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lignePanier.setId(longCount.incrementAndGet());

        // Create the LignePanier
        LignePanierDTO lignePanierDTO = lignePanierMapper.toDto(lignePanier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLignePanierMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lignePanierDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LignePanier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLignePanierWithPatch() throws Exception {
        // Initialize the database
        insertedLignePanier = lignePanierRepository.saveAndFlush(lignePanier);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lignePanier using partial update
        LignePanier partialUpdatedLignePanier = new LignePanier();
        partialUpdatedLignePanier.setId(lignePanier.getId());

        restLignePanierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLignePanier.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLignePanier))
            )
            .andExpect(status().isOk());

        // Validate the LignePanier in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLignePanierUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLignePanier, lignePanier),
            getPersistedLignePanier(lignePanier)
        );
    }

    @Test
    @Transactional
    void fullUpdateLignePanierWithPatch() throws Exception {
        // Initialize the database
        insertedLignePanier = lignePanierRepository.saveAndFlush(lignePanier);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lignePanier using partial update
        LignePanier partialUpdatedLignePanier = new LignePanier();
        partialUpdatedLignePanier.setId(lignePanier.getId());

        partialUpdatedLignePanier.quantite(UPDATED_QUANTITE);

        restLignePanierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLignePanier.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLignePanier))
            )
            .andExpect(status().isOk());

        // Validate the LignePanier in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLignePanierUpdatableFieldsEquals(partialUpdatedLignePanier, getPersistedLignePanier(partialUpdatedLignePanier));
    }

    @Test
    @Transactional
    void patchNonExistingLignePanier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lignePanier.setId(longCount.incrementAndGet());

        // Create the LignePanier
        LignePanierDTO lignePanierDTO = lignePanierMapper.toDto(lignePanier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLignePanierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, lignePanierDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(lignePanierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LignePanier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLignePanier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lignePanier.setId(longCount.incrementAndGet());

        // Create the LignePanier
        LignePanierDTO lignePanierDTO = lignePanierMapper.toDto(lignePanier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLignePanierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(lignePanierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LignePanier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLignePanier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lignePanier.setId(longCount.incrementAndGet());

        // Create the LignePanier
        LignePanierDTO lignePanierDTO = lignePanierMapper.toDto(lignePanier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLignePanierMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(lignePanierDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LignePanier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLignePanier() throws Exception {
        // Initialize the database
        insertedLignePanier = lignePanierRepository.saveAndFlush(lignePanier);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the lignePanier
        restLignePanierMockMvc
            .perform(delete(ENTITY_API_URL_ID, lignePanier.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return lignePanierRepository.count();
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

    protected LignePanier getPersistedLignePanier(LignePanier lignePanier) {
        return lignePanierRepository.findById(lignePanier.getId()).orElseThrow();
    }

    protected void assertPersistedLignePanierToMatchAllProperties(LignePanier expectedLignePanier) {
        assertLignePanierAllPropertiesEquals(expectedLignePanier, getPersistedLignePanier(expectedLignePanier));
    }

    protected void assertPersistedLignePanierToMatchUpdatableProperties(LignePanier expectedLignePanier) {
        assertLignePanierAllUpdatablePropertiesEquals(expectedLignePanier, getPersistedLignePanier(expectedLignePanier));
    }
}
