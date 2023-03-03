package com.mycompany.myapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.mycompany.myapp.domain.Resource;
import com.mycompany.myapp.repository.rowmapper.ClientRowMapper;
import com.mycompany.myapp.repository.rowmapper.ResourceRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Resource entity.
 */
@SuppressWarnings("unused")
class ResourceRepositoryInternalImpl extends SimpleR2dbcRepository<Resource, Long> implements ResourceRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ClientRowMapper clientMapper;
    private final ResourceRowMapper resourceMapper;

    private static final Table entityTable = Table.aliased("resource", EntityManager.ENTITY_ALIAS);
    private static final Table clientTable = Table.aliased("client", "client");

    public ResourceRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ClientRowMapper clientMapper,
        ResourceRowMapper resourceMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Resource.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.clientMapper = clientMapper;
        this.resourceMapper = resourceMapper;
    }

    @Override
    public Flux<Resource> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Resource> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ResourceSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ClientSqlHelper.getColumns(clientTable, "client"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(clientTable)
            .on(Column.create("client_id", entityTable))
            .equals(Column.create("id", clientTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Resource.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Resource> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Resource> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Resource process(Row row, RowMetadata metadata) {
        Resource entity = resourceMapper.apply(row, "e");
        entity.setClient(clientMapper.apply(row, "client"));
        return entity;
    }

    @Override
    public <S extends Resource> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
