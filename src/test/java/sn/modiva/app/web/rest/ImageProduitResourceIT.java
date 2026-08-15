package sn.modiva.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.modiva.app.domain.ImageProduitAsserts.*;
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
import sn.modiva.app.domain.ImageProduit;
import sn.modiva.app.domain.Produit;
import sn.modiva.app.repository.ImageProduitRepository;
import sn.modiva.app.service.dto.ImageProduitDTO;
import sn.modiva.app.service.mapper.ImageProduitMapper;

/**
 * Integration tests for the {@link ImageProduitResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ImageProduitResourceIT {

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORDRE = 1;
    private static final Integer UPDATED_ORDRE = 2;

    private static final String ENTITY_API_URL = "/api/image-produits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ImageProduitRepository imageProduitRepository;

    @Autowired
    private ImageProduitMapper imageProduitMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restImageProduitMockMvc;

    private ImageProduit imageProduit;

    private ImageProduit insertedImageProduit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImageProduit createEntity(EntityManager em) {
        ImageProduit imageProduit = new ImageProduit().url(DEFAULT_URL).ordre(DEFAULT_ORDRE);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        imageProduit.setProduit(produit);
        return imageProduit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImageProduit createUpdatedEntity(EntityManager em) {
        ImageProduit updatedImageProduit = new ImageProduit().url(UPDATED_URL).ordre(UPDATED_ORDRE);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createUpdatedEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        updatedImageProduit.setProduit(produit);
        return updatedImageProduit;
    }

    @BeforeEach
    void initTest() {
        imageProduit = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedImageProduit != null) {
            imageProduitRepository.delete(insertedImageProduit);
            insertedImageProduit = null;
        }
    }

    @Test
    @Transactional
    void createImageProduit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ImageProduit
        ImageProduitDTO imageProduitDTO = imageProduitMapper.toDto(imageProduit);
        var returnedImageProduitDTO = om.readValue(
            restImageProduitMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(imageProduitDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ImageProduitDTO.class
        );

        // Validate the ImageProduit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedImageProduit = imageProduitMapper.toEntity(returnedImageProduitDTO);
        assertImageProduitUpdatableFieldsEquals(returnedImageProduit, getPersistedImageProduit(returnedImageProduit));

        insertedImageProduit = returnedImageProduit;
    }

    @Test
    @Transactional
    void createImageProduitWithExistingId() throws Exception {
        // Create the ImageProduit with an existing ID
        imageProduit.setId(1L);
        ImageProduitDTO imageProduitDTO = imageProduitMapper.toDto(imageProduit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restImageProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(imageProduitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ImageProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        imageProduit.setUrl(null);

        // Create the ImageProduit, which fails.
        ImageProduitDTO imageProduitDTO = imageProduitMapper.toDto(imageProduit);

        restImageProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(imageProduitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllImageProduits() throws Exception {
        // Initialize the database
        insertedImageProduit = imageProduitRepository.saveAndFlush(imageProduit);

        // Get all the imageProduitList
        restImageProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(imageProduit.getId().intValue())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].ordre").value(hasItem(DEFAULT_ORDRE)));
    }

    @Test
    @Transactional
    void getImageProduit() throws Exception {
        // Initialize the database
        insertedImageProduit = imageProduitRepository.saveAndFlush(imageProduit);

        // Get the imageProduit
        restImageProduitMockMvc
            .perform(get(ENTITY_API_URL_ID, imageProduit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(imageProduit.getId().intValue()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.ordre").value(DEFAULT_ORDRE));
    }

    @Test
    @Transactional
    void getNonExistingImageProduit() throws Exception {
        // Get the imageProduit
        restImageProduitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingImageProduit() throws Exception {
        // Initialize the database
        insertedImageProduit = imageProduitRepository.saveAndFlush(imageProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the imageProduit
        ImageProduit updatedImageProduit = imageProduitRepository.findById(imageProduit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedImageProduit are not directly saved in db
        em.detach(updatedImageProduit);
        updatedImageProduit.url(UPDATED_URL).ordre(UPDATED_ORDRE);
        ImageProduitDTO imageProduitDTO = imageProduitMapper.toDto(updatedImageProduit);

        restImageProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, imageProduitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(imageProduitDTO))
            )
            .andExpect(status().isOk());

        // Validate the ImageProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedImageProduitToMatchAllProperties(updatedImageProduit);
    }

    @Test
    @Transactional
    void putNonExistingImageProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        imageProduit.setId(longCount.incrementAndGet());

        // Create the ImageProduit
        ImageProduitDTO imageProduitDTO = imageProduitMapper.toDto(imageProduit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImageProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, imageProduitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(imageProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImageProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchImageProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        imageProduit.setId(longCount.incrementAndGet());

        // Create the ImageProduit
        ImageProduitDTO imageProduitDTO = imageProduitMapper.toDto(imageProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImageProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(imageProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImageProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamImageProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        imageProduit.setId(longCount.incrementAndGet());

        // Create the ImageProduit
        ImageProduitDTO imageProduitDTO = imageProduitMapper.toDto(imageProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImageProduitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(imageProduitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ImageProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateImageProduitWithPatch() throws Exception {
        // Initialize the database
        insertedImageProduit = imageProduitRepository.saveAndFlush(imageProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the imageProduit using partial update
        ImageProduit partialUpdatedImageProduit = new ImageProduit();
        partialUpdatedImageProduit.setId(imageProduit.getId());

        partialUpdatedImageProduit.url(UPDATED_URL);

        restImageProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedImageProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedImageProduit))
            )
            .andExpect(status().isOk());

        // Validate the ImageProduit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertImageProduitUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedImageProduit, imageProduit),
            getPersistedImageProduit(imageProduit)
        );
    }

    @Test
    @Transactional
    void fullUpdateImageProduitWithPatch() throws Exception {
        // Initialize the database
        insertedImageProduit = imageProduitRepository.saveAndFlush(imageProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the imageProduit using partial update
        ImageProduit partialUpdatedImageProduit = new ImageProduit();
        partialUpdatedImageProduit.setId(imageProduit.getId());

        partialUpdatedImageProduit.url(UPDATED_URL).ordre(UPDATED_ORDRE);

        restImageProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedImageProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedImageProduit))
            )
            .andExpect(status().isOk());

        // Validate the ImageProduit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertImageProduitUpdatableFieldsEquals(partialUpdatedImageProduit, getPersistedImageProduit(partialUpdatedImageProduit));
    }

    @Test
    @Transactional
    void patchNonExistingImageProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        imageProduit.setId(longCount.incrementAndGet());

        // Create the ImageProduit
        ImageProduitDTO imageProduitDTO = imageProduitMapper.toDto(imageProduit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImageProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, imageProduitDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(imageProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImageProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchImageProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        imageProduit.setId(longCount.incrementAndGet());

        // Create the ImageProduit
        ImageProduitDTO imageProduitDTO = imageProduitMapper.toDto(imageProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImageProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(imageProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ImageProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamImageProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        imageProduit.setId(longCount.incrementAndGet());

        // Create the ImageProduit
        ImageProduitDTO imageProduitDTO = imageProduitMapper.toDto(imageProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restImageProduitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(imageProduitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ImageProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteImageProduit() throws Exception {
        // Initialize the database
        insertedImageProduit = imageProduitRepository.saveAndFlush(imageProduit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the imageProduit
        restImageProduitMockMvc
            .perform(delete(ENTITY_API_URL_ID, imageProduit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return imageProduitRepository.count();
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

    protected ImageProduit getPersistedImageProduit(ImageProduit imageProduit) {
        return imageProduitRepository.findById(imageProduit.getId()).orElseThrow();
    }

    protected void assertPersistedImageProduitToMatchAllProperties(ImageProduit expectedImageProduit) {
        assertImageProduitAllPropertiesEquals(expectedImageProduit, getPersistedImageProduit(expectedImageProduit));
    }

    protected void assertPersistedImageProduitToMatchUpdatableProperties(ImageProduit expectedImageProduit) {
        assertImageProduitAllUpdatablePropertiesEquals(expectedImageProduit, getPersistedImageProduit(expectedImageProduit));
    }
}
