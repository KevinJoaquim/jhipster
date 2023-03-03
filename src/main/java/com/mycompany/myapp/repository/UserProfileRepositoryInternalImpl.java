package com.mycompany.myapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.mycompany.myapp.domain.UserProfile;
import com.mycompany.myapp.repository.rowmapper.ResourceDataRowMapper;
import com.mycompany.myapp.repository.rowmapper.UserProfileRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the UserProfile entity.
 */
@SuppressWarnings("unused")
class UserProfileRepositoryInternalImpl extends SimpleR2dbcRepository<UserProfile, Long> implements UserProfileRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final ResourceDataRowMapper resourcedataMapper;
    private final UserProfileRowMapper userprofileMapper;

    private static final Table entityTable = Table.aliased("user_profile", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("jhi_user", "e_user");
    private static final Table resourceTable = Table.aliased("resource_data", "e_resource");

    public UserProfileRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        ResourceDataRowMapper resourcedataMapper,
        UserProfileRowMapper userprofileMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(UserProfile.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.resourcedataMapper = resourcedataMapper;
        this.userprofileMapper = userprofileMapper;
    }

    @Override
    public Flux<UserProfile> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<UserProfile> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = UserProfileSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(ResourceDataSqlHelper.getColumns(resourceTable, "resource"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(resourceTable)
            .on(Column.create("resource_id", entityTable))
            .equals(Column.create("id", resourceTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, UserProfile.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<UserProfile> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<UserProfile> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private UserProfile process(Row row, RowMetadata metadata) {
        UserProfile entity = userprofileMapper.apply(row, "e");
        entity.setUser(userMapper.apply(row, "user"));
        entity.setResource(resourcedataMapper.apply(row, "resource"));
        return entity;
    }

    @Override
    public <S extends UserProfile> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
