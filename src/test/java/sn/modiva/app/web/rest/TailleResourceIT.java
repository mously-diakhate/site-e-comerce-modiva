package sn.modiva.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.modiva.app.domain.TailleAsserts.*;
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
import sn.modiva.app.domain.Taille;
import sn.modiva.app.repository.TailleRepository;
import sn.modiva.app.service.dto.TailleDTO;
import sn.modiva.app.service.mapper.TailleMapper;

/**
 * Integration tests for the {@link TailleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TailleResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tailles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TailleRepository tailleRepository;

    @Autowired
    private TailleMapper tailleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTailleMockMvc;

    private Taille taille;

    private Taille insertedTaille;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Taille createEntity() {
        return new Taille().libelle(DEFAULT_LIBELLE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Taille createUpdatedEntity() {
        return new Taille().libelle(UPDATED_LIBELLE);
    }

    @BeforeEach
    void initTest() {
        taille = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTaille != null) {
            tailleRepository.delete(insertedTaille);
            insertedTaille = null;
        }
    }

    @Test
    @Transactional
    void createTaille() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Taille
        TailleDTO tailleDTO = tailleMapper.toDto(taille);
        var returnedTailleDTO = om.readValue(
            restTailleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tailleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TailleDTO.class
        );

        // Validate the Taille in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTaille = tailleMapper.toEntity(returnedTailleDTO);
        assertTailleUpdatableFieldsEquals(returnedTaille, getPersistedTaille(returnedTaille));

        insertedTaille = returnedTaille;
    }

    @Test
    @Transactional
    void createTailleWithExistingId() throws Exception {
        // Create the Taille with an existing ID
        taille.setId(1L);
        TailleDTO tailleDTO = tailleMapper.toDto(taille);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTailleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tailleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Taille in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        taille.setLibelle(null);

        // Create the Taille, which fails.
        TailleDTO tailleDTO = tailleMapper.toDto(taille);

        restTailleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tailleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTailles() throws Exception {
        // Initialize the database
        insertedTaille = tailleRepository.saveAndFlush(taille);

        // Get all the tailleList
        restTailleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taille.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
    }

    @Test
    @Transactional
    void getTaille() throws Exception {
        // Initialize the database
        insertedTaille = tailleRepository.saveAndFlush(taille);

        // Get the taille
        restTailleMockMvc
            .perform(get(ENTITY_API_URL_ID, taille.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(taille.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE));
    }

    @Test
    @Transactional
    void getNonExistingTaille() throws Exception {
        // Get the taille
        restTailleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTaille() throws Exception {
        // Initialize the database
        insertedTaille = tailleRepository.saveAndFlush(taille);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taille
        Taille updatedTaille = tailleRepository.findById(taille.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTaille are not directly saved in db
        em.detach(updatedTaille);
        updatedTaille.libelle(UPDATED_LIBELLE);
        TailleDTO tailleDTO = tailleMapper.toDto(updatedTaille);

        restTailleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tailleDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tailleDTO))
            )
            .andExpect(status().isOk());

        // Validate the Taille in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTailleToMatchAllProperties(updatedTaille);
    }

    @Test
    @Transactional
    void putNonExistingTaille() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taille.setId(longCount.incrementAndGet());

        // Create the Taille
        TailleDTO tailleDTO = tailleMapper.toDto(taille);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTailleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tailleDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tailleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Taille in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTaille() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taille.setId(longCount.incrementAndGet());

        // Create the Taille
        TailleDTO tailleDTO = tailleMapper.toDto(taille);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTailleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tailleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Taille in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTaille() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taille.setId(longCount.incrementAndGet());

        // Create the Taille
        TailleDTO tailleDTO = tailleMapper.toDto(taille);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTailleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tailleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Taille in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTailleWithPatch() throws Exception {
        // Initialize the database
        insertedTaille = tailleRepository.saveAndFlush(taille);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taille using partial update
        Taille partialUpdatedTaille = new Taille();
        partialUpdatedTaille.setId(taille.getId());

        partialUpdatedTaille.libelle(UPDATED_LIBELLE);

        restTailleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaille.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTaille))
            )
            .andExpect(status().isOk());

        // Validate the Taille in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTailleUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTaille, taille), getPersistedTaille(taille));
    }

    @Test
    @Transactional
    void fullUpdateTailleWithPatch() throws Exception {
        // Initialize the database
        insertedTaille = tailleRepository.saveAndFlush(taille);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taille using partial update
        Taille partialUpdatedTaille = new Taille();
        partialUpdatedTaille.setId(taille.getId());

        partialUpdatedTaille.libelle(UPDATED_LIBELLE);

        restTailleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaille.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTaille))
            )
            .andExpect(status().isOk());

        // Validate the Taille in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTailleUpdatableFieldsEquals(partialUpdatedTaille, getPersistedTaille(partialUpdatedTaille));
    }

    @Test
    @Transactional
    void patchNonExistingTaille() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taille.setId(longCount.incrementAndGet());

        // Create the Taille
        TailleDTO tailleDTO = tailleMapper.toDto(taille);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTailleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tailleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tailleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Taille in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTaille() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taille.setId(longCount.incrementAndGet());

        // Create the Taille
        TailleDTO tailleDTO = tailleMapper.toDto(taille);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTailleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tailleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Taille in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTaille() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taille.setId(longCount.incrementAndGet());

        // Create the Taille
        TailleDTO tailleDTO = tailleMapper.toDto(taille);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTailleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tailleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Taille in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTaille() throws Exception {
        // Initialize the database
        insertedTaille = tailleRepository.saveAndFlush(taille);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the taille
        restTailleMockMvc
            .perform(delete(ENTITY_API_URL_ID, taille.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tailleRepository.count();
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

    protected Taille getPersistedTaille(Taille taille) {
        return tailleRepository.findById(taille.getId()).orElseThrow();
    }

    protected void assertPersistedTailleToMatchAllProperties(Taille expectedTaille) {
        assertTailleAllPropertiesEquals(expectedTaille, getPersistedTaille(expectedTaille));
    }

    protected void assertPersistedTailleToMatchUpdatableProperties(Taille expectedTaille) {
        assertTailleAllUpdatablePropertiesEquals(expectedTaille, getPersistedTaille(expectedTaille));
    }
}
