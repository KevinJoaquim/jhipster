package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.ResourceGot;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ResourceGot}, with proper type conversions.
 */
@Service
public class ResourceGotRowMapper implements BiFunction<Row, String, ResourceGot> {

    private final ColumnConverter converter;

    public ResourceGotRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ResourceGot} stored in the database.
     */
    @Override
    public ResourceGot apply(Row row, String prefix) {
        ResourceGot entity = new ResourceGot();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setGold(converter.fromRow(row, prefix + "_gold", Float.class));
        entity.setWood(converter.fromRow(row, prefix + "_wood", Float.class));
        entity.setFer(converter.fromRow(row, prefix + "_fer", Float.class));
        entity.setRegisterUserId(converter.fromRow(row, prefix + "_register_user_id", Long.class));
        return entity;
    }
}
