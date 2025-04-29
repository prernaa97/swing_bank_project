package com.bank.swing.CustomerStatement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

import Bank.database_connectivity.Db_Config;

public class CustomerStatementSwing {

    public static void showStatement(JFrame parent, long userId) {
        Connection con = null;
        PreparedStatement psAccount = null;
        PreparedStatement psTransaction = null;
        ResultSet rsAccount = null;
        ResultSet rsTransaction = null;

        try {
            con = Db_Config.getConnection();

            // Get account details
            String accountQuery = "SELECT Account_no, avail_balance FROM account WHERE user_id = ?";
            psAccount = con.prepareStatement(accountQuery);
            psAccount.setLong(1, userId);
            rsAccount = psAccount.executeQuery();

            if (!rsAccount.next()) {
                JOptionPane.showMessageDialog(parent, "Account not found for User ID: " + userId, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            long accountNo = rsAccount.getLong("Account_no");
            double availBalance = rsAccount.getDouble("avail_balance");

            // Frame setup
            JFrame frame = new JFrame("Account Statement - User ID: " + userId);
            frame.setSize(900, 500);
            frame.setLocationRelativeTo(parent);
            frame.setLayout(new BorderLayout());

            // Top panel for account info
            JPanel infoPanel = new JPanel(new GridLayout(3, 1));
            infoPanel.add(new JLabel("User ID: " + userId));
            infoPanel.add(new JLabel("Account No: " + accountNo));
            infoPanel.add(new JLabel("Available Balance: ₹" + String.format("%.2f", availBalance)));

            // Table for transactions
            String[] columns = {"Transaction ID", "Amount", "Date & Time", "Type", "From", "To", "Status", "Description"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            JTable table = new JTable(model);

            // Fetch transaction history
            String transQuery = "SELECT trans_id, amount, date_time, transfer_type, from_account, to_account, status, description FROM transaction WHERE user_id = ?";
            psTransaction = con.prepareStatement(transQuery);
            psTransaction.setLong(1, userId);
            rsTransaction = psTransaction.executeQuery();

            boolean hasTransactions = false;
            while (rsTransaction.next()) {
                hasTransactions = true;
                model.addRow(new Object[]{
                        rsTransaction.getInt("trans_id"),
                        "₹" + rsTransaction.getDouble("amount"),
                        rsTransaction.getTimestamp("date_time"),
                        rsTransaction.getString("transfer_type"),
                        rsTransaction.getLong("from_account"),
                        rsTransaction.getLong("to_account"),
                        rsTransaction.getString("status"),
                        rsTransaction.getString("description")
                });
            }

            if (!hasTransactions) {
                JOptionPane.showMessageDialog(frame, "No transactions found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }

            JScrollPane scrollPane = new JScrollPane(table);
            frame.add(infoPanel, BorderLayout.NORTH);
            frame.add(scrollPane, BorderLayout.CENTER);
            frame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Error fetching statement: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rsAccount != null) rsAccount.close();
                if (psAccount != null) psAccount.close();
                if (rsTransaction != null) rsTransaction.close();
                if (psTransaction != null) psTransaction.close();
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
