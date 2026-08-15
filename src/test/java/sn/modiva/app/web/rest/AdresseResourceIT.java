package sn.modiva.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.modiva.app.domain.AdresseAsserts.*;
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
import sn.modiva.app.domain.Adresse;
import sn.modiva.app.domain.ClienteProfile;
import sn.modiva.app.repository.AdresseRepository;
import sn.modiva.app.service.dto.AdresseDTO;
import sn.modiva.app.service.mapper.AdresseMapper;

/**
 * Integration tests for the {@link AdresseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AdresseResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESSE = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE = "BBBBBBBBBB";

    private static final String DEFAULT_VILLE = "AAAAAAAAAA";
    private static final String UPDATED_VILLE = "BBBBBBBBBB";

    private static final String DEFAULT_QUARTIER = "AAAAAAAAAA";
    private static final String UPDATED_QUARTIER = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_PAR_DEFAUT = false;
    private static final Boolean UPDATED_PAR_DEFAUT = true;

    private static final String ENTITY_API_URL = "/api/adresses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AdresseRepository adresseRepository;

    @Autowired
    private AdresseMapper adresseMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAdresseMockMvc;

    private Adresse adresse;

    private Adresse insertedAdresse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Adresse createEntity(EntityManager em) {
        Adresse adresse = new Adresse()
            .libelle(DEFAULT_LIBELLE)
            .adresse(DEFAULT_ADRESSE)
            .ville(DEFAULT_VILLE)
            .quartier(DEFAULT_QUARTIER)
            .telephone(DEFAULT_TELEPHONE)
            .parDefaut(DEFAULT_PAR_DEFAUT);
        // Add required entity
        ClienteProfile clienteProfile;
        if (TestUtil.findAll(em, ClienteProfile.class).isEmpty()) {
            clienteProfile = ClienteProfileResourceIT.createEntity(em);
            em.persist(clienteProfile);
            em.flush();
        } else {
            clienteProfile = TestUtil.findAll(em, ClienteProfile.class).get(0);
        }
        adresse.setClienteProfile(clienteProfile);
        return adresse;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Adresse createUpdatedEntity(EntityManager em) {
        Adresse updatedAdresse = new Adresse()
            .libelle(UPDATED_LIBELLE)
            .adresse(UPDATED_ADRESSE)
            .ville(UPDATED_VILLE)
            .quartier(UPDATED_QUARTIER)
            .telephone(UPDATED_TELEPHONE)
            .parDefaut(UPDATED_PAR_DEFAUT);
        // Add required entity
        ClienteProfile clienteProfile;
        if (TestUtil.findAll(em, ClienteProfile.class).isEmpty()) {
            clienteProfile = ClienteProfileResourceIT.createUpdatedEntity(em);
            em.persist(clienteProfile);
            em.flush();
        } else {
            clienteProfile = TestUtil.findAll(em, ClienteProfile.class).get(0);
        }
        updatedAdresse.setClienteProfile(clienteProfile);
        return updatedAdresse;
    }

    @BeforeEach
    void initTest() {
        adresse = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAdresse != null) {
            adresseRepository.delete(insertedAdresse);
            insertedAdresse = null;
        }
    }

    @Test
    @Transactional
    void createAdresse() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Adresse
        AdresseDTO adresseDTO = adresseMapper.toDto(adresse);
        var returnedAdresseDTO = om.readValue(
            restAdresseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(adresseDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AdresseDTO.class
        );

        // Validate the Adresse in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAdresse = adresseMapper.toEntity(returnedAdresseDTO);
        assertAdresseUpdatableFieldsEquals(returnedAdresse, getPersistedAdresse(returnedAdresse));

        insertedAdresse = returnedAdresse;
    }

    @Test
    @Transactional
    void createAdresseWithExistingId() throws Exception {
        // Create the Adresse with an existing ID
        adresse.setId(1L);
        AdresseDTO adresseDTO = adresseMapper.toDto(adresse);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAdresseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(adresseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Adresse in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAdresseIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        adresse.setAdresse(null);

        // Create the Adresse, which fails.
        AdresseDTO adresseDTO = adresseMapper.toDto(adresse);

        restAdresseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(adresseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVilleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        adresse.setVille(null);

        // Create the Adresse, which fails.
        AdresseDTO adresseDTO = adresseMapper.toDto(adresse);

        restAdresseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(adresseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAdresses() throws Exception {
        // Initialize the database
        insertedAdresse = adresseRepository.saveAndFlush(adresse);

        // Get all the adresseList
        restAdresseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(adresse.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)))
            .andExpect(jsonPath("$.[*].ville").value(hasItem(DEFAULT_VILLE)))
            .andExpect(jsonPath("$.[*].quartier").value(hasItem(DEFAULT_QUARTIER)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].parDefaut").value(hasItem(DEFAULT_PAR_DEFAUT)));
    }

    @Test
    @Transactional
    void getAdresse() throws Exception {
        // Initialize the database
        insertedAdresse = adresseRepository.saveAndFlush(adresse);

        // Get the adresse
        restAdresseMockMvc
            .perform(get(ENTITY_API_URL_ID, adresse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(adresse.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE))
            .andExpect(jsonPath("$.adresse").value(DEFAULT_ADRESSE))
            .andExpect(jsonPath("$.ville").value(DEFAULT_VILLE))
            .andExpect(jsonPath("$.quartier").value(DEFAULT_QUARTIER))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE))
            .andExpect(jsonPath("$.parDefaut").value(DEFAULT_PAR_DEFAUT));
    }

    @Test
    @Transactional
    void getNonExistingAdresse() throws Exception {
        // Get the adresse
        restAdresseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAdresse() throws Exception {
        // Initialize the database
        insertedAdresse = adresseRepository.saveAndFlush(adresse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the adresse
        Adresse updatedAdresse = adresseRepository.findById(adresse.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAdresse are not directly saved in db
        em.detach(updatedAdresse);
        updatedAdresse
            .libelle(UPDATED_LIBELLE)
            .adresse(UPDATED_ADRESSE)
            .ville(UPDATED_VILLE)
            .quartier(UPDATED_QUARTIER)
            .telephone(UPDATED_TELEPHONE)
            .parDefaut(UPDATED_PAR_DEFAUT);
        AdresseDTO adresseDTO = adresseMapper.toDto(updatedAdresse);

        restAdresseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, adresseDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(adresseDTO))
            )
            .andExpect(status().isOk());

        // Validate the Adresse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAdresseToMatchAllProperties(updatedAdresse);
    }

    @Test
    @Transactional
    void putNonExistingAdresse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        adresse.setId(longCount.incrementAndGet());

        // Create the Adresse
        AdresseDTO adresseDTO = adresseMapper.toDto(adresse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAdresseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, adresseDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(adresseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Adresse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAdresse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        adresse.setId(longCount.incrementAndGet());

        // Create the Adresse
        AdresseDTO adresseDTO = adresseMapper.toDto(adresse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdresseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(adresseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Adresse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAdresse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        adresse.setId(longCount.incrementAndGet());

        // Create the Adresse
        AdresseDTO adresseDTO = adresseMapper.toDto(adresse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdresseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(adresseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Adresse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAdresseWithPatch() throws Exception {
        // Initialize the database
        insertedAdresse = adresseRepository.saveAndFlush(adresse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the adresse using partial update
        Adresse partialUpdatedAdresse = new Adresse();
        partialUpdatedAdresse.setId(adresse.getId());

        partialUpdatedAdresse.libelle(UPDATED_LIBELLE).adresse(UPDATED_ADRESSE).ville(UPDATED_VILLE).quartier(UPDATED_QUARTIER);

        restAdresseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAdresse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAdresse))
            )
            .andExpect(status().isOk());

        // Validate the Adresse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAdresseUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAdresse, adresse), getPersistedAdresse(adresse));
    }

    @Test
    @Transactional
    void fullUpdateAdresseWithPatch() throws Exception {
        // Initialize the database
        insertedAdresse = adresseRepository.saveAndFlush(adresse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the adresse using partial update
        Adresse partialUpdatedAdresse = new Adresse();
        partialUpdatedAdresse.setId(adresse.getId());

        partialUpdatedAdresse
            .libelle(UPDATED_LIBELLE)
            .adresse(UPDATED_ADRESSE)
            .ville(UPDATED_VILLE)
            .quartier(UPDATED_QUARTIER)
            .telephone(UPDATED_TELEPHONE)
            .parDefaut(UPDATED_PAR_DEFAUT);

        restAdresseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAdresse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAdresse))
            )
            .andExpect(status().isOk());

        // Validate the Adresse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAdresseUpdatableFieldsEquals(partialUpdatedAdresse, getPersistedAdresse(partialUpdatedAdresse));
    }

    @Test
    @Transactional
    void patchNonExistingAdresse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        adresse.setId(longCount.incrementAndGet());

        // Create the Adresse
        AdresseDTO adresseDTO = adresseMapper.toDto(adresse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAdresseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, adresseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(adresseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Adresse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAdresse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        adresse.setId(longCount.incrementAndGet());

        // Create the Adresse
        AdresseDTO adresseDTO = adresseMapper.toDto(adresse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdresseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(adresseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Adresse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAdresse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        adresse.setId(longCount.incrementAndGet());

        // Create the Adresse
        AdresseDTO adresseDTO = adresseMapper.toDto(adresse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdresseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(adresseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Adresse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAdresse() throws Exception {
        // Initialize the database
        insertedAdresse = adresseRepository.saveAndFlush(adresse);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the adresse
        restAdresseMockMvc
            .perform(delete(ENTITY_API_URL_ID, adresse.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return adresseRepository.count();
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

    protected Adresse getPersistedAdresse(Adresse adresse) {
        return adresseRepository.findById(adresse.getId()).orElseThrow();
    }

    protected void assertPersistedAdresseToMatchAllProperties(Adresse expectedAdresse) {
        assertAdresseAllPropertiesEquals(expectedAdresse, getPersistedAdresse(expectedAdresse));
    }

    protected void assertPersistedAdresseToMatchUpdatableProperties(Adresse expectedAdresse) {
        assertAdresseAllUpdatablePropertiesEquals(expectedAdresse, getPersistedAdresse(expectedAdresse));
    }
}
