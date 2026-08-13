/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.prefeiturasegura.utils;

import com.prefeiturasegura.interfaces.DatabaseConnectionInterface;
import com.prefeiturasegura.valueobjects.Operation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryBuilder {

    private final DatabaseConnectionInterface connection;

    private String query;

    private String table;

    private final List<String> columns = new ArrayList<>();
    private final List<String> conditions = new ArrayList<>();
    private final List<Object> parameters = new ArrayList<>();
    private final List<String> orderBy = new ArrayList<>();

    private final Map<String, String> values = new LinkedHashMap<>();

    private Integer limit;
    private Integer offset;

    private Operation operation;

    public QueryBuilder(DatabaseConnectionInterface connection) {
        this.connection = connection;
    }

    public QueryBuilder select(String... columns) {
        this.operation = Operation.SELECT;

        if (columns == null) {
            throw new IllegalStateException("Nenhuma coluna definida.");
        }

        for (String column : columns) {
            this.columns.add(column);
        }

        return this;
    }

    public QueryBuilder insert(String table) {
        this.operation = Operation.INSERT;
        this.table = table;
        return this;
    }

    public QueryBuilder update(String table) {
        this.operation = Operation.UPDATE;
        this.table = table;
        return this;
    }

    public QueryBuilder delete(String table) {
        this.operation = Operation.DELETE;
        this.table = table;
        return this;
    }

    public QueryBuilder from(String table) {
        this.table = table;
        return this;
    }

    public QueryBuilder set(String column, Object value) {
        this.values.put(column, "?");
        this.parameters.add(value);
        return this;
    }

    public QueryBuilder where(String column, String operator, Object value) {
        this.conditions.add(column + " " + operator + " ?");
        this.parameters.add(value);
        return this;
    }

    public QueryBuilder andWhere(String column, String operator, Object value) {
        return where(" AND " + column, operator, value);
    }

    public QueryBuilder orWhere(String column, String operator, Object value) {
        return where(" OR " + column, operator, value);
    }

    public QueryBuilder orderBy(String column, String direction) {
        this.orderBy.add(column + " " + direction);
        return this;
    }

    public QueryBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    public QueryBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    public List<Object> fetchColumn() {
        this.build();
        return this.connection.fetchColumn(this.query, this.parameters);
    }

    public List<Map<String, Object>> fetchAll() {
        this.build();
        return this.connection.fetchAll(this.query, this.parameters);
    }

    public int execute() {
        this.build();
        return this.connection.executeUpdate(this.query, this.parameters);
    }

    private void build() {
        if (this.operation == null) {
            throw new IllegalStateException("Nenhuma operação definida.");
        }

        if (this.table == null || this.table.isBlank()) {
            throw new IllegalStateException("Tabela não definida.");
        }

        switch (operation) {
            case SELECT -> this.buildSelect();
            case INSERT -> this.buildInsert();
            case UPDATE -> this.buildUpdate();
            case DELETE -> this.buildDelete();
            default -> throw new AssertionError("Operação inválida.");
        }
    }

    private void buildSelect() {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ")
                .append(String.join(", ", this.columns))
                .append(" FROM ")
                .append(this.table);

        if (!this.conditions.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(String.join("", this.conditions));
        }

        if (!this.orderBy.isEmpty()) {
            sql.append(" ORDER BY ");
            sql.append(String.join(", ", this.orderBy));
        }

        if (this.limit != null) {
            sql.append(" LIMIT ").append(this.limit);
        }

        if (this.offset != null) {
            sql.append(" OFFSET ").append(this.offset);
        }

        sql.append(";");

        this.query = sql.toString();
    }

    private void buildInsert() {
        StringBuilder sql = new StringBuilder();

        sql.append("INSERT INTO ")
                .append(this.table)
                .append("(")
                .append(String.join(", ", this.values.keySet()))
                .append(")")
                .append(" VALUES ")
                .append("(")
                .append(String.join(", ", this.values.values()))
                .append(")");

        if (!this.conditions.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(String.join("", this.conditions));
        }

        sql.append(";");

        this.query = sql.toString();
    }

    private void buildUpdate() {
        StringBuilder sql = new StringBuilder();

        sql.append("UPDATE ")
                .append(this.table)
                .append(" SET ")
                .append(String.join(" = ?, ", this.values.keySet()))
                .append(" = ?");

        if (!this.conditions.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(String.join("", this.conditions));
        }

        sql.append(";");

        this.query = sql.toString();
    }

    private void buildDelete() {
        StringBuilder sql = new StringBuilder();

        sql.append("DELETE FROM ").append(this.table);

        if (this.conditions.isEmpty()) {
            throw new IllegalStateException("Definir condições para DELETE.");
        }

        sql.append(" WHERE ");
        sql.append(String.join("", this.conditions));

        sql.append(";");

        this.query = sql.toString();
    }

    public List<Object> getParameters() {
        return this.parameters;
    }

    public String getSQL() {
        this.build();
        return this.query;
    }

}
