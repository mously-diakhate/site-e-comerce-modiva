package sn.modiva.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.modiva.app.domain.LigneCommandeAsserts.*;
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
import sn.modiva.app.domain.Commande;
import sn.modiva.app.domain.LigneCommande;
import sn.modiva.app.domain.VarianteProduit;
import sn.modiva.app.repository.LigneCommandeRepository;
import sn.modiva.app.service.dto.LigneCommandeDTO;
import sn.modiva.app.service.mapper.LigneCommandeMapper;

/**
 * Integration tests for the {@link LigneCommandeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LigneCommandeResourceIT {

    private static final Integer DEFAULT_QUANTITE = 1;
    private static final Integer UPDATED_QUANTITE = 2;

    private static final BigDecimal DEFAULT_PRIX_UNITAIRE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRIX_UNITAIRE = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/ligne-commandes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;

    @Autowired
    private LigneCommandeMapper ligneCommandeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLigneCommandeMockMvc;

    private LigneCommande ligneCommande;

    private LigneCommande insertedLigneCommande;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LigneCommande createEntity(EntityManager em) {
        LigneCommande ligneCommande = new LigneCommande().quantite(DEFAULT_QUANTITE).prixUnitaire(DEFAULT_PRIX_UNITAIRE);
        // Add required entity
        VarianteProduit varianteProduit;
        if (TestUtil.findAll(em, VarianteProduit.class).isEmpty()) {
            varianteProduit = VarianteProduitResourceIT.createEntity(em);
            em.persist(varianteProduit);
            em.flush();
        } else {
            varianteProduit = TestUtil.findAll(em, VarianteProduit.class).get(0);
        }
        ligneCommande.setVariante(varianteProduit);
        // Add required entity
        Commande commande;
        if (TestUtil.findAll(em, Commande.class).isEmpty()) {
            commande = CommandeResourceIT.createEntity(em);
            em.persist(commande);
            em.flush();
        } else {
            commande = TestUtil.findAll(em, Commande.class).get(0);
        }
        ligneCommande.setCommande(commande);
        return ligneCommande;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LigneCommande createUpdatedEntity(EntityManager em) {
        LigneCommande updatedLigneCommande = new LigneCommande().quantite(UPDATED_QUANTITE).prixUnitaire(UPDATED_PRIX_UNITAIRE);
        // Add required entity
        VarianteProduit varianteProduit;
        if (TestUtil.findAll(em, VarianteProduit.class).isEmpty()) {
            varianteProduit = VarianteProduitResourceIT.createUpdatedEntity(em);
            em.persist(varianteProduit);
            em.flush();
        } else {
            varianteProduit = TestUtil.findAll(em, VarianteProduit.class).get(0);
        }
        updatedLigneCommande.setVariante(varianteProduit);
        // Add required entity
        Commande commande;
        if (TestUtil.findAll(em, Commande.class).isEmpty()) {
            commande = CommandeResourceIT.createUpdatedEntity(em);
            em.persist(commande);
            em.flush();
        } else {
            commande = TestUtil.findAll(em, Commande.class).get(0);
        }
        updatedLigneCommande.setCommande(commande);
        return updatedLigneCommande;
    }

    @BeforeEach
    void initTest() {
        ligneCommande = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedLigneCommande != null) {
            ligneCommandeRepository.delete(insertedLigneCommande);
            insertedLigneCommande = null;
        }
    }

    @Test
    @Transactional
    void createLigneCommande() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LigneCommande
        LigneCommandeDTO ligneCommandeDTO = ligneCommandeMapper.toDto(ligneCommande);
        var returnedLigneCommandeDTO = om.readValue(
            restLigneCommandeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneCommandeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LigneCommandeDTO.class
        );

        // Validate the LigneCommande in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLigneCommande = ligneCommandeMapper.toEntity(returnedLigneCommandeDTO);
        assertLigneCommandeUpdatableFieldsEquals(returnedLigneCommande, getPersistedLigneCommande(returnedLigneCommande));

        insertedLigneCommande = returnedLigneCommande;
    }

    @Test
    @Transactional
    void createLigneCommandeWithExistingId() throws Exception {
        // Create the LigneCommande with an existing ID
        ligneCommande.setId(1L);
        LigneCommandeDTO ligneCommandeDTO = ligneCommandeMapper.toDto(ligneCommande);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLigneCommandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneCommandeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LigneCommande in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantiteIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ligneCommande.setQuantite(null);

        // Create the LigneCommande, which fails.
        LigneCommandeDTO ligneCommandeDTO = ligneCommandeMapper.toDto(ligneCommande);

        restLigneCommandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneCommandeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrixUnitaireIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ligneCommande.setPrixUnitaire(null);

        // Create the LigneCommande, which fails.
        LigneCommandeDTO ligneCommandeDTO = ligneCommandeMapper.toDto(ligneCommande);

        restLigneCommandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneCommandeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLigneCommandes() throws Exception {
        // Initialize the database
        insertedLigneCommande = ligneCommandeRepository.saveAndFlush(ligneCommande);

        // Get all the ligneCommandeList
        restLigneCommandeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ligneCommande.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantite").value(hasItem(DEFAULT_QUANTITE)))
            .andExpect(jsonPath("$.[*].prixUnitaire").value(hasItem(sameNumber(DEFAULT_PRIX_UNITAIRE))));
    }

    @Test
    @Transactional
    void getLigneCommande() throws Exception {
        // Initialize the database
        insertedLigneCommande = ligneCommandeRepository.saveAndFlush(ligneCommande);

        // Get the ligneCommande
        restLigneCommandeMockMvc
            .perform(get(ENTITY_API_URL_ID, ligneCommande.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ligneCommande.getId().intValue()))
            .andExpect(jsonPath("$.quantite").value(DEFAULT_QUANTITE))
            .andExpect(jsonPath("$.prixUnitaire").value(sameNumber(DEFAULT_PRIX_UNITAIRE)));
    }

    @Test
    @Transactional
    void getNonExistingLigneCommande() throws Exception {
        // Get the ligneCommande
        restLigneCommandeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLigneCommande() throws Exception {
        // Initialize the database
        insertedLigneCommande = ligneCommandeRepository.saveAndFlush(ligneCommande);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneCommande
        LigneCommande updatedLigneCommande = ligneCommandeRepository.findById(ligneCommande.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLigneCommande are not directly saved in db
        em.detach(updatedLigneCommande);
        updatedLigneCommande.quantite(UPDATED_QUANTITE).prixUnitaire(UPDATED_PRIX_UNITAIRE);
        LigneCommandeDTO ligneCommandeDTO = ligneCommandeMapper.toDto(updatedLigneCommande);

        restLigneCommandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ligneCommandeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneCommandeDTO))
            )
            .andExpect(status().isOk());

        // Validate the LigneCommande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLigneCommandeToMatchAllProperties(updatedLigneCommande);
    }

    @Test
    @Transactional
    void putNonExistingLigneCommande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneCommande.setId(longCount.incrementAndGet());

        // Create the LigneCommande
        LigneCommandeDTO ligneCommandeDTO = ligneCommandeMapper.toDto(ligneCommande);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLigneCommandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ligneCommandeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneCommandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneCommande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLigneCommande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneCommande.setId(longCount.incrementAndGet());

        // Create the LigneCommande
        LigneCommandeDTO ligneCommandeDTO = ligneCommandeMapper.toDto(ligneCommande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneCommandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneCommandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneCommande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLigneCommande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneCommande.setId(longCount.incrementAndGet());

        // Create the LigneCommande
        LigneCommandeDTO ligneCommandeDTO = ligneCommandeMapper.toDto(ligneCommande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneCommandeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneCommandeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LigneCommande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLigneCommandeWithPatch() throws Exception {
        // Initialize the database
        insertedLigneCommande = ligneCommandeRepository.saveAndFlush(ligneCommande);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneCommande using partial update
        LigneCommande partialUpdatedLigneCommande = new LigneCommande();
        partialUpdatedLigneCommande.setId(ligneCommande.getId());

        partialUpdatedLigneCommande.prixUnitaire(UPDATED_PRIX_UNITAIRE);

        restLigneCommandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLigneCommande.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLigneCommande))
            )
            .andExpect(status().isOk());

        // Validate the LigneCommande in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLigneCommandeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLigneCommande, ligneCommande),
            getPersistedLigneCommande(ligneCommande)
        );
    }

    @Test
    @Transactional
    void fullUpdateLigneCommandeWithPatch() throws Exception {
        // Initialize the database
        insertedLigneCommande = ligneCommandeRepository.saveAndFlush(ligneCommande);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneCommande using partial update
        LigneCommande partialUpdatedLigneCommande = new LigneCommande();
        partialUpdatedLigneCommande.setId(ligneCommande.getId());

        partialUpdatedLigneCommande.quantite(UPDATED_QUANTITE).prixUnitaire(UPDATED_PRIX_UNITAIRE);

        restLigneCommandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLigneCommande.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLigneCommande))
            )
            .andExpect(status().isOk());

        // Validate the LigneCommande in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLigneCommandeUpdatableFieldsEquals(partialUpdatedLigneCommande, getPersistedLigneCommande(partialUpdatedLigneCommande));
    }

    @Test
    @Transactional
    void patchNonExistingLigneCommande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneCommande.setId(longCount.incrementAndGet());

        // Create the LigneCommande
        LigneCommandeDTO ligneCommandeDTO = ligneCommandeMapper.toDto(ligneCommande);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLigneCommandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ligneCommandeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ligneCommandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneCommande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLigneCommande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneCommande.setId(longCount.incrementAndGet());

        // Create the LigneCommande
        LigneCommandeDTO ligneCommandeDTO = ligneCommandeMapper.toDto(ligneCommande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneCommandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ligneCommandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneCommande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLigneCommande() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneCommande.setId(longCount.incrementAndGet());

        // Create the LigneCommande
        LigneCommandeDTO ligneCommandeDTO = ligneCommandeMapper.toDto(ligneCommande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneCommandeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ligneCommandeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LigneCommande in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLigneCommande() throws Exception {
        // Initialize the database
        insertedLigneCommande = ligneCommandeRepository.saveAndFlush(ligneCommande);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ligneCommande
        restLigneCommandeMockMvc
            .perform(delete(ENTITY_API_URL_ID, ligneCommande.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ligneCommandeRepository.count();
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

    protected LigneCommande getPersistedLigneCommande(LigneCommande ligneCommande) {
        return ligneCommandeRepository.findById(ligneCommande.getId()).orElseThrow();
    }

    protected void assertPersistedLigneCommandeToMatchAllProperties(LigneCommande expectedLigneCommande) {
        assertLigneCommandeAllPropertiesEquals(expectedLigneCommande, getPersistedLigneCommande(expectedLigneCommande));
    }

    protected void assertPersistedLigneCommandeToMatchUpdatableProperties(LigneCommande expectedLigneCommande) {
        assertLigneCommandeAllUpdatablePropertiesEquals(expectedLigneCommande, getPersistedLigneCommande(expectedLigneCommande));
    }
}
