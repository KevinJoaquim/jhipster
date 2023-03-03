package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ResourceData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ResourceData entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResourceDataRepository extends ReactiveCrudRepository<ResourceData, Long>, ResourceDataRepositoryInternal {
    @Query("SELECT * FROM resource_data entity WHERE entity.register_user_id = :id")
    Flux<ResourceData> findByRegisterUser(Long id);

    @Query("SELECT * FROM resource_data entity WHERE entity.register_user_id IS NULL")
    Flux<ResourceData> findAllWhereRegisterUserIsNull();

    @Override
    <S extends ResourceData> Mono<S> save(S entity);

    @Override
    Flux<ResourceData> findAll();

    @Override
    Mono<ResourceData> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ResourceDataRepositoryInternal {
    <S extends ResourceData> Mono<S> save(S entity);

    Flux<ResourceData> findAllBy(Pageable pageable);

    Flux<ResourceData> findAll();

    Mono<ResourceData> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ResourceData> findAllBy(Pageable pageable, Criteria criteria);

}
