/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.prefeiturasegura.factories;

import com.prefeiturasegura.infrastructures.DatabaseConfig;
import com.prefeiturasegura.infrastructures.MysqlConnection;
import com.prefeiturasegura.infrastructures.PostgresConnection;
import com.prefeiturasegura.interfaces.DatabaseConnectionInterface;

public class DatabaseConnectionFactory {

    public static DatabaseConnectionInterface create(String driver, DatabaseConfig config) {
        return switch (driver) {
            case "POSTGRES" -> new PostgresConnection(config);
            case "MYSQL" -> new MysqlConnection(config);
            default -> throw new IllegalStateException("Driver inválido: " + driver);
        };
    }

}
