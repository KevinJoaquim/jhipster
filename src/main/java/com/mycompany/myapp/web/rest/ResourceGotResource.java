package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.ResourceGot;
import com.mycompany.myapp.repository.ResourceGotRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.ResourceGot}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ResourceGotResource {

    private final Logger log = LoggerFactory.getLogger(ResourceGotResource.class);

    private static final String ENTITY_NAME = "resourceGot";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ResourceGotRepository resourceGotRepository;

    public ResourceGotResource(ResourceGotRepository resourceGotRepository) {
        this.resourceGotRepository = resourceGotRepository;
    }

    /**
     * {@code POST  /resource-gots} : Create a new resourceGot.
     *
     * @param resourceGot the resourceGot to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new resourceGot, or with status {@code 400 (Bad Request)} if the resourceGot has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/resource-gots")
    public Mono<ResponseEntity<ResourceGot>> createResourceGot(@RequestBody ResourceGot resourceGot) throws URISyntaxException {
        log.debug("REST request to save ResourceGot : {}", resourceGot);
        if (resourceGot.getId() != null) {
            throw new BadRequestAlertException("A new resourceGot cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return resourceGotRepository
            .save(resourceGot)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/resource-gots/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /resource-gots/:id} : Updates an existing resourceGot.
     *
     * @param id the id of the resourceGot to save.
     * @param resourceGot the resourceGot to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resourceGot,
     * or with status {@code 400 (Bad Request)} if the resourceGot is not valid,
     * or with status {@code 500 (Internal Server Error)} if the resourceGot couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/resource-gots/{id}")
    public Mono<ResponseEntity<ResourceGot>> updateResourceGot(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ResourceGot resourceGot
    ) throws URISyntaxException {
        log.debug("REST request to update ResourceGot : {}, {}", id, resourceGot);
        if (resourceGot.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, resourceGot.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return resourceGotRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return resourceGotRepository
                    .save(resourceGot)
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
     * {@code PATCH  /resource-gots/:id} : Partial updates given fields of an existing resourceGot, field will ignore if it is null
     *
     * @param id the id of the resourceGot to save.
     * @param resourceGot the resourceGot to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resourceGot,
     * or with status {@code 400 (Bad Request)} if the resourceGot is not valid,
     * or with status {@code 404 (Not Found)} if the resourceGot is not found,
     * or with status {@code 500 (Internal Server Error)} if the resourceGot couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/resource-gots/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ResourceGot>> partialUpdateResourceGot(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ResourceGot resourceGot
    ) throws URISyntaxException {
        log.debug("REST request to partial update ResourceGot partially : {}, {}", id, resourceGot);
        if (resourceGot.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, resourceGot.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return resourceGotRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ResourceGot> result = resourceGotRepository
                    .findById(resourceGot.getId())
                    .map(existingResourceGot -> {
                        if (resourceGot.getGold() != null) {
                            existingResourceGot.setGold(resourceGot.getGold());
                        }
                        if (resourceGot.getWood() != null) {
                            existingResourceGot.setWood(resourceGot.getWood());
                        }
                        if (resourceGot.getFer() != null) {
                            existingResourceGot.setFer(resourceGot.getFer());
                        }

                        return existingResourceGot;
                    })
                    .flatMap(resourceGotRepository::save);

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
     * {@code GET  /resource-gots} : get all the resourceGots.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of resourceGots in body.
     */
    @GetMapping("/resource-gots")
    public Mono<List<ResourceGot>> getAllResourceGots() {
        log.debug("REST request to get all ResourceGots");
        return resourceGotRepository.findAll().collectList();
    }

    /**
     * {@code GET  /resource-gots} : get all the resourceGots as a stream.
     * @return the {@link Flux} of resourceGots.
     */
    @GetMapping(value = "/resource-gots", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ResourceGot> getAllResourceGotsAsStream() {
        log.debug("REST request to get all ResourceGots as a stream");
        return resourceGotRepository.findAll();
    }

    /**
     * {@code GET  /resource-gots/:id} : get the "id" resourceGot.
     *
     * @param id the id of the resourceGot to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the resourceGot, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/resource-gots/{id}")
    public Mono<ResponseEntity<ResourceGot>> getResourceGot(@PathVariable Long id) {
        log.debug("REST request to get ResourceGot : {}", id);
        Mono<ResourceGot> resourceGot = resourceGotRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(resourceGot);
    }

    /**
     * {@code DELETE  /resource-gots/:id} : delete the "id" resourceGot.
     *
     * @param id the id of the resourceGot to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/resource-gots/{id}")
    public Mono<ResponseEntity<Void>> deleteResourceGot(@PathVariable Long id) {
        log.debug("REST request to delete ResourceGot : {}", id);
        return resourceGotRepository
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
