package com.mycompany.myapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.mycompany.myapp.domain.ResourceData;
import com.mycompany.myapp.repository.rowmapper.ResourceDataRowMapper;
import com.mycompany.myapp.repository.rowmapper.UserRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the ResourceData entity.
 */
@SuppressWarnings("unused")
class ResourceDataRepositoryInternalImpl extends SimpleR2dbcRepository<ResourceData, Long> implements ResourceDataRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final ResourceDataRowMapper resourcedataMapper;

    private static final Table entityTable = Table.aliased("resource_data", EntityManager.ENTITY_ALIAS);
    private static final Table registerUserTable = Table.aliased("jhi_user", "registerUser");

    public ResourceDataRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        ResourceDataRowMapper resourcedataMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(ResourceData.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.resourcedataMapper = resourcedataMapper;
    }

    @Override
    public Flux<ResourceData> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ResourceData> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ResourceDataSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(registerUserTable, "registerUser"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(registerUserTable)
            .on(Column.create("register_user_id", entityTable))
            .equals(Column.create("id", registerUserTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ResourceData.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ResourceData> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ResourceData> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private ResourceData process(Row row, RowMetadata metadata) {
        ResourceData entity = resourcedataMapper.apply(row, "e");
        entity.setRegisterUser(userMapper.apply(row, "registerUser"));
        return entity;
    }

    @Override
    public <S extends ResourceData> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
