package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ResourceGot;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ResourceGot entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResourceGotRepository extends ReactiveCrudRepository<ResourceGot, Long>, ResourceGotRepositoryInternal {
    @Query("SELECT * FROM resource_got entity WHERE entity.register_user_id = :id")
    Flux<ResourceGot> findByRegisterUser(Long id);

    @Query("SELECT * FROM resource_got entity WHERE entity.register_user_id IS NULL")
    Flux<ResourceGot> findAllWhereRegisterUserIsNull();

    @Override
    <S extends ResourceGot> Mono<S> save(S entity);

    @Override
    Flux<ResourceGot> findAll();

    @Override
    Mono<ResourceGot> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ResourceGotRepositoryInternal {
    <S extends ResourceGot> Mono<S> save(S entity);

    Flux<ResourceGot> findAllBy(Pageable pageable);

    Flux<ResourceGot> findAll();

    Mono<ResourceGot> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ResourceGot> findAllBy(Pageable pageable, Criteria criteria);

}
