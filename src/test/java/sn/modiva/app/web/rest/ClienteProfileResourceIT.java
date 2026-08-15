package sn.modiva.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.modiva.app.domain.ClienteProfileAsserts.*;
import static sn.modiva.app.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sn.modiva.app.IntegrationTest;
import sn.modiva.app.domain.ClienteProfile;
import sn.modiva.app.domain.Panier;
import sn.modiva.app.domain.User;
import sn.modiva.app.repository.ClienteProfileRepository;
import sn.modiva.app.repository.UserRepository;
import sn.modiva.app.service.ClienteProfileService;
import sn.modiva.app.service.dto.ClienteProfileDTO;
import sn.modiva.app.service.mapper.ClienteProfileMapper;

/**
 * Integration tests for the {@link ClienteProfileResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ClienteProfileResourceIT {

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIF = false;
    private static final Boolean UPDATED_ACTIF = true;

    private static final String ENTITY_API_URL = "/api/cliente-profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClienteProfileRepository clienteProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ClienteProfileRepository clienteProfileRepositoryMock;

    @Autowired
    private ClienteProfileMapper clienteProfileMapper;

    @Mock
    private ClienteProfileService clienteProfileServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClienteProfileMockMvc;

    private ClienteProfile clienteProfile;

    private ClienteProfile insertedClienteProfile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClienteProfile createEntity(EntityManager em) {
        ClienteProfile clienteProfile = new ClienteProfile().telephone(DEFAULT_TELEPHONE).actif(DEFAULT_ACTIF);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        clienteProfile.setUser(user);
        return clienteProfile;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClienteProfile createUpdatedEntity(EntityManager em) {
        ClienteProfile updatedClienteProfile = new ClienteProfile().telephone(UPDATED_TELEPHONE).actif(UPDATED_ACTIF);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedClienteProfile.setUser(user);
        return updatedClienteProfile;
    }

    @BeforeEach
    void initTest() {
        clienteProfile = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedClienteProfile != null) {
            clienteProfileRepository.delete(insertedClienteProfile);
            insertedClienteProfile = null;
        }
    }

    @Test
    @Transactional
    void createClienteProfile() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ClienteProfile
        ClienteProfileDTO clienteProfileDTO = clienteProfileMapper.toDto(clienteProfile);
        var returnedClienteProfileDTO = om.readValue(
            restClienteProfileMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clienteProfileDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClienteProfileDTO.class
        );

        // Validate the ClienteProfile in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedClienteProfile = clienteProfileMapper.toEntity(returnedClienteProfileDTO);
        assertClienteProfileUpdatableFieldsEquals(returnedClienteProfile, getPersistedClienteProfile(returnedClienteProfile));

        insertedClienteProfile = returnedClienteProfile;
    }

    @Test
    @Transactional
    void createClienteProfileWithExistingId() throws Exception {
        // Create the ClienteProfile with an existing ID
        clienteProfile.setId(1L);
        ClienteProfileDTO clienteProfileDTO = clienteProfileMapper.toDto(clienteProfile);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClienteProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clienteProfileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ClienteProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllClienteProfiles() throws Exception {
        // Initialize the database
        insertedClienteProfile = clienteProfileRepository.saveAndFlush(clienteProfile);

        // Get all the clienteProfileList
        restClienteProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clienteProfile.getId().intValue())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClienteProfilesWithEagerRelationshipsIsEnabled() throws Exception {
        when(clienteProfileServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClienteProfileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(clienteProfileServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClienteProfilesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(clienteProfileServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClienteProfileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(clienteProfileRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getClienteProfile() throws Exception {
        // Initialize the database
        insertedClienteProfile = clienteProfileRepository.saveAndFlush(clienteProfile);

        // Get the clienteProfile
        restClienteProfileMockMvc
            .perform(get(ENTITY_API_URL_ID, clienteProfile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clienteProfile.getId().intValue()))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE))
            .andExpect(jsonPath("$.actif").value(DEFAULT_ACTIF));
    }

    @Test
    @Transactional
    void getClienteProfilesByIdFiltering() throws Exception {
        // Initialize the database
        insertedClienteProfile = clienteProfileRepository.saveAndFlush(clienteProfile);

        Long id = clienteProfile.getId();

        defaultClienteProfileFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultClienteProfileFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultClienteProfileFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllClienteProfilesByTelephoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClienteProfile = clienteProfileRepository.saveAndFlush(clienteProfile);

        // Get all the clienteProfileList where telephone equals to
        defaultClienteProfileFiltering("telephone.equals=" + DEFAULT_TELEPHONE, "telephone.equals=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllClienteProfilesByTelephoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClienteProfile = clienteProfileRepository.saveAndFlush(clienteProfile);

        // Get all the clienteProfileList where telephone in
        defaultClienteProfileFiltering("telephone.in=" + DEFAULT_TELEPHONE + "," + UPDATED_TELEPHONE, "telephone.in=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllClienteProfilesByTelephoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClienteProfile = clienteProfileRepository.saveAndFlush(clienteProfile);

        // Get all the clienteProfileList where telephone is not null
        defaultClienteProfileFiltering("telephone.specified=true", "telephone.specified=false");
    }

    @Test
    @Transactional
    void getAllClienteProfilesByTelephoneContainsSomething() throws Exception {
        // Initialize the database
        insertedClienteProfile = clienteProfileRepository.saveAndFlush(clienteProfile);

        // Get all the clienteProfileList where telephone contains
        defaultClienteProfileFiltering("telephone.contains=" + DEFAULT_TELEPHONE, "telephone.contains=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllClienteProfilesByTelephoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedClienteProfile = clienteProfileRepository.saveAndFlush(clienteProfile);

        // Get all the clienteProfileList where telephone does not contain
        defaultClienteProfileFiltering("telephone.doesNotContain=" + UPDATED_TELEPHONE, "telephone.doesNotContain=" + DEFAULT_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllClienteProfilesByActifIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedClienteProfile = clienteProfileRepository.saveAndFlush(clienteProfile);

        // Get all the clienteProfileList where actif equals to
        defaultClienteProfileFiltering("actif.equals=" + DEFAULT_ACTIF, "actif.equals=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllClienteProfilesByActifIsInShouldWork() throws Exception {
        // Initialize the database
        insertedClienteProfile = clienteProfileRepository.saveAndFlush(clienteProfile);

        // Get all the clienteProfileList where actif in
        defaultClienteProfileFiltering("actif.in=" + DEFAULT_ACTIF + "," + UPDATED_ACTIF, "actif.in=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllClienteProfilesByActifIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedClienteProfile = clienteProfileRepository.saveAndFlush(clienteProfile);

        // Get all the clienteProfileList where actif is not null
        defaultClienteProfileFiltering("actif.specified=true", "actif.specified=false");
    }

    @Test
    @Transactional
    void getAllClienteProfilesByUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        User user = clienteProfile.getUser();
        clienteProfileRepository.saveAndFlush(clienteProfile);
        Long userId = user.getId();
        // Get all the clienteProfileList where user equals to userId
        defaultClienteProfileShouldBeFound("userId.equals=" + userId);

        // Get all the clienteProfileList where user equals to (userId + 1)
        defaultClienteProfileShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllClienteProfilesByPanierIsEqualToSomething() throws Exception {
        Panier panier;
        if (TestUtil.findAll(em, Panier.class).isEmpty()) {
            clienteProfileRepository.saveAndFlush(clienteProfile);
            panier = PanierResourceIT.createEntity();
        } else {
            panier = TestUtil.findAll(em, Panier.class).get(0);
        }
        em.persist(panier);
        em.flush();
        clienteProfile.setPanier(panier);
        clienteProfileRepository.saveAndFlush(clienteProfile);
        Long panierId = panier.getId();
        // Get all the clienteProfileList where panier equals to panierId
        defaultClienteProfileShouldBeFound("panierId.equals=" + panierId);

        // Get all the clienteProfileList where panier equals to (panierId + 1)
        defaultClienteProfileShouldNotBeFound("panierId.equals=" + (panierId + 1));
    }

    private void defaultClienteProfileFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultClienteProfileShouldBeFound(shouldBeFound);
        defaultClienteProfileShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClienteProfileShouldBeFound(String filter) throws Exception {
        restClienteProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clienteProfile.getId().intValue())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));

        // Check, that the count call also returns 1
        restClienteProfileMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClienteProfileShouldNotBeFound(String filter) throws Exception {
        restClienteProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClienteProfileMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingClienteProfile() throws Exception {
        // Get the clienteProfile
        restClienteProfileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClienteProfile() throws Exception {
        // Initialize the database
        insertedClienteProfile = clienteProfileRepository.saveAndFlush(clienteProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clienteProfile
        ClienteProfile updatedClienteProfile = clienteProfileRepository.findById(clienteProfile.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClienteProfile are not directly saved in db
        em.detach(updatedClienteProfile);
        updatedClienteProfile.telephone(UPDATED_TELEPHONE).actif(UPDATED_ACTIF);
        ClienteProfileDTO clienteProfileDTO = clienteProfileMapper.toDto(updatedClienteProfile);

        restClienteProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clienteProfileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clienteProfileDTO))
            )
            .andExpect(status().isOk());

        // Validate the ClienteProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClienteProfileToMatchAllProperties(updatedClienteProfile);
    }

    @Test
    @Transactional
    void putNonExistingClienteProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clienteProfile.setId(longCount.incrementAndGet());

        // Create the ClienteProfile
        ClienteProfileDTO clienteProfileDTO = clienteProfileMapper.toDto(clienteProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClienteProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clienteProfileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clienteProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClienteProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClienteProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clienteProfile.setId(longCount.incrementAndGet());

        // Create the ClienteProfile
        ClienteProfileDTO clienteProfileDTO = clienteProfileMapper.toDto(clienteProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClienteProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(clienteProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClienteProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClienteProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clienteProfile.setId(longCount.incrementAndGet());

        // Create the ClienteProfile
        ClienteProfileDTO clienteProfileDTO = clienteProfileMapper.toDto(clienteProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClienteProfileMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(clienteProfileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClienteProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClienteProfileWithPatch() throws Exception {
        // Initialize the database
        insertedClienteProfile = clienteProfileRepository.saveAndFlush(clienteProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clienteProfile using partial update
        ClienteProfile partialUpdatedClienteProfile = new ClienteProfile();
        partialUpdatedClienteProfile.setId(clienteProfile.getId());

        partialUpdatedClienteProfile.actif(UPDATED_ACTIF);

        restClienteProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClienteProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClienteProfile))
            )
            .andExpect(status().isOk());

        // Validate the ClienteProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClienteProfileUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedClienteProfile, clienteProfile),
            getPersistedClienteProfile(clienteProfile)
        );
    }

    @Test
    @Transactional
    void fullUpdateClienteProfileWithPatch() throws Exception {
        // Initialize the database
        insertedClienteProfile = clienteProfileRepository.saveAndFlush(clienteProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clienteProfile using partial update
        ClienteProfile partialUpdatedClienteProfile = new ClienteProfile();
        partialUpdatedClienteProfile.setId(clienteProfile.getId());

        partialUpdatedClienteProfile.telephone(UPDATED_TELEPHONE).actif(UPDATED_ACTIF);

        restClienteProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClienteProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClienteProfile))
            )
            .andExpect(status().isOk());

        // Validate the ClienteProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClienteProfileUpdatableFieldsEquals(partialUpdatedClienteProfile, getPersistedClienteProfile(partialUpdatedClienteProfile));
    }

    @Test
    @Transactional
    void patchNonExistingClienteProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clienteProfile.setId(longCount.incrementAndGet());

        // Create the ClienteProfile
        ClienteProfileDTO clienteProfileDTO = clienteProfileMapper.toDto(clienteProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClienteProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clienteProfileDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clienteProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClienteProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClienteProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clienteProfile.setId(longCount.incrementAndGet());

        // Create the ClienteProfile
        ClienteProfileDTO clienteProfileDTO = clienteProfileMapper.toDto(clienteProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClienteProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(clienteProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClienteProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClienteProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clienteProfile.setId(longCount.incrementAndGet());

        // Create the ClienteProfile
        ClienteProfileDTO clienteProfileDTO = clienteProfileMapper.toDto(clienteProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClienteProfileMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(clienteProfileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClienteProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClienteProfile() throws Exception {
        // Initialize the database
        insertedClienteProfile = clienteProfileRepository.saveAndFlush(clienteProfile);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the clienteProfile
        restClienteProfileMockMvc
            .perform(delete(ENTITY_API_URL_ID, clienteProfile.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return clienteProfileRepository.count();
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

    protected ClienteProfile getPersistedClienteProfile(ClienteProfile clienteProfile) {
        return clienteProfileRepository.findById(clienteProfile.getId()).orElseThrow();
    }

    protected void assertPersistedClienteProfileToMatchAllProperties(ClienteProfile expectedClienteProfile) {
        assertClienteProfileAllPropertiesEquals(expectedClienteProfile, getPersistedClienteProfile(expectedClienteProfile));
    }

    protected void assertPersistedClienteProfileToMatchUpdatableProperties(ClienteProfile expectedClienteProfile) {
        assertClienteProfileAllUpdatablePropertiesEquals(expectedClienteProfile, getPersistedClienteProfile(expectedClienteProfile));
    }
}
