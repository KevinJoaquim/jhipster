package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Resource;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.ResourceRepository;
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
 * Integration tests for the {@link ResourceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ResourceResourceIT {

    private static final Float DEFAULT_GOLD = 1F;
    private static final Float UPDATED_GOLD = 2F;

    private static final Float DEFAULT_WOOD = 1F;
    private static final Float UPDATED_WOOD = 2F;

    private static final Float DEFAULT_FER = 1F;
    private static final Float UPDATED_FER = 2F;

    private static final String ENTITY_API_URL = "/api/resources";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Resource resource;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Resource createEntity(EntityManager em) {
        Resource resource = new Resource().gold(DEFAULT_GOLD).wood(DEFAULT_WOOD).fer(DEFAULT_FER);
        return resource;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Resource createUpdatedEntity(EntityManager em) {
        Resource resource = new Resource().gold(UPDATED_GOLD).wood(UPDATED_WOOD).fer(UPDATED_FER);
        return resource;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Resource.class).block();
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
        resource = createEntity(em);
    }

    @Test
    void createResource() throws Exception {
        int databaseSizeBeforeCreate = resourceRepository.findAll().collectList().block().size();
        // Create the Resource
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resource))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll().collectList().block();
        assertThat(resourceList).hasSize(databaseSizeBeforeCreate + 1);
        Resource testResource = resourceList.get(resourceList.size() - 1);
        assertThat(testResource.getGold()).isEqualTo(DEFAULT_GOLD);
        assertThat(testResource.getWood()).isEqualTo(DEFAULT_WOOD);
        assertThat(testResource.getFer()).isEqualTo(DEFAULT_FER);
    }

    @Test
    void createResourceWithExistingId() throws Exception {
        // Create the Resource with an existing ID
        resource.setId(1L);

        int databaseSizeBeforeCreate = resourceRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resource))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll().collectList().block();
        assertThat(resourceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllResourcesAsStream() {
        // Initialize the database
        resourceRepository.save(resource).block();

        List<Resource> resourceList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Resource.class)
            .getResponseBody()
            .filter(resource::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(resourceList).isNotNull();
        assertThat(resourceList).hasSize(1);
        Resource testResource = resourceList.get(0);
        assertThat(testResource.getGold()).isEqualTo(DEFAULT_GOLD);
        assertThat(testResource.getWood()).isEqualTo(DEFAULT_WOOD);
        assertThat(testResource.getFer()).isEqualTo(DEFAULT_FER);
    }

    @Test
    void getAllResources() {
        // Initialize the database
        resourceRepository.save(resource).block();

        // Get all the resourceList
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
            .value(hasItem(resource.getId().intValue()))
            .jsonPath("$.[*].gold")
            .value(hasItem(DEFAULT_GOLD.doubleValue()))
            .jsonPath("$.[*].wood")
            .value(hasItem(DEFAULT_WOOD.doubleValue()))
            .jsonPath("$.[*].fer")
            .value(hasItem(DEFAULT_FER.doubleValue()));
    }

    @Test
    void getResource() {
        // Initialize the database
        resourceRepository.save(resource).block();

        // Get the resource
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, resource.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(resource.getId().intValue()))
            .jsonPath("$.gold")
            .value(is(DEFAULT_GOLD.doubleValue()))
            .jsonPath("$.wood")
            .value(is(DEFAULT_WOOD.doubleValue()))
            .jsonPath("$.fer")
            .value(is(DEFAULT_FER.doubleValue()));
    }

    @Test
    void getNonExistingResource() {
        // Get the resource
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingResource() throws Exception {
        // Initialize the database
        resourceRepository.save(resource).block();

        int databaseSizeBeforeUpdate = resourceRepository.findAll().collectList().block().size();

        // Update the resource
        Resource updatedResource = resourceRepository.findById(resource.getId()).block();
        updatedResource.gold(UPDATED_GOLD).wood(UPDATED_WOOD).fer(UPDATED_FER);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedResource.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedResource))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll().collectList().block();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
        Resource testResource = resourceList.get(resourceList.size() - 1);
        assertThat(testResource.getGold()).isEqualTo(UPDATED_GOLD);
        assertThat(testResource.getWood()).isEqualTo(UPDATED_WOOD);
        assertThat(testResource.getFer()).isEqualTo(UPDATED_FER);
    }

    @Test
    void putNonExistingResource() throws Exception {
        int databaseSizeBeforeUpdate = resourceRepository.findAll().collectList().block().size();
        resource.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, resource.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resource))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll().collectList().block();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchResource() throws Exception {
        int databaseSizeBeforeUpdate = resourceRepository.findAll().collectList().block().size();
        resource.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resource))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll().collectList().block();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamResource() throws Exception {
        int databaseSizeBeforeUpdate = resourceRepository.findAll().collectList().block().size();
        resource.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resource))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll().collectList().block();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateResourceWithPatch() throws Exception {
        // Initialize the database
        resourceRepository.save(resource).block();

        int databaseSizeBeforeUpdate = resourceRepository.findAll().collectList().block().size();

        // Update the resource using partial update
        Resource partialUpdatedResource = new Resource();
        partialUpdatedResource.setId(resource.getId());

        partialUpdatedResource.wood(UPDATED_WOOD);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedResource.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedResource))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll().collectList().block();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
        Resource testResource = resourceList.get(resourceList.size() - 1);
        assertThat(testResource.getGold()).isEqualTo(DEFAULT_GOLD);
        assertThat(testResource.getWood()).isEqualTo(UPDATED_WOOD);
        assertThat(testResource.getFer()).isEqualTo(DEFAULT_FER);
    }

    @Test
    void fullUpdateResourceWithPatch() throws Exception {
        // Initialize the database
        resourceRepository.save(resource).block();

        int databaseSizeBeforeUpdate = resourceRepository.findAll().collectList().block().size();

        // Update the resource using partial update
        Resource partialUpdatedResource = new Resource();
        partialUpdatedResource.setId(resource.getId());

        partialUpdatedResource.gold(UPDATED_GOLD).wood(UPDATED_WOOD).fer(UPDATED_FER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedResource.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedResource))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll().collectList().block();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
        Resource testResource = resourceList.get(resourceList.size() - 1);
        assertThat(testResource.getGold()).isEqualTo(UPDATED_GOLD);
        assertThat(testResource.getWood()).isEqualTo(UPDATED_WOOD);
        assertThat(testResource.getFer()).isEqualTo(UPDATED_FER);
    }

    @Test
    void patchNonExistingResource() throws Exception {
        int databaseSizeBeforeUpdate = resourceRepository.findAll().collectList().block().size();
        resource.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, resource.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(resource))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll().collectList().block();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchResource() throws Exception {
        int databaseSizeBeforeUpdate = resourceRepository.findAll().collectList().block().size();
        resource.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(resource))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll().collectList().block();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamResource() throws Exception {
        int databaseSizeBeforeUpdate = resourceRepository.findAll().collectList().block().size();
        resource.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(resource))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Resource in the database
        List<Resource> resourceList = resourceRepository.findAll().collectList().block();
        assertThat(resourceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteResource() {
        // Initialize the database
        resourceRepository.save(resource).block();

        int databaseSizeBeforeDelete = resourceRepository.findAll().collectList().block().size();

        // Delete the resource
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, resource.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Resource> resourceList = resourceRepository.findAll().collectList().block();
        assertThat(resourceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
