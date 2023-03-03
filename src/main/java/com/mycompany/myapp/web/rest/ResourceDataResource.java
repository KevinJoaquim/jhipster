package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.ResourceData;
import com.mycompany.myapp.repository.ResourceDataRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.ResourceData}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ResourceDataResource {

    private final Logger log = LoggerFactory.getLogger(ResourceDataResource.class);

    private static final String ENTITY_NAME = "resourceData";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ResourceDataRepository resourceDataRepository;

    public ResourceDataResource(ResourceDataRepository resourceDataRepository) {
        this.resourceDataRepository = resourceDataRepository;
    }

    /**
     * {@code POST  /resource-data} : Create a new resourceData.
     *
     * @param resourceData the resourceData to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new resourceData, or with status {@code 400 (Bad Request)} if the resourceData has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/resource-data")
    public Mono<ResponseEntity<ResourceData>> createResourceData(@RequestBody ResourceData resourceData) throws URISyntaxException {
        log.debug("REST request to save ResourceData : {}", resourceData);
        if (resourceData.getId() != null) {
            throw new BadRequestAlertException("A new resourceData cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return resourceDataRepository
            .save(resourceData)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/resource-data/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /resource-data/:id} : Updates an existing resourceData.
     *
     * @param id the id of the resourceData to save.
     * @param resourceData the resourceData to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resourceData,
     * or with status {@code 400 (Bad Request)} if the resourceData is not valid,
     * or with status {@code 500 (Internal Server Error)} if the resourceData couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/resource-data/{id}")
    public Mono<ResponseEntity<ResourceData>> updateResourceData(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ResourceData resourceData
    ) throws URISyntaxException {
        log.debug("REST request to update ResourceData : {}, {}", id, resourceData);
        if (resourceData.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, resourceData.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return resourceDataRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return resourceDataRepository
                    .save(resourceData)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /resource-data/:id} : Partial updates given fields of an existing resourceData, field will ignore if it is null
     *
     * @param id the id of the resourceData to save.
     * @param resourceData the resourceData to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resourceData,
     * or with status {@code 400 (Bad Request)} if the resourceData is not valid,
     * or with status {@code 404 (Not Found)} if the resourceData is not found,
     * or with status {@code 500 (Internal Server Error)} if the resourceData couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/resource-data/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ResourceData>> partialUpdateResourceData(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ResourceData resourceData
    ) throws URISyntaxException {
        log.debug("REST request to partial update ResourceData partially : {}, {}", id, resourceData);
        if (resourceData.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, resourceData.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return resourceDataRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ResourceData> result = resourceDataRepository
                    .findById(resourceData.getId())
                    .map(existingResourceData -> {
                        if (resourceData.getGold() != null) {
                            existingResourceData.setGold(resourceData.getGold());
                        }
                        if (resourceData.getWood() != null) {
                            existingResourceData.setWood(resourceData.getWood());
                        }
                        if (resourceData.getFer() != null) {
                            existingResourceData.setFer(resourceData.getFer());
                        }

                        return existingResourceData;
                    })
                    .flatMap(resourceDataRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /resource-data} : get all the resourceData.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of resourceData in body.
     */
    @GetMapping("/resource-data")
    public Mono<List<ResourceData>> getAllResourceData() {
        log.debug("REST request to get all ResourceData");
        return resourceDataRepository.findAll().collectList();
    }

    /**
     * {@code GET  /resource-data} : get all the resourceData as a stream.
     * @return the {@link Flux} of resourceData.
     */
    @GetMapping(value = "/resource-data", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ResourceData> getAllResourceDataAsStream() {
        log.debug("REST request to get all ResourceData as a stream");
        return resourceDataRepository.findAll();
    }

    /**
     * {@code GET  /resource-data/:id} : get the "id" resourceData.
     *
     * @param id the id of the resourceData to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the resourceData, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/resource-data/{id}")
    public Mono<ResponseEntity<ResourceData>> getResourceData(@PathVariable Long id) {
        log.debug("REST request to get ResourceData : {}", id);
        Mono<ResourceData> resourceData = resourceDataRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(resourceData);
    }

    /**
     * {@code DELETE  /resource-data/:id} : delete the "id" resourceData.
     *
     * @param id the id of the resourceData to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/resource-data/{id}")
    public Mono<ResponseEntity<Void>> deleteResourceData(@PathVariable Long id) {
        log.debug("REST request to delete ResourceData : {}", id);
        return resourceDataRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
