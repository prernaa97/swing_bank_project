package com.bank.swingCustomerTransaction;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

import Bank.database_connectivity.Db_Config;

public class CustomerTransactionSwing {

    public static void openTransactionForm(JFrame parentFrame, int userId) {
        JTextField toAccountField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField recipientNameField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Recipient's Account Number:"));
        panel.add(toAccountField);
        panel.add(new JLabel("Amount to Transfer:"));
        panel.add(amountField);
        panel.add(new JLabel("Recipient's Name:"));
        panel.add(recipientNameField);

        int result = JOptionPane.showConfirmDialog(parentFrame, panel, "Transfer Money", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            long toAccount = Long.parseLong(toAccountField.getText().trim());
            double amount = Double.parseDouble(amountField.getText().trim());
            String recipientName = recipientNameField.getText().trim();
            String transferType = "transfer";

            if (toAccount <= 0 || amount <= 0 || userId <= 0 || recipientName.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "All fields are required and must be valid.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection con = Db_Config.getConnection()) {
                con.setAutoCommit(false);

                try (
                    PreparedStatement psSenderAcc = con.prepareStatement("SELECT Account_no, avail_balance FROM account WHERE user_id = ?");
                    PreparedStatement psReceiverAcc = con.prepareStatement("SELECT avail_balance FROM account WHERE Account_no = ?");
                    PreparedStatement psUpdateSender = con.prepareStatement("UPDATE account SET avail_balance = ? WHERE Account_no = ?");
                    PreparedStatement psUpdateReceiver = con.prepareStatement("UPDATE account SET avail_balance = ? WHERE Account_no = ?");
                    PreparedStatement psInsertTransaction = con.prepareStatement("INSERT INTO transaction (account_no, from_account, to_account, amount, transfer_type, recipient_Name, user_id, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                ) {
                    // Fetch sender account info
                    psSenderAcc.setLong(1, userId);
                    try (ResultSet rsSender = psSenderAcc.executeQuery()) {
                        if (!rsSender.next()) {
                            JOptionPane.showMessageDialog(parentFrame, "Sender account not found.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        long fromAccount = rsSender.getLong("Account_no");
                        double senderBalance = rsSender.getDouble("avail_balance");

                        if (fromAccount == toAccount) {
                            JOptionPane.showMessageDialog(parentFrame, "Cannot transfer to your own account.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        if (senderBalance < amount) {
                            JOptionPane.showMessageDialog(parentFrame, "Insufficient balance.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // Fetch receiver account info
                        psReceiverAcc.setLong(1, toAccount);
                        try (ResultSet rsReceiver = psReceiverAcc.executeQuery()) {
                            if (!rsReceiver.next()) {
                                JOptionPane.showMessageDialog(parentFrame, "Receiver account not found.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            double receiverBalance = rsReceiver.getDouble("avail_balance");

                            // Update balances
                            psUpdateSender.setDouble(1, senderBalance - amount);
                            psUpdateSender.setLong(2, fromAccount);
                            psUpdateSender.executeUpdate();

                            psUpdateReceiver.setDouble(1, receiverBalance + amount);
                            psUpdateReceiver.setLong(2, toAccount);
                            psUpdateReceiver.executeUpdate();

                            // Insert into transaction table
                            psInsertTransaction.setLong(1, fromAccount);
                            psInsertTransaction.setLong(2, fromAccount);
                            psInsertTransaction.setLong(3, toAccount);
                            psInsertTransaction.setDouble(4, amount);
                            psInsertTransaction.setString(5, transferType);
                            psInsertTransaction.setString(6, recipientName);
                            psInsertTransaction.setLong(7, userId);
                            psInsertTransaction.setString(8, "Transferred " + amount + " to " + recipientName + " (" + toAccount + ")");
                            psInsertTransaction.executeUpdate();

                            con.commit();
                            JOptionPane.showMessageDialog(parentFrame, "Transaction successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } catch (Exception innerEx) {
                    con.rollback();
                    throw innerEx;
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parentFrame, "Invalid input. Please enter valid numeric values.", "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame, "Transaction failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
