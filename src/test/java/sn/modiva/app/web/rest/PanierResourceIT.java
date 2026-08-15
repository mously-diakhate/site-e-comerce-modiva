package sn.modiva.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.modiva.app.domain.PanierAsserts.*;
import static sn.modiva.app.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
import sn.modiva.app.domain.Panier;
import sn.modiva.app.repository.PanierRepository;
import sn.modiva.app.service.dto.PanierDTO;
import sn.modiva.app.service.mapper.PanierMapper;

/**
 * Integration tests for the {@link PanierResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PanierResourceIT {

    private static final Instant DEFAULT_DATE_CREATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CREATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/paniers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PanierRepository panierRepository;

    @Autowired
    private PanierMapper panierMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPanierMockMvc;

    private Panier panier;

    private Panier insertedPanier;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Panier createEntity() {
        return new Panier().dateCreation(DEFAULT_DATE_CREATION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Panier createUpdatedEntity() {
        return new Panier().dateCreation(UPDATED_DATE_CREATION);
    }

    @BeforeEach
    void initTest() {
        panier = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPanier != null) {
            panierRepository.delete(insertedPanier);
            insertedPanier = null;
        }
    }

    @Test
    @Transactional
    void createPanier() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);
        var returnedPanierDTO = om.readValue(
            restPanierMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(panierDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PanierDTO.class
        );

        // Validate the Panier in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPanier = panierMapper.toEntity(returnedPanierDTO);
        assertPanierUpdatableFieldsEquals(returnedPanier, getPersistedPanier(returnedPanier));

        insertedPanier = returnedPanier;
    }

    @Test
    @Transactional
    void createPanierWithExistingId() throws Exception {
        // Create the Panier with an existing ID
        panier.setId(1L);
        PanierDTO panierDTO = panierMapper.toDto(panier);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPanierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(panierDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Panier in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPaniers() throws Exception {
        // Initialize the database
        insertedPanier = panierRepository.saveAndFlush(panier);

        // Get all the panierList
        restPanierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(panier.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())));
    }

    @Test
    @Transactional
    void getPanier() throws Exception {
        // Initialize the database
        insertedPanier = panierRepository.saveAndFlush(panier);

        // Get the panier
        restPanierMockMvc
            .perform(get(ENTITY_API_URL_ID, panier.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(panier.getId().intValue()))
            .andExpect(jsonPath("$.dateCreation").value(DEFAULT_DATE_CREATION.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPanier() throws Exception {
        // Get the panier
        restPanierMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPanier() throws Exception {
        // Initialize the database
        insertedPanier = panierRepository.saveAndFlush(panier);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the panier
        Panier updatedPanier = panierRepository.findById(panier.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPanier are not directly saved in db
        em.detach(updatedPanier);
        updatedPanier.dateCreation(UPDATED_DATE_CREATION);
        PanierDTO panierDTO = panierMapper.toDto(updatedPanier);

        restPanierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, panierDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(panierDTO))
            )
            .andExpect(status().isOk());

        // Validate the Panier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPanierToMatchAllProperties(updatedPanier);
    }

    @Test
    @Transactional
    void putNonExistingPanier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        panier.setId(longCount.incrementAndGet());

        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPanierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, panierDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(panierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Panier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPanier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        panier.setId(longCount.incrementAndGet());

        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPanierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(panierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Panier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPanier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        panier.setId(longCount.incrementAndGet());

        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPanierMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(panierDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Panier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePanierWithPatch() throws Exception {
        // Initialize the database
        insertedPanier = panierRepository.saveAndFlush(panier);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the panier using partial update
        Panier partialUpdatedPanier = new Panier();
        partialUpdatedPanier.setId(panier.getId());

        restPanierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPanier.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPanier))
            )
            .andExpect(status().isOk());

        // Validate the Panier in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPanierUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPanier, panier), getPersistedPanier(panier));
    }

    @Test
    @Transactional
    void fullUpdatePanierWithPatch() throws Exception {
        // Initialize the database
        insertedPanier = panierRepository.saveAndFlush(panier);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the panier using partial update
        Panier partialUpdatedPanier = new Panier();
        partialUpdatedPanier.setId(panier.getId());

        partialUpdatedPanier.dateCreation(UPDATED_DATE_CREATION);

        restPanierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPanier.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPanier))
            )
            .andExpect(status().isOk());

        // Validate the Panier in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPanierUpdatableFieldsEquals(partialUpdatedPanier, getPersistedPanier(partialUpdatedPanier));
    }

    @Test
    @Transactional
    void patchNonExistingPanier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        panier.setId(longCount.incrementAndGet());

        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPanierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, panierDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(panierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Panier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPanier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        panier.setId(longCount.incrementAndGet());

        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPanierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(panierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Panier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPanier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        panier.setId(longCount.incrementAndGet());

        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPanierMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(panierDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Panier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePanier() throws Exception {
        // Initialize the database
        insertedPanier = panierRepository.saveAndFlush(panier);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the panier
        restPanierMockMvc
            .perform(delete(ENTITY_API_URL_ID, panier.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return panierRepository.count();
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

    protected Panier getPersistedPanier(Panier panier) {
        return panierRepository.findById(panier.getId()).orElseThrow();
    }

    protected void assertPersistedPanierToMatchAllProperties(Panier expectedPanier) {
        assertPanierAllPropertiesEquals(expectedPanier, getPersistedPanier(expectedPanier));
    }

    protected void assertPersistedPanierToMatchUpdatableProperties(Panier expectedPanier) {
        assertPanierAllUpdatablePropertiesEquals(expectedPanier, getPersistedPanier(expectedPanier));
    }
}
