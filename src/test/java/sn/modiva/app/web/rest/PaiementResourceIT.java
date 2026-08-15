package sn.modiva.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.modiva.app.domain.PaiementAsserts.*;
import static sn.modiva.app.web.rest.TestUtil.createUpdateProxyForBean;
import static sn.modiva.app.web.rest.TestUtil.sameNumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
import sn.modiva.app.domain.Paiement;
import sn.modiva.app.domain.enumeration.StatutPaiement;
import sn.modiva.app.repository.PaiementRepository;
import sn.modiva.app.service.dto.PaiementDTO;
import sn.modiva.app.service.mapper.PaiementMapper;

/**
 * Integration tests for the {@link PaiementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PaiementResourceIT {

    private static final BigDecimal DEFAULT_MONTANT = new BigDecimal(1);
    private static final BigDecimal UPDATED_MONTANT = new BigDecimal(2);

    private static final Instant DEFAULT_DATE_PAIEMENT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_PAIEMENT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_MOYEN_PAIEMENT = "AAAAAAAAAA";
    private static final String UPDATED_MOYEN_PAIEMENT = "BBBBBBBBBB";

    private static final String DEFAULT_REFERENCE_TRANSACTION = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE_TRANSACTION = "BBBBBBBBBB";

    private static final StatutPaiement DEFAULT_STATUT_PAIEMENT = StatutPaiement.EN_ATTENTE;
    private static final StatutPaiement UPDATED_STATUT_PAIEMENT = StatutPaiement.REUSSI;

    private static final String ENTITY_API_URL = "/api/paiements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private PaiementMapper paiementMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaiementMockMvc;

    private Paiement paiement;

    private Paiement insertedPaiement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Paiement createEntity() {
        return new Paiement()
            .montant(DEFAULT_MONTANT)
            .datePaiement(DEFAULT_DATE_PAIEMENT)
            .moyenPaiement(DEFAULT_MOYEN_PAIEMENT)
            .referenceTransaction(DEFAULT_REFERENCE_TRANSACTION)
            .statutPaiement(DEFAULT_STATUT_PAIEMENT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Paiement createUpdatedEntity() {
        return new Paiement()
            .montant(UPDATED_MONTANT)
            .datePaiement(UPDATED_DATE_PAIEMENT)
            .moyenPaiement(UPDATED_MOYEN_PAIEMENT)
            .referenceTransaction(UPDATED_REFERENCE_TRANSACTION)
            .statutPaiement(UPDATED_STATUT_PAIEMENT);
    }

    @BeforeEach
    void initTest() {
        paiement = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPaiement != null) {
            paiementRepository.delete(insertedPaiement);
            insertedPaiement = null;
        }
    }

    @Test
    @Transactional
    void createPaiement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Paiement
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);
        var returnedPaiementDTO = om.readValue(
            restPaiementMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiementDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PaiementDTO.class
        );

        // Validate the Paiement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPaiement = paiementMapper.toEntity(returnedPaiementDTO);
        assertPaiementUpdatableFieldsEquals(returnedPaiement, getPersistedPaiement(returnedPaiement));

        insertedPaiement = returnedPaiement;
    }

    @Test
    @Transactional
    void createPaiementWithExistingId() throws Exception {
        // Create the Paiement with an existing ID
        paiement.setId(1L);
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaiementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiementDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Paiement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMontantIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paiement.setMontant(null);

        // Create the Paiement, which fails.
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);

        restPaiementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutPaiementIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paiement.setStatutPaiement(null);

        // Create the Paiement, which fails.
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);

        restPaiementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPaiements() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get all the paiementList
        restPaiementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paiement.getId().intValue())))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(sameNumber(DEFAULT_MONTANT))))
            .andExpect(jsonPath("$.[*].datePaiement").value(hasItem(DEFAULT_DATE_PAIEMENT.toString())))
            .andExpect(jsonPath("$.[*].moyenPaiement").value(hasItem(DEFAULT_MOYEN_PAIEMENT)))
            .andExpect(jsonPath("$.[*].referenceTransaction").value(hasItem(DEFAULT_REFERENCE_TRANSACTION)))
            .andExpect(jsonPath("$.[*].statutPaiement").value(hasItem(DEFAULT_STATUT_PAIEMENT.toString())));
    }

    @Test
    @Transactional
    void getPaiement() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        // Get the paiement
        restPaiementMockMvc
            .perform(get(ENTITY_API_URL_ID, paiement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(paiement.getId().intValue()))
            .andExpect(jsonPath("$.montant").value(sameNumber(DEFAULT_MONTANT)))
            .andExpect(jsonPath("$.datePaiement").value(DEFAULT_DATE_PAIEMENT.toString()))
            .andExpect(jsonPath("$.moyenPaiement").value(DEFAULT_MOYEN_PAIEMENT))
            .andExpect(jsonPath("$.referenceTransaction").value(DEFAULT_REFERENCE_TRANSACTION))
            .andExpect(jsonPath("$.statutPaiement").value(DEFAULT_STATUT_PAIEMENT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPaiement() throws Exception {
        // Get the paiement
        restPaiementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPaiement() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paiement
        Paiement updatedPaiement = paiementRepository.findById(paiement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPaiement are not directly saved in db
        em.detach(updatedPaiement);
        updatedPaiement
            .montant(UPDATED_MONTANT)
            .datePaiement(UPDATED_DATE_PAIEMENT)
            .moyenPaiement(UPDATED_MOYEN_PAIEMENT)
            .referenceTransaction(UPDATED_REFERENCE_TRANSACTION)
            .statutPaiement(UPDATED_STATUT_PAIEMENT);
        PaiementDTO paiementDTO = paiementMapper.toDto(updatedPaiement);

        restPaiementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paiementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paiementDTO))
            )
            .andExpect(status().isOk());

        // Validate the Paiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPaiementToMatchAllProperties(updatedPaiement);
    }

    @Test
    @Transactional
    void putNonExistingPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiement.setId(longCount.incrementAndGet());

        // Create the Paiement
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaiementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paiementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paiementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Paiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiement.setId(longCount.incrementAndGet());

        // Create the Paiement
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaiementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paiementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Paiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiement.setId(longCount.incrementAndGet());

        // Create the Paiement
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaiementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Paiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePaiementWithPatch() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paiement using partial update
        Paiement partialUpdatedPaiement = new Paiement();
        partialUpdatedPaiement.setId(paiement.getId());

        partialUpdatedPaiement.montant(UPDATED_MONTANT).datePaiement(UPDATED_DATE_PAIEMENT);

        restPaiementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaiement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaiement))
            )
            .andExpect(status().isOk());

        // Validate the Paiement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaiementUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPaiement, paiement), getPersistedPaiement(paiement));
    }

    @Test
    @Transactional
    void fullUpdatePaiementWithPatch() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paiement using partial update
        Paiement partialUpdatedPaiement = new Paiement();
        partialUpdatedPaiement.setId(paiement.getId());

        partialUpdatedPaiement
            .montant(UPDATED_MONTANT)
            .datePaiement(UPDATED_DATE_PAIEMENT)
            .moyenPaiement(UPDATED_MOYEN_PAIEMENT)
            .referenceTransaction(UPDATED_REFERENCE_TRANSACTION)
            .statutPaiement(UPDATED_STATUT_PAIEMENT);

        restPaiementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaiement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaiement))
            )
            .andExpect(status().isOk());

        // Validate the Paiement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaiementUpdatableFieldsEquals(partialUpdatedPaiement, getPersistedPaiement(partialUpdatedPaiement));
    }

    @Test
    @Transactional
    void patchNonExistingPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiement.setId(longCount.incrementAndGet());

        // Create the Paiement
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaiementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paiementDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paiementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Paiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiement.setId(longCount.incrementAndGet());

        // Create the Paiement
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaiementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paiementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Paiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiement.setId(longCount.incrementAndGet());

        // Create the Paiement
        PaiementDTO paiementDTO = paiementMapper.toDto(paiement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaiementMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(paiementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Paiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePaiement() throws Exception {
        // Initialize the database
        insertedPaiement = paiementRepository.saveAndFlush(paiement);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the paiement
        restPaiementMockMvc
            .perform(delete(ENTITY_API_URL_ID, paiement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return paiementRepository.count();
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

    protected Paiement getPersistedPaiement(Paiement paiement) {
        return paiementRepository.findById(paiement.getId()).orElseThrow();
    }

    protected void assertPersistedPaiementToMatchAllProperties(Paiement expectedPaiement) {
        assertPaiementAllPropertiesEquals(expectedPaiement, getPersistedPaiement(expectedPaiement));
    }

    protected void assertPersistedPaiementToMatchUpdatableProperties(Paiement expectedPaiement) {
        assertPaiementAllUpdatablePropertiesEquals(expectedPaiement, getPersistedPaiement(expectedPaiement));
    }
}
