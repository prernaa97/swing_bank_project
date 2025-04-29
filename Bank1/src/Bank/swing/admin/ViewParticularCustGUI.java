package Bank.swing.admin;

import javax.swing.*;
import java.sql.*;
import java.awt.*;
import Bank.database_connectivity.Db_Config;

public class ViewParticularCustGUI {

    public static void showUserDetails(JFrame parent) {
        String input = JOptionPane.showInputDialog(parent, "Enter User ID:", "View Particular User", JOptionPane.QUESTION_MESSAGE);

        if (input == null || input.isEmpty()) {
            return; // Cancel or empty input
        }

        try {
            int userId = Integer.parseInt(input);
            viewUserDetails(userId, parent);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent, "Invalid User ID. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void viewUserDetails(int userId, JFrame parent) {
        String query = "SELECT c.user_id, c.name, c.email, c.contact_no, c.city, c.date_of_birth, " +
                       "c.PAN_no, c.address, c.state, c.country, c.aadhar_no, c.gender, c.occupation, " +
                       "a.account_id, a.account_no, a.ifsc_code, a.account_type, a.avail_balance " +
                       "FROM customerinfo c " +
                       "LEFT JOIN account a ON c.user_id = a.user_id " +
                       "WHERE c.user_id = ?";

        try (Connection con = Db_Config.getConnection(); 
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("========= Customer Details =========\n");
                    sb.append("User ID: ").append(rs.getInt("user_id")).append("\n");
                    sb.append("Name: ").append(rs.getString("name")).append("\n");
                    sb.append("Email: ").append(rs.getString("email")).append("\n");
                    sb.append("Contact No: ").append(rs.getString("contact_no")).append("\n");
                    sb.append("City: ").append(rs.getString("city")).append("\n");
                    sb.append("DOB: ").append(rs.getString("date_of_birth")).append("\n");
                    sb.append("PAN: ").append(rs.getString("PAN_no")).append("\n");
                    sb.append("Address: ").append(rs.getString("address")).append("\n");
                    sb.append("State: ").append(rs.getString("state")).append("\n");
                    sb.append("Country: ").append(rs.getString("country")).append("\n");
                    sb.append("Aadhar: ").append(rs.getString("aadhar_no")).append("\n");
                    sb.append("Gender: ").append(rs.getString("gender")).append("\n");
                    sb.append("Occupation: ").append(rs.getString("occupation")).append("\n");

                    sb.append("\n========= Account Details =========\n");
                    sb.append("Account ID: ").append(rs.getInt("account_id")).append("\n");
                    sb.append("Account No: ").append(rs.getString("account_no")).append("\n");
                    sb.append("IFSC Code: ").append(rs.getString("ifsc_code")).append("\n");
                    sb.append("Account Type: ").append(rs.getString("account_type")).append("\n");
                    sb.append("Available Balance: ₹").append(rs.getDouble("avail_balance")).append("\n");

                    // Displaying the user details in a JTextArea
                    JTextArea textArea = new JTextArea(sb.toString());
                    textArea.setEditable(false);
                    textArea.setFont(new Font("Arial", Font.PLAIN, 14));
                    textArea.setBackground(new Color(240, 240, 240));
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(600, 400)); // Adjusted size for better UI

                    JOptionPane.showMessageDialog(parent, scrollPane, "User Account Details", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parent, "User not found.", "No Results", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
