package com.bank.swingUpdateCustomer;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import Bank.database_connectivity.Db_Config;

public class CustomerUpdateGUI {

    public static void updateCustomer(JFrame parent) {
        String input = JOptionPane.showInputDialog(parent, "Enter User ID:", "Update Customer Details", JOptionPane.QUESTION_MESSAGE);
        if (input == null || input.trim().isEmpty()) return;

        long userId;
        try {
            userId = Long.parseLong(input.trim());
        } catch (NumberFormatException e) {
            showError(parent, "Invalid User ID. Please enter a valid number.");
            return;
        }

        try (Connection con = Db_Config.getConnection()) {
            if (!isCustomerExist(con, userId)) {
                showWarning(parent, "No customer found with User ID: " + userId);
                return;
            }

            String[] options = {"No Change", "Update Name", "Update Mobile Number", "Update Email", "Update All Details"};
            String choice = (String) JOptionPane.showInputDialog(
                    parent, "Select the field you want to update:",
                    "Choose Update Option",
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]
            );

            if (choice == null || choice.equals("No Change")) {
                showInfo(parent, "No update performed.");
                return;
            }

            switch (choice) {
                case "Update Name" -> updateSingleField(con, "name", "Enter new customer name:", userId, parent);
                case "Update Mobile Number" -> updateSingleField(con, "contact_no", "Enter new mobile number:", userId, parent);
                case "Update Email" -> updateSingleField(con, "email", "Enter new email address:", userId, parent);
                case "Update All Details" -> updateAllFields(con, userId, parent);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError(parent, "An unexpected error occurred: " + e.getMessage());
        }
    }

    private static boolean isCustomerExist(Connection con, long userId) {
        String query = "SELECT COUNT(*) FROM customerinfo WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void updateSingleField(Connection con, String fieldName, String message, long userId, JFrame parent) {
        String newValue = JOptionPane.showInputDialog(parent, message);
        if (newValue == null || newValue.trim().isEmpty()) {
            showWarning(parent, "Input cannot be empty.");
            return;
        }

        String sql = "UPDATE customerinfo SET " + fieldName + " = ? WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newValue.trim());
            ps.setLong(2, userId);
            int result = ps.executeUpdate();
            if (result > 0) {
                showInfo(parent, fieldName.toUpperCase() + " updated successfully.");
            } else {
                showError(parent, "Failed to update " + fieldName + ".");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError(parent, "Error updating " + fieldName + ": " + e.getMessage());
        }
    }

    private static void updateAllFields(Connection con, long userId, JFrame parent) {
        String name = JOptionPane.showInputDialog(parent, "Enter new customer name:");
        String mobile = JOptionPane.showInputDialog(parent, "Enter new mobile number:");
        String email = JOptionPane.showInputDialog(parent, "Enter new email address:");

        if (name == null || mobile == null || email == null || 
            name.trim().isEmpty() || mobile.trim().isEmpty() || email.trim().isEmpty()) {
            showWarning(parent, "All fields are required.");
            return;
        }

        String sql = "UPDATE customerinfo SET name = ?, contact_no = ?, email = ? WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, mobile.trim());
            ps.setString(3, email.trim());
            ps.setLong(4, userId);

            int result = ps.executeUpdate();
            if (result > 0) {
                showInfo(parent, "All details updated successfully.");
            } else {
                showError(parent, "Failed to update customer details.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError(parent, "Error updating details: " + e.getMessage());
        }
    }

    // Helper methods for message popups
    private static void showInfo(JFrame parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void showWarning(JFrame parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private static void showError(JFrame parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
