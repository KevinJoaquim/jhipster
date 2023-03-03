package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ResourceData;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.ResourceDataRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ResourceDataResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ResourceDataResourceIT {

    private static final Float DEFAULT_GOLD = 1F;
    private static final Float UPDATED_GOLD = 2F;

    private static final Float DEFAULT_WOOD = 1F;
    private static final Float UPDATED_WOOD = 2F;

    private static final Float DEFAULT_FER = 1F;
    private static final Float UPDATED_FER = 2F;

    private static final String ENTITY_API_URL = "/api/resource-data";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ResourceDataRepository resourceDataRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ResourceData resourceData;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ResourceData createEntity(EntityManager em) {
        ResourceData resourceData = new ResourceData().gold(DEFAULT_GOLD).wood(DEFAULT_WOOD).fer(DEFAULT_FER);
        return resourceData;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ResourceData createUpdatedEntity(EntityManager em) {
        ResourceData resourceData = new ResourceData().gold(UPDATED_GOLD).wood(UPDATED_WOOD).fer(UPDATED_FER);
        return resourceData;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ResourceData.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        resourceData = createEntity(em);
    }

    @Test
    void createResourceData() throws Exception {
        int databaseSizeBeforeCreate = resourceDataRepository.findAll().collectList().block().size();
        // Create the ResourceData
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resourceData))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ResourceData in the database
        List<ResourceData> resourceDataList = resourceDataRepository.findAll().collectList().block();
        assertThat(resourceDataList).hasSize(databaseSizeBeforeCreate + 1);
        ResourceData testResourceData = resourceDataList.get(resourceDataList.size() - 1);
        assertThat(testResourceData.getGold()).isEqualTo(DEFAULT_GOLD);
        assertThat(testResourceData.getWood()).isEqualTo(DEFAULT_WOOD);
        assertThat(testResourceData.getFer()).isEqualTo(DEFAULT_FER);
    }

    @Test
    void createResourceDataWithExistingId() throws Exception {
        // Create the ResourceData with an existing ID
        resourceData.setId(1L);

        int databaseSizeBeforeCreate = resourceDataRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resourceData))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ResourceData in the database
        List<ResourceData> resourceDataList = resourceDataRepository.findAll().collectList().block();
        assertThat(resourceDataList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllResourceDataAsStream() {
        // Initialize the database
        resourceDataRepository.save(resourceData).block();

        List<ResourceData> resourceDataList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ResourceData.class)
            .getResponseBody()
            .filter(resourceData::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(resourceDataList).isNotNull();
        assertThat(resourceDataList).hasSize(1);
        ResourceData testResourceData = resourceDataList.get(0);
        assertThat(testResourceData.getGold()).isEqualTo(DEFAULT_GOLD);
        assertThat(testResourceData.getWood()).isEqualTo(DEFAULT_WOOD);
        assertThat(testResourceData.getFer()).isEqualTo(DEFAULT_FER);
    }

    @Test
    void getAllResourceData() {
        // Initialize the database
        resourceDataRepository.save(resourceData).block();

        // Get all the resourceDataList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(resourceData.getId().intValue()))
            .jsonPath("$.[*].gold")
            .value(hasItem(DEFAULT_GOLD.doubleValue()))
            .jsonPath("$.[*].wood")
            .value(hasItem(DEFAULT_WOOD.doubleValue()))
            .jsonPath("$.[*].fer")
            .value(hasItem(DEFAULT_FER.doubleValue()));
    }

    @Test
    void getResourceData() {
        // Initialize the database
        resourceDataRepository.save(resourceData).block();

        // Get the resourceData
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, resourceData.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(resourceData.getId().intValue()))
            .jsonPath("$.gold")
            .value(is(DEFAULT_GOLD.doubleValue()))
            .jsonPath("$.wood")
            .value(is(DEFAULT_WOOD.doubleValue()))
            .jsonPath("$.fer")
            .value(is(DEFAULT_FER.doubleValue()));
    }

    @Test
    void getNonExistingResourceData() {
        // Get the resourceData
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingResourceData() throws Exception {
        // Initialize the database
        resourceDataRepository.save(resourceData).block();

        int databaseSizeBeforeUpdate = resourceDataRepository.findAll().collectList().block().size();

        // Update the resourceData
        ResourceData updatedResourceData = resourceDataRepository.findById(resourceData.getId()).block();
        updatedResourceData.gold(UPDATED_GOLD).wood(UPDATED_WOOD).fer(UPDATED_FER);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedResourceData.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedResourceData))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ResourceData in the database
        List<ResourceData> resourceDataList = resourceDataRepository.findAll().collectList().block();
        assertThat(resourceDataList).hasSize(databaseSizeBeforeUpdate);
        ResourceData testResourceData = resourceDataList.get(resourceDataList.size() - 1);
        assertThat(testResourceData.getGold()).isEqualTo(UPDATED_GOLD);
        assertThat(testResourceData.getWood()).isEqualTo(UPDATED_WOOD);
        assertThat(testResourceData.getFer()).isEqualTo(UPDATED_FER);
    }

    @Test
    void putNonExistingResourceData() throws Exception {
        int databaseSizeBeforeUpdate = resourceDataRepository.findAll().collectList().block().size();
        resourceData.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, resourceData.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resourceData))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ResourceData in the database
        List<ResourceData> resourceDataList = resourceDataRepository.findAll().collectList().block();
        assertThat(resourceDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchResourceData() throws Exception {
        int databaseSizeBeforeUpdate = resourceDataRepository.findAll().collectList().block().size();
        resourceData.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resourceData))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ResourceData in the database
        List<ResourceData> resourceDataList = resourceDataRepository.findAll().collectList().block();
        assertThat(resourceDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamResourceData() throws Exception {
        int databaseSizeBeforeUpdate = resourceDataRepository.findAll().collectList().block().size();
        resourceData.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resourceData))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ResourceData in the database
        List<ResourceData> resourceDataList = resourceDataRepository.findAll().collectList().block();
        assertThat(resourceDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateResourceDataWithPatch() throws Exception {
        // Initialize the database
        resourceDataRepository.save(resourceData).block();

        int databaseSizeBeforeUpdate = resourceDataRepository.findAll().collectList().block().size();

        // Update the resourceData using partial update
        ResourceData partialUpdatedResourceData = new ResourceData();
        partialUpdatedResourceData.setId(resourceData.getId());

        partialUpdatedResourceData.wood(UPDATED_WOOD).fer(UPDATED_FER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedResourceData.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedResourceData))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ResourceData in the database
        List<ResourceData> resourceDataList = resourceDataRepository.findAll().collectList().block();
        assertThat(resourceDataList).hasSize(databaseSizeBeforeUpdate);
        ResourceData testResourceData = resourceDataList.get(resourceDataList.size() - 1);
        assertThat(testResourceData.getGold()).isEqualTo(DEFAULT_GOLD);
        assertThat(testResourceData.getWood()).isEqualTo(UPDATED_WOOD);
        assertThat(testResourceData.getFer()).isEqualTo(UPDATED_FER);
    }

    @Test
    void fullUpdateResourceDataWithPatch() throws Exception {
        // Initialize the database
        resourceDataRepository.save(resourceData).block();

        int databaseSizeBeforeUpdate = resourceDataRepository.findAll().collectList().block().size();

        // Update the resourceData using partial update
        ResourceData partialUpdatedResourceData = new ResourceData();
        partialUpdatedResourceData.setId(resourceData.getId());

        partialUpdatedResourceData.gold(UPDATED_GOLD).wood(UPDATED_WOOD).fer(UPDATED_FER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedResourceData.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedResourceData))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ResourceData in the database
        List<ResourceData> resourceDataList = resourceDataRepository.findAll().collectList().block();
        assertThat(resourceDataList).hasSize(databaseSizeBeforeUpdate);
        ResourceData testResourceData = resourceDataList.get(resourceDataList.size() - 1);
        assertThat(testResourceData.getGold()).isEqualTo(UPDATED_GOLD);
        assertThat(testResourceData.getWood()).isEqualTo(UPDATED_WOOD);
        assertThat(testResourceData.getFer()).isEqualTo(UPDATED_FER);
    }

    @Test
    void patchNonExistingResourceData() throws Exception {
        int databaseSizeBeforeUpdate = resourceDataRepository.findAll().collectList().block().size();
        resourceData.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, resourceData.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(resourceData))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ResourceData in the database
        List<ResourceData> resourceDataList = resourceDataRepository.findAll().collectList().block();
        assertThat(resourceDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchResourceData() throws Exception {
        int databaseSizeBeforeUpdate = resourceDataRepository.findAll().collectList().block().size();
        resourceData.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(resourceData))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ResourceData in the database
        List<ResourceData> resourceDataList = resourceDataRepository.findAll().collectList().block();
        assertThat(resourceDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamResourceData() throws Exception {
        int databaseSizeBeforeUpdate = resourceDataRepository.findAll().collectList().block().size();
        resourceData.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(resourceData))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ResourceData in the database
        List<ResourceData> resourceDataList = resourceDataRepository.findAll().collectList().block();
        assertThat(resourceDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteResourceData() {
        // Initialize the database
        resourceDataRepository.save(resourceData).block();

        int databaseSizeBeforeDelete = resourceDataRepository.findAll().collectList().block().size();

        // Delete the resourceData
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, resourceData.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ResourceData> resourceDataList = resourceDataRepository.findAll().collectList().block();
        assertThat(resourceDataList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
