package com.bank.swing.main;

import javax.swing.*;
import com.bank.swing.CustomerStatement.CustomerStatementSwing;
import com.bank.swing.admin.AdminGetAllListSwing;
import com.bank.swingBlockAndUnblock.AdminBlockUnblockSwing;
import com.bank.swingCustomerTransaction.CustomerTransactionSwing;
import com.bank.swingUpdateCustomer.CustomerUpdateGUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import Bank.com.bank.admin_signup.Admin_Login;
import Bank.com.bank.customer_swingCreateAccount.AccountCreateSwing;
import Bank.database_connectivity.Db_Config;
import Bank.swing.admin.ViewParticularCustGUI;

public class SwingMain1 extends JFrame {

    public SwingMain1() {
        setTitle("Bank Management System");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Welcome to Bank Management System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 70, 140));
        add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JButton adminButton = createStyledButton("Admin Login");
        JButton customerButton = createStyledButton("Customer Login");
        JButton exitButton = createStyledButton("Exit");

        buttonPanel.add(adminButton);
        buttonPanel.add(customerButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.CENTER);

        adminButton.addActionListener(this::adminLogin);
        customerButton.addActionListener(this::customerLogin);
        exitButton.addActionListener(e -> System.exit(0));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 102, 204));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void adminLogin(ActionEvent e) {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Admin Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Admin Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Admin Login", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (Admin_Login.login(username, password)) {
                JOptionPane.showMessageDialog(this, "Login Successful", "Admin", JOptionPane.INFORMATION_MESSAGE);
                showAdminMenu();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Admin Credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAdminMenu() {
        String[] options = {
            "Create Account", "View Particular Customer", "View All Customers",
            "Block/Unblock Account", "Exit Admin"
        };

        while (true) {
            int choice = JOptionPane.showOptionDialog(
                this, "Select an Admin Operation", "Admin Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                options, options[0]);

            if (choice == -1 || choice == 4) break;
            switch (choice) {
                case 0 -> AccountCreateSwing.openAccountForm(this);
                case 1 -> ViewParticularCustGUI.showUserDetails(this);
                case 2 -> AdminGetAllListSwing.showAllUsers(this);
                case 3 -> AdminBlockUnblockSwing.showBlockUnblockUI(this);
            }
        }
    }

    private void customerLogin(ActionEvent e) {
        JTextField idField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("User ID:"));
        panel.add(idField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Customer Login", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int userId = Integer.parseInt(idField.getText());
                String password = new String(passwordField.getPassword());

                Connection con = Db_Config.getConnection();
                String sql = "SELECT * FROM account WHERE user_id = ? AND password = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, userId);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login Successful", "Customer", JOptionPane.INFORMATION_MESSAGE);
                    showCustomerMenu(userId);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "User ID must be a number", "Input Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Something went wrong", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showCustomerMenu(int userId) {
        String[] options = {
            "View Details", "Update Details", "Transfer Money", "View Transaction History", "Exit"
        };

        while (true) {
            int choice = JOptionPane.showOptionDialog(
                this, "Select a Customer Operation", "Customer Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                options, options[0]);

            if (choice == -1 || choice == 4) break;
            switch (choice) {
                case 0 -> ViewParticularCustGUI.showUserDetails(this);
                case 1 -> CustomerUpdateGUI.updateCustomer(this);
                case 2 -> CustomerTransactionSwing.openTransactionForm(this, userId);
                case 3 -> CustomerStatementSwing.showStatement(this, userId);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SwingMain1().setVisible(true);
        });
    }
}
