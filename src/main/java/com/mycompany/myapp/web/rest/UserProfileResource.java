package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.UserProfile;
import com.mycompany.myapp.repository.UserProfileRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.UserProfile}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UserProfileResource {

    private final Logger log = LoggerFactory.getLogger(UserProfileResource.class);

    private static final String ENTITY_NAME = "userProfile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserProfileRepository userProfileRepository;

    public UserProfileResource(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    /**
     * {@code POST  /user-profiles} : Create a new userProfile.
     *
     * @param userProfile the userProfile to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userProfile, or with status {@code 400 (Bad Request)} if the userProfile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-profiles")
    public Mono<ResponseEntity<UserProfile>> createUserProfile(@RequestBody UserProfile userProfile) throws URISyntaxException {
        log.debug("REST request to save UserProfile : {}", userProfile);
        if (userProfile.getId() != null) {
            throw new BadRequestAlertException("A new userProfile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return userProfileRepository
            .save(userProfile)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/user-profiles/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /user-profiles/:id} : Updates an existing userProfile.
     *
     * @param id the id of the userProfile to save.
     * @param userProfile the userProfile to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userProfile,
     * or with status {@code 400 (Bad Request)} if the userProfile is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userProfile couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-profiles/{id}")
    public Mono<ResponseEntity<UserProfile>> updateUserProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserProfile userProfile
    ) throws URISyntaxException {
        log.debug("REST request to update UserProfile : {}, {}", id, userProfile);
        if (userProfile.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userProfile.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userProfileRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return userProfileRepository
                    .save(userProfile)
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
     * {@code PATCH  /user-profiles/:id} : Partial updates given fields of an existing userProfile, field will ignore if it is null
     *
     * @param id the id of the userProfile to save.
     * @param userProfile the userProfile to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userProfile,
     * or with status {@code 400 (Bad Request)} if the userProfile is not valid,
     * or with status {@code 404 (Not Found)} if the userProfile is not found,
     * or with status {@code 500 (Internal Server Error)} if the userProfile couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-profiles/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<UserProfile>> partialUpdateUserProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserProfile userProfile
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserProfile partially : {}, {}", id, userProfile);
        if (userProfile.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userProfile.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userProfileRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<UserProfile> result = userProfileRepository
                    .findById(userProfile.getId())
                    .map(existingUserProfile -> {
                        return existingUserProfile;
                    })
                    .flatMap(userProfileRepository::save);

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
     * {@code GET  /user-profiles} : get all the userProfiles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userProfiles in body.
     */
    @GetMapping("/user-profiles")
    public Mono<List<UserProfile>> getAllUserProfiles() {
        log.debug("REST request to get all UserProfiles");
        return userProfileRepository.findAll().collectList();
    }

    /**
     * {@code GET  /user-profiles} : get all the userProfiles as a stream.
     * @return the {@link Flux} of userProfiles.
     */
    @GetMapping(value = "/user-profiles", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<UserProfile> getAllUserProfilesAsStream() {
        log.debug("REST request to get all UserProfiles as a stream");
        return userProfileRepository.findAll();
    }

    /**
     * {@code GET  /user-profiles/:id} : get the "id" userProfile.
     *
     * @param id the id of the userProfile to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userProfile, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-profiles/{id}")
    public Mono<ResponseEntity<UserProfile>> getUserProfile(@PathVariable Long id) {
        log.debug("REST request to get UserProfile : {}", id);
        Mono<UserProfile> userProfile = userProfileRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(userProfile);
    }

    /**
     * {@code DELETE  /user-profiles/:id} : delete the "id" userProfile.
     *
     * @param id the id of the userProfile to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-profiles/{id}")
    public Mono<ResponseEntity<Void>> deleteUserProfile(@PathVariable Long id) {
        log.debug("REST request to delete UserProfile : {}", id);
        return userProfileRepository
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
