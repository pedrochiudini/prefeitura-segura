/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.prefeiturasegura.infrastructures;

import com.prefeiturasegura.interfaces.DatabaseConnectionInterface;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author imply
 */
public class MysqlConnection implements DatabaseConnectionInterface {

    private Connection conn;

    private final String host;
    private final String dbName;
    private final String user;
    private final String pass;

    public MysqlConnection(DatabaseConfig config) {
        this.host = config.host();
        this.dbName = config.dbName();
        this.user = config.user();
        this.pass = config.pass();
    }

    @Override
    public void connect() {
        try {
            String url = "jdbc:mysql://" + this.host + "/" + this.dbName;
            this.conn = DriverManager.getConnection(url, this.user, this.pass);
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao conectar no MySQL", e);
        }
    }

    @Override
    public List<Map<String, Object>> fetchAll(String sql, List<Object> params) {
        List<Map<String, Object>> resultado = new ArrayList<>();

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            bindParams(stmt, params);

            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int colunas = meta.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= colunas; i++) {
                        row.put(meta.getColumnLabel(i), rs.getObject(i));
                    }
                    resultado.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao executar fetchAll: " + sql, e);
        }

        return resultado;
    }

    @Override
    public List<Object> fetchColumn(String sql, List<Object> params) {
        List<Object> resultado = new ArrayList<>();

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            bindParams(stmt, params);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultado.add(rs.getObject(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao executar fetchColumn: " + sql, e);
        }

        return resultado;
    }

    @Override
    public int executeUpdate(String sql, List<Object> params) {
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            bindParams(stmt, params);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao executar update: " + sql, e);
        }
    }

    private void bindParams(PreparedStatement stmt, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }
    }

    @Override
    public void close() {
        try {
            if (this.conn != null) {
                this.conn.close();
            }
        } catch (SQLException e) {
            // gerar log para registro
        }
    }
}
