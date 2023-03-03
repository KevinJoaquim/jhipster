package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ResourceDataSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("gold", table, columnPrefix + "_gold"));
        columns.add(Column.aliased("wood", table, columnPrefix + "_wood"));
        columns.add(Column.aliased("fer", table, columnPrefix + "_fer"));

        columns.add(Column.aliased("register_user_id", table, columnPrefix + "_register_user_id"));
        return columns;
    }
}
