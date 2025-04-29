package com.bank.swing.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

import Bank.database_connectivity.Db_Config;

public class AdminGetAllListSwing {

    public static void showAllUsers(JFrame parent) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String[] columns = {
            "User ID", "Name", "Email", "Mobile", "City", "DOB", "PAN", "Address",
            "State", "Country", "Aadhar", "Gender", "Occupation",
            "Account ID", "Account No", "IFSC", "Account Type", "Balance",
            "Account Created", "Account Updated", "Password"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0);

        try {
            con = Db_Config.getConnection();

            String query = "SELECT " +
                    "c.user_id, c.name, c.email, c.contact_no, c.city, c.date_of_birth, c.PAN_no, " +
                    "c.address, c.state, c.country, c.aadhar_no, c.gender, c.occupation, " +
                    "a.account_id, a.account_no, a.ifsc_code, a.account_type, a.avail_balance, " +
                    "a.created_at AS account_created_at, a.updated_at AS account_updated_at, a.password " +
                    "FROM customerinfo c " +
                    "LEFT JOIN account a ON c.user_id = a.user_id";

            ps = con.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getLong("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("contact_no"),
                    rs.getString("city"),
                    rs.getString("date_of_birth"),
                    rs.getString("PAN_no"),
                    rs.getString("address"),
                    rs.getString("state"),
                    rs.getString("country"),
                    rs.getString("aadhar_no"),
                    rs.getString("gender"),
                    rs.getString("occupation"),
                    rs.getObject("account_id") != null ? rs.getLong("account_id") : null,
                    rs.getString("account_no"),
                    rs.getString("ifsc_code"),
                    rs.getString("account_type"),
                    rs.getObject("avail_balance") != null ? rs.getDouble("avail_balance") : null,
                    rs.getString("account_created_at"),
                    rs.getString("account_updated_at"),
                    rs.getString("password")
                });
            }

            // Create and show frame
            JFrame frame = new JFrame("All Customers and Accounts");
            frame.setSize(1400, 600);
            frame.setLocationRelativeTo(parent);
            frame.setLayout(new BorderLayout());

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            frame.add(scrollPane, BorderLayout.CENTER);

            frame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Error loading user data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
