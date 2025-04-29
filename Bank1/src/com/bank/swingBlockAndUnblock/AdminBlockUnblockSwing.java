package com.bank.swingBlockAndUnblock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;

import Bank.database_connectivity.Db_Config;

public class AdminBlockUnblockSwing {

    public static void showBlockUnblockUI(JFrame parent) {
        JFrame frame = new JFrame("Block / Unblock Customer Account");
        frame.setSize(400, 250);
        frame.setLocationRelativeTo(parent);
        frame.setLayout(new GridLayout(4, 1, 10, 10));

        // User ID input
        JPanel idPanel = new JPanel(new FlowLayout());
        JLabel idLabel = new JLabel("User ID:");
        JTextField idField = new JTextField(20);
        idPanel.add(idLabel);
        idPanel.add(idField);

        // Choice dropdown
        JPanel choicePanel = new JPanel(new FlowLayout());
        JLabel choiceLabel = new JLabel("Action:");
        String[] actions = {"Block (Deactivate)", "Unblock (Activate)"};
        JComboBox<String> choiceBox = new JComboBox<>(actions);
        choicePanel.add(choiceLabel);
        choicePanel.add(choiceBox);

        // Update button
        JButton updateButton = new JButton("Update Status");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateButton);

        // Message label
        JLabel messageLabel = new JLabel("", JLabel.CENTER);

        // Add panels to frame
        frame.add(idPanel);
        frame.add(choicePanel);
        frame.add(buttonPanel);
        frame.add(messageLabel);

        // Button action
        updateButton.addActionListener((ActionEvent e) -> {
            String userIdText = idField.getText().trim();
            if (userIdText.isEmpty() || !userIdText.matches("\\d+")) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid numeric User ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            long userId = Long.parseLong(userIdText);
            int choice = choiceBox.getSelectedIndex(); // 0 = Block, 1 = Unblock
            int userActiveValue = (choice == 1) ? 1 : 0;

            try (Connection con = Db_Config.getConnection();
                 PreparedStatement ps = con.prepareStatement("UPDATE account SET user_active = ? WHERE user_id = ?")) {

                ps.setInt(1, userActiveValue);
                ps.setLong(2, userId);
                int updatedRows = ps.executeUpdate();

                if (updatedRows > 0) {
                    messageLabel.setText(userActiveValue == 1 ? "Customer unblocked successfully." : "Customer blocked successfully.");
                } else {
                    messageLabel.setText("User ID not found.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }
}
