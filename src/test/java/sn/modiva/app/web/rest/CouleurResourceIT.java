package sn.modiva.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.modiva.app.domain.CouleurAsserts.*;
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
import sn.modiva.app.repository.CouleurRepository;
import sn.modiva.app.service.dto.CouleurDTO;
import sn.modiva.app.service.mapper.CouleurMapper;

/**
 * Integration tests for the {@link CouleurResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CouleurResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String DEFAULT_CODE_HEX = "AAAAAAAAAA";
    private static final String UPDATED_CODE_HEX = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/couleurs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CouleurRepository couleurRepository;

    @Autowired
    private CouleurMapper couleurMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCouleurMockMvc;

    private Couleur couleur;

    private Couleur insertedCouleur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Couleur createEntity() {
        return new Couleur().libelle(DEFAULT_LIBELLE).codeHex(DEFAULT_CODE_HEX);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Couleur createUpdatedEntity() {
        return new Couleur().libelle(UPDATED_LIBELLE).codeHex(UPDATED_CODE_HEX);
    }

    @BeforeEach
    void initTest() {
        couleur = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCouleur != null) {
            couleurRepository.delete(insertedCouleur);
            insertedCouleur = null;
        }
    }

    @Test
    @Transactional
    void createCouleur() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Couleur
        CouleurDTO couleurDTO = couleurMapper.toDto(couleur);
        var returnedCouleurDTO = om.readValue(
            restCouleurMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couleurDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CouleurDTO.class
        );

        // Validate the Couleur in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCouleur = couleurMapper.toEntity(returnedCouleurDTO);
        assertCouleurUpdatableFieldsEquals(returnedCouleur, getPersistedCouleur(returnedCouleur));

        insertedCouleur = returnedCouleur;
    }

    @Test
    @Transactional
    void createCouleurWithExistingId() throws Exception {
        // Create the Couleur with an existing ID
        couleur.setId(1L);
        CouleurDTO couleurDTO = couleurMapper.toDto(couleur);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCouleurMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couleurDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Couleur in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        couleur.setLibelle(null);

        // Create the Couleur, which fails.
        CouleurDTO couleurDTO = couleurMapper.toDto(couleur);

        restCouleurMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couleurDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCouleurs() throws Exception {
        // Initialize the database
        insertedCouleur = couleurRepository.saveAndFlush(couleur);

        // Get all the couleurList
        restCouleurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(couleur.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].codeHex").value(hasItem(DEFAULT_CODE_HEX)));
    }

    @Test
    @Transactional
    void getCouleur() throws Exception {
        // Initialize the database
        insertedCouleur = couleurRepository.saveAndFlush(couleur);

        // Get the couleur
        restCouleurMockMvc
            .perform(get(ENTITY_API_URL_ID, couleur.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(couleur.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE))
            .andExpect(jsonPath("$.codeHex").value(DEFAULT_CODE_HEX));
    }

    @Test
    @Transactional
    void getNonExistingCouleur() throws Exception {
        // Get the couleur
        restCouleurMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCouleur() throws Exception {
        // Initialize the database
        insertedCouleur = couleurRepository.saveAndFlush(couleur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the couleur
        Couleur updatedCouleur = couleurRepository.findById(couleur.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCouleur are not directly saved in db
        em.detach(updatedCouleur);
        updatedCouleur.libelle(UPDATED_LIBELLE).codeHex(UPDATED_CODE_HEX);
        CouleurDTO couleurDTO = couleurMapper.toDto(updatedCouleur);

        restCouleurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, couleurDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couleurDTO))
            )
            .andExpect(status().isOk());

        // Validate the Couleur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCouleurToMatchAllProperties(updatedCouleur);
    }

    @Test
    @Transactional
    void putNonExistingCouleur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        couleur.setId(longCount.incrementAndGet());

        // Create the Couleur
        CouleurDTO couleurDTO = couleurMapper.toDto(couleur);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCouleurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, couleurDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couleurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Couleur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCouleur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        couleur.setId(longCount.incrementAndGet());

        // Create the Couleur
        CouleurDTO couleurDTO = couleurMapper.toDto(couleur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCouleurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(couleurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Couleur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCouleur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        couleur.setId(longCount.incrementAndGet());

        // Create the Couleur
        CouleurDTO couleurDTO = couleurMapper.toDto(couleur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCouleurMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couleurDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Couleur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCouleurWithPatch() throws Exception {
        // Initialize the database
        insertedCouleur = couleurRepository.saveAndFlush(couleur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the couleur using partial update
        Couleur partialUpdatedCouleur = new Couleur();
        partialUpdatedCouleur.setId(couleur.getId());

        partialUpdatedCouleur.codeHex(UPDATED_CODE_HEX);

        restCouleurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCouleur.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCouleur))
            )
            .andExpect(status().isOk());

        // Validate the Couleur in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCouleurUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCouleur, couleur), getPersistedCouleur(couleur));
    }

    @Test
    @Transactional
    void fullUpdateCouleurWithPatch() throws Exception {
        // Initialize the database
        insertedCouleur = couleurRepository.saveAndFlush(couleur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the couleur using partial update
        Couleur partialUpdatedCouleur = new Couleur();
        partialUpdatedCouleur.setId(couleur.getId());

        partialUpdatedCouleur.libelle(UPDATED_LIBELLE).codeHex(UPDATED_CODE_HEX);

        restCouleurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCouleur.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCouleur))
            )
            .andExpect(status().isOk());

        // Validate the Couleur in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCouleurUpdatableFieldsEquals(partialUpdatedCouleur, getPersistedCouleur(partialUpdatedCouleur));
    }

    @Test
    @Transactional
    void patchNonExistingCouleur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        couleur.setId(longCount.incrementAndGet());

        // Create the Couleur
        CouleurDTO couleurDTO = couleurMapper.toDto(couleur);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCouleurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, couleurDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(couleurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Couleur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCouleur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        couleur.setId(longCount.incrementAndGet());

        // Create the Couleur
        CouleurDTO couleurDTO = couleurMapper.toDto(couleur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCouleurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(couleurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Couleur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCouleur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        couleur.setId(longCount.incrementAndGet());

        // Create the Couleur
        CouleurDTO couleurDTO = couleurMapper.toDto(couleur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCouleurMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(couleurDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Couleur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCouleur() throws Exception {
        // Initialize the database
        insertedCouleur = couleurRepository.saveAndFlush(couleur);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the couleur
        restCouleurMockMvc
            .perform(delete(ENTITY_API_URL_ID, couleur.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return couleurRepository.count();
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

    protected Couleur getPersistedCouleur(Couleur couleur) {
        return couleurRepository.findById(couleur.getId()).orElseThrow();
    }

    protected void assertPersistedCouleurToMatchAllProperties(Couleur expectedCouleur) {
        assertCouleurAllPropertiesEquals(expectedCouleur, getPersistedCouleur(expectedCouleur));
    }

    protected void assertPersistedCouleurToMatchUpdatableProperties(Couleur expectedCouleur) {
        assertCouleurAllUpdatablePropertiesEquals(expectedCouleur, getPersistedCouleur(expectedCouleur));
    }
}
