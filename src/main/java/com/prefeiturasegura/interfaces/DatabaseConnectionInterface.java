/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.prefeiturasegura.interfaces;

import java.util.List;
import java.util.Map;

public interface DatabaseConnectionInterface {

    void connect();

    List<Map<String, Object>> fetchAll(String sql, List<Object> params);

    List<Object> fetchColumn(String sql, List<Object> params);

    int executeUpdate(String sql, List<Object> params);

    void close();

}
