package sn.modiva.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.modiva.app.domain.CategorieAsserts.*;
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
import sn.modiva.app.domain.Categorie;
import sn.modiva.app.repository.CategorieRepository;
import sn.modiva.app.service.dto.CategorieDTO;
import sn.modiva.app.service.mapper.CategorieMapper;

/**
 * Integration tests for the {@link CategorieResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CategorieResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CategorieRepository categorieRepository;

    @Autowired
    private CategorieMapper categorieMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCategorieMockMvc;

    private Categorie categorie;

    private Categorie insertedCategorie;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Categorie createEntity() {
        return new Categorie().nom(DEFAULT_NOM).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Categorie createUpdatedEntity() {
        return new Categorie().nom(UPDATED_NOM).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    void initTest() {
        categorie = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCategorie != null) {
            categorieRepository.delete(insertedCategorie);
            insertedCategorie = null;
        }
    }

    @Test
    @Transactional
    void createCategorie() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Categorie
        CategorieDTO categorieDTO = categorieMapper.toDto(categorie);
        var returnedCategorieDTO = om.readValue(
            restCategorieMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(categorieDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CategorieDTO.class
        );

        // Validate the Categorie in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCategorie = categorieMapper.toEntity(returnedCategorieDTO);
        assertCategorieUpdatableFieldsEquals(returnedCategorie, getPersistedCategorie(returnedCategorie));

        insertedCategorie = returnedCategorie;
    }

    @Test
    @Transactional
    void createCategorieWithExistingId() throws Exception {
        // Create the Categorie with an existing ID
        categorie.setId(1L);
        CategorieDTO categorieDTO = categorieMapper.toDto(categorie);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCategorieMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(categorieDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Categorie in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        categorie.setNom(null);

        // Create the Categorie, which fails.
        CategorieDTO categorieDTO = categorieMapper.toDto(categorie);

        restCategorieMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(categorieDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCategories() throws Exception {
        // Initialize the database
        insertedCategorie = categorieRepository.saveAndFlush(categorie);

        // Get all the categorieList
        restCategorieMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(categorie.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getCategorie() throws Exception {
        // Initialize the database
        insertedCategorie = categorieRepository.saveAndFlush(categorie);

        // Get the categorie
        restCategorieMockMvc
            .perform(get(ENTITY_API_URL_ID, categorie.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(categorie.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingCategorie() throws Exception {
        // Get the categorie
        restCategorieMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCategorie() throws Exception {
        // Initialize the database
        insertedCategorie = categorieRepository.saveAndFlush(categorie);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the categorie
        Categorie updatedCategorie = categorieRepository.findById(categorie.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCategorie are not directly saved in db
        em.detach(updatedCategorie);
        updatedCategorie.nom(UPDATED_NOM).description(UPDATED_DESCRIPTION);
        CategorieDTO categorieDTO = categorieMapper.toDto(updatedCategorie);

        restCategorieMockMvc
            .perform(
                put(ENTITY_API_URL_ID, categorieDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(categorieDTO))
            )
            .andExpect(status().isOk());

        // Validate the Categorie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCategorieToMatchAllProperties(updatedCategorie);
    }

    @Test
    @Transactional
    void putNonExistingCategorie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categorie.setId(longCount.incrementAndGet());

        // Create the Categorie
        CategorieDTO categorieDTO = categorieMapper.toDto(categorie);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCategorieMockMvc
            .perform(
                put(ENTITY_API_URL_ID, categorieDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(categorieDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Categorie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCategorie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categorie.setId(longCount.incrementAndGet());

        // Create the Categorie
        CategorieDTO categorieDTO = categorieMapper.toDto(categorie);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategorieMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(categorieDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Categorie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCategorie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categorie.setId(longCount.incrementAndGet());

        // Create the Categorie
        CategorieDTO categorieDTO = categorieMapper.toDto(categorie);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategorieMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(categorieDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Categorie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCategorieWithPatch() throws Exception {
        // Initialize the database
        insertedCategorie = categorieRepository.saveAndFlush(categorie);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the categorie using partial update
        Categorie partialUpdatedCategorie = new Categorie();
        partialUpdatedCategorie.setId(categorie.getId());

        restCategorieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCategorie.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCategorie))
            )
            .andExpect(status().isOk());

        // Validate the Categorie in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCategorieUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCategorie, categorie),
            getPersistedCategorie(categorie)
        );
    }

    @Test
    @Transactional
    void fullUpdateCategorieWithPatch() throws Exception {
        // Initialize the database
        insertedCategorie = categorieRepository.saveAndFlush(categorie);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the categorie using partial update
        Categorie partialUpdatedCategorie = new Categorie();
        partialUpdatedCategorie.setId(categorie.getId());

        partialUpdatedCategorie.nom(UPDATED_NOM).description(UPDATED_DESCRIPTION);

        restCategorieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCategorie.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCategorie))
            )
            .andExpect(status().isOk());

        // Validate the Categorie in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCategorieUpdatableFieldsEquals(partialUpdatedCategorie, getPersistedCategorie(partialUpdatedCategorie));
    }

    @Test
    @Transactional
    void patchNonExistingCategorie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categorie.setId(longCount.incrementAndGet());

        // Create the Categorie
        CategorieDTO categorieDTO = categorieMapper.toDto(categorie);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCategorieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, categorieDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(categorieDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Categorie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCategorie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categorie.setId(longCount.incrementAndGet());

        // Create the Categorie
        CategorieDTO categorieDTO = categorieMapper.toDto(categorie);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategorieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(categorieDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Categorie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCategorie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categorie.setId(longCount.incrementAndGet());

        // Create the Categorie
        CategorieDTO categorieDTO = categorieMapper.toDto(categorie);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategorieMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(categorieDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Categorie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCategorie() throws Exception {
        // Initialize the database
        insertedCategorie = categorieRepository.saveAndFlush(categorie);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the categorie
        restCategorieMockMvc
            .perform(delete(ENTITY_API_URL_ID, categorie.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return categorieRepository.count();
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

    protected Categorie getPersistedCategorie(Categorie categorie) {
        return categorieRepository.findById(categorie.getId()).orElseThrow();
    }

    protected void assertPersistedCategorieToMatchAllProperties(Categorie expectedCategorie) {
        assertCategorieAllPropertiesEquals(expectedCategorie, getPersistedCategorie(expectedCategorie));
    }

    protected void assertPersistedCategorieToMatchUpdatableProperties(Categorie expectedCategorie) {
        assertCategorieAllUpdatablePropertiesEquals(expectedCategorie, getPersistedCategorie(expectedCategorie));
    }
}
