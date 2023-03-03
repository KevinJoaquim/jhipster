package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.ResourceData;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ResourceData}, with proper type conversions.
 */
@Service
public class ResourceDataRowMapper implements BiFunction<Row, String, ResourceData> {

    private final ColumnConverter converter;

    public ResourceDataRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ResourceData} stored in the database.
     */
    @Override
    public ResourceData apply(Row row, String prefix) {
        ResourceData entity = new ResourceData();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setGold(converter.fromRow(row, prefix + "_gold", Float.class));
        entity.setWood(converter.fromRow(row, prefix + "_wood", Float.class));
        entity.setFer(converter.fromRow(row, prefix + "_fer", Float.class));
        entity.setRegisterUserId(converter.fromRow(row, prefix + "_register_user_id", Long.class));
        return entity;
    }
}
