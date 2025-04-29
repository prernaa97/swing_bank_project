/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Bank.database_connectivity;

import java.sql.Connection;
import java.sql.DriverManager;


public class Db_Config {

    private static final String URL = "jdbc:mysql://localhost:3306/bank1";
    private static final String USER = "root";
    private static final String PASSWORD = "0410";

    private static Connection con = null;

    public static Connection getConnection() {
        try {
            if (con == null || con.isClosed()) { // Ensure only one connection instance
                //Class.forName("com.mysql.cj.jdbc.Driver"); // Load driver
                con = DriverManager.getConnection(URL, USER, PASSWORD);
//                System.out.println("Database connected successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}

