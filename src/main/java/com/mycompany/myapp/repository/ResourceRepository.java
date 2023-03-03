package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Resource entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResourceRepository extends ReactiveCrudRepository<Resource, Long>, ResourceRepositoryInternal {
    @Query("SELECT * FROM resource entity WHERE entity.client_id = :id")
    Flux<Resource> findByClient(Long id);

    @Query("SELECT * FROM resource entity WHERE entity.client_id IS NULL")
    Flux<Resource> findAllWhereClientIsNull();

    @Override
    <S extends Resource> Mono<S> save(S entity);

    @Override
    Flux<Resource> findAll();

    @Override
    Mono<Resource> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ResourceRepositoryInternal {
    <S extends Resource> Mono<S> save(S entity);

    Flux<Resource> findAllBy(Pageable pageable);

    Flux<Resource> findAll();

    Mono<Resource> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Resource> findAllBy(Pageable pageable, Criteria criteria);

}
