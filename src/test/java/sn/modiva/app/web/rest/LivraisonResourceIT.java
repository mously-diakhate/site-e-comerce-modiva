package sn.modiva.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.modiva.app.domain.LivraisonAsserts.*;
import static sn.modiva.app.web.rest.TestUtil.createUpdateProxyForBean;
import static sn.modiva.app.web.rest.TestUtil.sameNumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
import sn.modiva.app.domain.Livraison;
import sn.modiva.app.domain.enumeration.StatutLivraison;
import sn.modiva.app.repository.LivraisonRepository;
import sn.modiva.app.service.dto.LivraisonDTO;
import sn.modiva.app.service.mapper.LivraisonMapper;

/**
 * Integration tests for the {@link LivraisonResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LivraisonResourceIT {

    private static final String DEFAULT_ADRESSE = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE = "BBBBBBBBBB";

    private static final String DEFAULT_VILLE = "AAAAAAAAAA";
    private static final String UPDATED_VILLE = "BBBBBBBBBB";

    private static final String DEFAULT_QUARTIER = "AAAAAAAAAA";
    private static final String UPDATED_QUARTIER = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_FRAIS_LIVRAISON = new BigDecimal(1);
    private static final BigDecimal UPDATED_FRAIS_LIVRAISON = new BigDecimal(2);

    private static final StatutLivraison DEFAULT_STATUT_LIVRAISON = StatutLivraison.EN_ATTENTE;
    private static final StatutLivraison UPDATED_STATUT_LIVRAISON = StatutLivraison.EN_PREPARATION;

    private static final String ENTITY_API_URL = "/api/livraisons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LivraisonRepository livraisonRepository;

    @Autowired
    private LivraisonMapper livraisonMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLivraisonMockMvc;

    private Livraison livraison;

    private Livraison insertedLivraison;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Livraison createEntity() {
        return new Livraison()
            .adresse(DEFAULT_ADRESSE)
            .ville(DEFAULT_VILLE)
            .quartier(DEFAULT_QUARTIER)
            .telephone(DEFAULT_TELEPHONE)
            .fraisLivraison(DEFAULT_FRAIS_LIVRAISON)
            .statutLivraison(DEFAULT_STATUT_LIVRAISON);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Livraison createUpdatedEntity() {
        return new Livraison()
            .adresse(UPDATED_ADRESSE)
            .ville(UPDATED_VILLE)
            .quartier(UPDATED_QUARTIER)
            .telephone(UPDATED_TELEPHONE)
            .fraisLivraison(UPDATED_FRAIS_LIVRAISON)
            .statutLivraison(UPDATED_STATUT_LIVRAISON);
    }

    @BeforeEach
    void initTest() {
        livraison = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedLivraison != null) {
            livraisonRepository.delete(insertedLivraison);
            insertedLivraison = null;
        }
    }

    @Test
    @Transactional
    void createLivraison() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Livraison
        LivraisonDTO livraisonDTO = livraisonMapper.toDto(livraison);
        var returnedLivraisonDTO = om.readValue(
            restLivraisonMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(livraisonDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LivraisonDTO.class
        );

        // Validate the Livraison in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLivraison = livraisonMapper.toEntity(returnedLivraisonDTO);
        assertLivraisonUpdatableFieldsEquals(returnedLivraison, getPersistedLivraison(returnedLivraison));

        insertedLivraison = returnedLivraison;
    }

    @Test
    @Transactional
    void createLivraisonWithExistingId() throws Exception {
        // Create the Livraison with an existing ID
        livraison.setId(1L);
        LivraisonDTO livraisonDTO = livraisonMapper.toDto(livraison);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLivraisonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(livraisonDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Livraison in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAdresseIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        livraison.setAdresse(null);

        // Create the Livraison, which fails.
        LivraisonDTO livraisonDTO = livraisonMapper.toDto(livraison);

        restLivraisonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(livraisonDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVilleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        livraison.setVille(null);

        // Create the Livraison, which fails.
        LivraisonDTO livraisonDTO = livraisonMapper.toDto(livraison);

        restLivraisonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(livraisonDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutLivraisonIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        livraison.setStatutLivraison(null);

        // Create the Livraison, which fails.
        LivraisonDTO livraisonDTO = livraisonMapper.toDto(livraison);

        restLivraisonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(livraisonDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLivraisons() throws Exception {
        // Initialize the database
        insertedLivraison = livraisonRepository.saveAndFlush(livraison);

        // Get all the livraisonList
        restLivraisonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(livraison.getId().intValue())))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)))
            .andExpect(jsonPath("$.[*].ville").value(hasItem(DEFAULT_VILLE)))
            .andExpect(jsonPath("$.[*].quartier").value(hasItem(DEFAULT_QUARTIER)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].fraisLivraison").value(hasItem(sameNumber(DEFAULT_FRAIS_LIVRAISON))))
            .andExpect(jsonPath("$.[*].statutLivraison").value(hasItem(DEFAULT_STATUT_LIVRAISON.toString())));
    }

    @Test
    @Transactional
    void getLivraison() throws Exception {
        // Initialize the database
        insertedLivraison = livraisonRepository.saveAndFlush(livraison);

        // Get the livraison
        restLivraisonMockMvc
            .perform(get(ENTITY_API_URL_ID, livraison.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(livraison.getId().intValue()))
            .andExpect(jsonPath("$.adresse").value(DEFAULT_ADRESSE))
            .andExpect(jsonPath("$.ville").value(DEFAULT_VILLE))
            .andExpect(jsonPath("$.quartier").value(DEFAULT_QUARTIER))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE))
            .andExpect(jsonPath("$.fraisLivraison").value(sameNumber(DEFAULT_FRAIS_LIVRAISON)))
            .andExpect(jsonPath("$.statutLivraison").value(DEFAULT_STATUT_LIVRAISON.toString()));
    }

    @Test
    @Transactional
    void getNonExistingLivraison() throws Exception {
        // Get the livraison
        restLivraisonMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLivraison() throws Exception {
        // Initialize the database
        insertedLivraison = livraisonRepository.saveAndFlush(livraison);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the livraison
        Livraison updatedLivraison = livraisonRepository.findById(livraison.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLivraison are not directly saved in db
        em.detach(updatedLivraison);
        updatedLivraison
            .adresse(UPDATED_ADRESSE)
            .ville(UPDATED_VILLE)
            .quartier(UPDATED_QUARTIER)
            .telephone(UPDATED_TELEPHONE)
            .fraisLivraison(UPDATED_FRAIS_LIVRAISON)
            .statutLivraison(UPDATED_STATUT_LIVRAISON);
        LivraisonDTO livraisonDTO = livraisonMapper.toDto(updatedLivraison);

        restLivraisonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, livraisonDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(livraisonDTO))
            )
            .andExpect(status().isOk());

        // Validate the Livraison in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLivraisonToMatchAllProperties(updatedLivraison);
    }

    @Test
    @Transactional
    void putNonExistingLivraison() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        livraison.setId(longCount.incrementAndGet());

        // Create the Livraison
        LivraisonDTO livraisonDTO = livraisonMapper.toDto(livraison);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLivraisonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, livraisonDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(livraisonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livraison in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLivraison() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        livraison.setId(longCount.incrementAndGet());

        // Create the Livraison
        LivraisonDTO livraisonDTO = livraisonMapper.toDto(livraison);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivraisonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(livraisonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livraison in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLivraison() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        livraison.setId(longCount.incrementAndGet());

        // Create the Livraison
        LivraisonDTO livraisonDTO = livraisonMapper.toDto(livraison);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivraisonMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(livraisonDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Livraison in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLivraisonWithPatch() throws Exception {
        // Initialize the database
        insertedLivraison = livraisonRepository.saveAndFlush(livraison);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the livraison using partial update
        Livraison partialUpdatedLivraison = new Livraison();
        partialUpdatedLivraison.setId(livraison.getId());

        partialUpdatedLivraison.ville(UPDATED_VILLE).telephone(UPDATED_TELEPHONE).statutLivraison(UPDATED_STATUT_LIVRAISON);

        restLivraisonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLivraison.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLivraison))
            )
            .andExpect(status().isOk());

        // Validate the Livraison in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLivraisonUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLivraison, livraison),
            getPersistedLivraison(livraison)
        );
    }

    @Test
    @Transactional
    void fullUpdateLivraisonWithPatch() throws Exception {
        // Initialize the database
        insertedLivraison = livraisonRepository.saveAndFlush(livraison);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the livraison using partial update
        Livraison partialUpdatedLivraison = new Livraison();
        partialUpdatedLivraison.setId(livraison.getId());

        partialUpdatedLivraison
            .adresse(UPDATED_ADRESSE)
            .ville(UPDATED_VILLE)
            .quartier(UPDATED_QUARTIER)
            .telephone(UPDATED_TELEPHONE)
            .fraisLivraison(UPDATED_FRAIS_LIVRAISON)
            .statutLivraison(UPDATED_STATUT_LIVRAISON);

        restLivraisonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLivraison.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLivraison))
            )
            .andExpect(status().isOk());

        // Validate the Livraison in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLivraisonUpdatableFieldsEquals(partialUpdatedLivraison, getPersistedLivraison(partialUpdatedLivraison));
    }

    @Test
    @Transactional
    void patchNonExistingLivraison() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        livraison.setId(longCount.incrementAndGet());

        // Create the Livraison
        LivraisonDTO livraisonDTO = livraisonMapper.toDto(livraison);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLivraisonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, livraisonDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(livraisonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livraison in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLivraison() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        livraison.setId(longCount.incrementAndGet());

        // Create the Livraison
        LivraisonDTO livraisonDTO = livraisonMapper.toDto(livraison);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivraisonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(livraisonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livraison in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLivraison() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        livraison.setId(longCount.incrementAndGet());

        // Create the Livraison
        LivraisonDTO livraisonDTO = livraisonMapper.toDto(livraison);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivraisonMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(livraisonDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Livraison in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLivraison() throws Exception {
        // Initialize the database
        insertedLivraison = livraisonRepository.saveAndFlush(livraison);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the livraison
        restLivraisonMockMvc
            .perform(delete(ENTITY_API_URL_ID, livraison.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return livraisonRepository.count();
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

    protected Livraison getPersistedLivraison(Livraison livraison) {
        return livraisonRepository.findById(livraison.getId()).orElseThrow();
    }

    protected void assertPersistedLivraisonToMatchAllProperties(Livraison expectedLivraison) {
        assertLivraisonAllPropertiesEquals(expectedLivraison, getPersistedLivraison(expectedLivraison));
    }

    protected void assertPersistedLivraisonToMatchUpdatableProperties(Livraison expectedLivraison) {
        assertLivraisonAllUpdatablePropertiesEquals(expectedLivraison, getPersistedLivraison(expectedLivraison));
    }
}
