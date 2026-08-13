/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.prefeiturasegura.infrastructures;

import com.prefeiturasegura.factories.DatabaseConnectionFactory;
import com.prefeiturasegura.interfaces.DatabaseConnectionInterface;

public class Database {

    private final DatabaseConnectionInterface connection;

    private Database() {
        String driver = System.getenv().getOrDefault("DB_DRIVER", "POSTGRES");

        DatabaseConfig config = new DatabaseConfig(
            System.getenv("DB_HOST"),
            System.getenv("DB_NAME"),
            System.getenv("DB_USER"),
            System.getenv("DB_PASSWORD")
        );

        this.connection = DatabaseConnectionFactory.create(driver, config);
        this.connection.connect();
    }
    
    private static class Holder {
        private static final Database INSTANCE = new Database();
    }

    public static Database getInstance() {
        return Holder.INSTANCE;
    }

    public DatabaseConnectionInterface getConnection() {
        return connection;
    }

}
