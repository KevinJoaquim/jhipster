package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Resource;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Resource}, with proper type conversions.
 */
@Service
public class ResourceRowMapper implements BiFunction<Row, String, Resource> {

    private final ColumnConverter converter;

    public ResourceRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Resource} stored in the database.
     */
    @Override
    public Resource apply(Row row, String prefix) {
        Resource entity = new Resource();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setGold(converter.fromRow(row, prefix + "_gold", Float.class));
        entity.setWood(converter.fromRow(row, prefix + "_wood", Float.class));
        entity.setFer(converter.fromRow(row, prefix + "_fer", Float.class));
        entity.setClientId(converter.fromRow(row, prefix + "_client_id", Long.class));
        return entity;
    }
}
