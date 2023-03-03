package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ResourceGot;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.ResourceGotRepository;
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
 * Integration tests for the {@link ResourceGotResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ResourceGotResourceIT {

    private static final Float DEFAULT_GOLD = 1F;
    private static final Float UPDATED_GOLD = 2F;

    private static final Float DEFAULT_WOOD = 1F;
    private static final Float UPDATED_WOOD = 2F;

    private static final Float DEFAULT_FER = 1F;
    private static final Float UPDATED_FER = 2F;

    private static final String ENTITY_API_URL = "/api/resource-gots";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ResourceGotRepository resourceGotRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ResourceGot resourceGot;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ResourceGot createEntity(EntityManager em) {
        ResourceGot resourceGot = new ResourceGot().gold(DEFAULT_GOLD).wood(DEFAULT_WOOD).fer(DEFAULT_FER);
        return resourceGot;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ResourceGot createUpdatedEntity(EntityManager em) {
        ResourceGot resourceGot = new ResourceGot().gold(UPDATED_GOLD).wood(UPDATED_WOOD).fer(UPDATED_FER);
        return resourceGot;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ResourceGot.class).block();
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
        resourceGot = createEntity(em);
    }

    @Test
    void createResourceGot() throws Exception {
        int databaseSizeBeforeCreate = resourceGotRepository.findAll().collectList().block().size();
        // Create the ResourceGot
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resourceGot))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ResourceGot in the database
        List<ResourceGot> resourceGotList = resourceGotRepository.findAll().collectList().block();
        assertThat(resourceGotList).hasSize(databaseSizeBeforeCreate + 1);
        ResourceGot testResourceGot = resourceGotList.get(resourceGotList.size() - 1);
        assertThat(testResourceGot.getGold()).isEqualTo(DEFAULT_GOLD);
        assertThat(testResourceGot.getWood()).isEqualTo(DEFAULT_WOOD);
        assertThat(testResourceGot.getFer()).isEqualTo(DEFAULT_FER);
    }

    @Test
    void createResourceGotWithExistingId() throws Exception {
        // Create the ResourceGot with an existing ID
        resourceGot.setId(1L);

        int databaseSizeBeforeCreate = resourceGotRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resourceGot))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ResourceGot in the database
        List<ResourceGot> resourceGotList = resourceGotRepository.findAll().collectList().block();
        assertThat(resourceGotList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllResourceGotsAsStream() {
        // Initialize the database
        resourceGotRepository.save(resourceGot).block();

        List<ResourceGot> resourceGotList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ResourceGot.class)
            .getResponseBody()
            .filter(resourceGot::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(resourceGotList).isNotNull();
        assertThat(resourceGotList).hasSize(1);
        ResourceGot testResourceGot = resourceGotList.get(0);
        assertThat(testResourceGot.getGold()).isEqualTo(DEFAULT_GOLD);
        assertThat(testResourceGot.getWood()).isEqualTo(DEFAULT_WOOD);
        assertThat(testResourceGot.getFer()).isEqualTo(DEFAULT_FER);
    }

    @Test
    void getAllResourceGots() {
        // Initialize the database
        resourceGotRepository.save(resourceGot).block();

        // Get all the resourceGotList
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
            .value(hasItem(resourceGot.getId().intValue()))
            .jsonPath("$.[*].gold")
            .value(hasItem(DEFAULT_GOLD.doubleValue()))
            .jsonPath("$.[*].wood")
            .value(hasItem(DEFAULT_WOOD.doubleValue()))
            .jsonPath("$.[*].fer")
            .value(hasItem(DEFAULT_FER.doubleValue()));
    }

    @Test
    void getResourceGot() {
        // Initialize the database
        resourceGotRepository.save(resourceGot).block();

        // Get the resourceGot
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, resourceGot.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(resourceGot.getId().intValue()))
            .jsonPath("$.gold")
            .value(is(DEFAULT_GOLD.doubleValue()))
            .jsonPath("$.wood")
            .value(is(DEFAULT_WOOD.doubleValue()))
            .jsonPath("$.fer")
            .value(is(DEFAULT_FER.doubleValue()));
    }

    @Test
    void getNonExistingResourceGot() {
        // Get the resourceGot
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingResourceGot() throws Exception {
        // Initialize the database
        resourceGotRepository.save(resourceGot).block();

        int databaseSizeBeforeUpdate = resourceGotRepository.findAll().collectList().block().size();

        // Update the resourceGot
        ResourceGot updatedResourceGot = resourceGotRepository.findById(resourceGot.getId()).block();
        updatedResourceGot.gold(UPDATED_GOLD).wood(UPDATED_WOOD).fer(UPDATED_FER);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedResourceGot.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedResourceGot))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ResourceGot in the database
        List<ResourceGot> resourceGotList = resourceGotRepository.findAll().collectList().block();
        assertThat(resourceGotList).hasSize(databaseSizeBeforeUpdate);
        ResourceGot testResourceGot = resourceGotList.get(resourceGotList.size() - 1);
        assertThat(testResourceGot.getGold()).isEqualTo(UPDATED_GOLD);
        assertThat(testResourceGot.getWood()).isEqualTo(UPDATED_WOOD);
        assertThat(testResourceGot.getFer()).isEqualTo(UPDATED_FER);
    }

    @Test
    void putNonExistingResourceGot() throws Exception {
        int databaseSizeBeforeUpdate = resourceGotRepository.findAll().collectList().block().size();
        resourceGot.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, resourceGot.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resourceGot))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ResourceGot in the database
        List<ResourceGot> resourceGotList = resourceGotRepository.findAll().collectList().block();
        assertThat(resourceGotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchResourceGot() throws Exception {
        int databaseSizeBeforeUpdate = resourceGotRepository.findAll().collectList().block().size();
        resourceGot.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resourceGot))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ResourceGot in the database
        List<ResourceGot> resourceGotList = resourceGotRepository.findAll().collectList().block();
        assertThat(resourceGotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamResourceGot() throws Exception {
        int databaseSizeBeforeUpdate = resourceGotRepository.findAll().collectList().block().size();
        resourceGot.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resourceGot))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ResourceGot in the database
        List<ResourceGot> resourceGotList = resourceGotRepository.findAll().collectList().block();
        assertThat(resourceGotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateResourceGotWithPatch() throws Exception {
        // Initialize the database
        resourceGotRepository.save(resourceGot).block();

        int databaseSizeBeforeUpdate = resourceGotRepository.findAll().collectList().block().size();

        // Update the resourceGot using partial update
        ResourceGot partialUpdatedResourceGot = new ResourceGot();
        partialUpdatedResourceGot.setId(resourceGot.getId());

        partialUpdatedResourceGot.wood(UPDATED_WOOD).fer(UPDATED_FER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedResourceGot.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedResourceGot))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ResourceGot in the database
        List<ResourceGot> resourceGotList = resourceGotRepository.findAll().collectList().block();
        assertThat(resourceGotList).hasSize(databaseSizeBeforeUpdate);
        ResourceGot testResourceGot = resourceGotList.get(resourceGotList.size() - 1);
        assertThat(testResourceGot.getGold()).isEqualTo(DEFAULT_GOLD);
        assertThat(testResourceGot.getWood()).isEqualTo(UPDATED_WOOD);
        assertThat(testResourceGot.getFer()).isEqualTo(UPDATED_FER);
    }

    @Test
    void fullUpdateResourceGotWithPatch() throws Exception {
        // Initialize the database
        resourceGotRepository.save(resourceGot).block();

        int databaseSizeBeforeUpdate = resourceGotRepository.findAll().collectList().block().size();

        // Update the resourceGot using partial update
        ResourceGot partialUpdatedResourceGot = new ResourceGot();
        partialUpdatedResourceGot.setId(resourceGot.getId());

        partialUpdatedResourceGot.gold(UPDATED_GOLD).wood(UPDATED_WOOD).fer(UPDATED_FER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedResourceGot.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedResourceGot))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ResourceGot in the database
        List<ResourceGot> resourceGotList = resourceGotRepository.findAll().collectList().block();
        assertThat(resourceGotList).hasSize(databaseSizeBeforeUpdate);
        ResourceGot testResourceGot = resourceGotList.get(resourceGotList.size() - 1);
        assertThat(testResourceGot.getGold()).isEqualTo(UPDATED_GOLD);
        assertThat(testResourceGot.getWood()).isEqualTo(UPDATED_WOOD);
        assertThat(testResourceGot.getFer()).isEqualTo(UPDATED_FER);
    }

    @Test
    void patchNonExistingResourceGot() throws Exception {
        int databaseSizeBeforeUpdate = resourceGotRepository.findAll().collectList().block().size();
        resourceGot.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, resourceGot.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(resourceGot))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ResourceGot in the database
        List<ResourceGot> resourceGotList = resourceGotRepository.findAll().collectList().block();
        assertThat(resourceGotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchResourceGot() throws Exception {
        int databaseSizeBeforeUpdate = resourceGotRepository.findAll().collectList().block().size();
        resourceGot.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(resourceGot))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ResourceGot in the database
        List<ResourceGot> resourceGotList = resourceGotRepository.findAll().collectList().block();
        assertThat(resourceGotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamResourceGot() throws Exception {
        int databaseSizeBeforeUpdate = resourceGotRepository.findAll().collectList().block().size();
        resourceGot.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(resourceGot))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ResourceGot in the database
        List<ResourceGot> resourceGotList = resourceGotRepository.findAll().collectList().block();
        assertThat(resourceGotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteResourceGot() {
        // Initialize the database
        resourceGotRepository.save(resourceGot).block();

        int databaseSizeBeforeDelete = resourceGotRepository.findAll().collectList().block().size();

        // Delete the resourceGot
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, resourceGot.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ResourceGot> resourceGotList = resourceGotRepository.findAll().collectList().block();
        assertThat(resourceGotList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
