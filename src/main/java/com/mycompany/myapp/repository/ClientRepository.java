package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Client;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Client entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClientRepository extends ReactiveCrudRepository<Client, Long>, ClientRepositoryInternal {
    @Query("SELECT * FROM client entity WHERE entity.user_id = :id")
    Flux<Client> findByUser(Long id);

    @Query("SELECT * FROM client entity WHERE entity.user_id IS NULL")
    Flux<Client> findAllWhereUserIsNull();

    @Query("SELECT * FROM client entity WHERE entity.company_id = :id")
    Flux<Client> findByCompany(Long id);

    @Query("SELECT * FROM client entity WHERE entity.company_id IS NULL")
    Flux<Client> findAllWhereCompanyIsNull();

    @Override
    <S extends Client> Mono<S> save(S entity);

    @Override
    Flux<Client> findAll();

    @Override
    Mono<Client> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ClientRepositoryInternal {
    <S extends Client> Mono<S> save(S entity);

    Flux<Client> findAllBy(Pageable pageable);

    Flux<Client> findAll();

    Mono<Client> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Client> findAllBy(Pageable pageable, Criteria criteria);

}
