/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.prefeiturasegura;

import com.prefeiturasegura.infrastructures.Database;
import com.prefeiturasegura.utils.QueryBuilder;

public class Main {

    public static void main(String[] args) {
        try {
            Database.getInstance().getConnection();
        } catch (Exception e) {
        }
    }
}
