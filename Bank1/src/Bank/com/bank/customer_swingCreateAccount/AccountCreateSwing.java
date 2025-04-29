package Bank.com.bank.customer_swingCreateAccount;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import Bank.database_connectivity.Db_Config;

public class AccountCreateSwing {

    public static void openAccountForm(JFrame parentFrame) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Adds space between components

        // Create form fields
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField contactField = new JTextField(20);
        JTextField cityField = new JTextField(20);
        JTextField dobField = new JTextField(20); // Format: dd/MM/yyyy
        JTextField aadharField = new JTextField(20);
        JTextField panField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField stateField = new JTextField(20);
        JTextField countryField = new JTextField(20);
        JTextField genderField = new JTextField(20);
        JTextField occupationField = new JTextField(20);
        JTextField balanceField = new JTextField(20);
        JTextField accountTypeField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        // Set up labels and fields
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Contact No:"), gbc);
        gbc.gridx = 1;
        panel.add(contactField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("City:"), gbc);
        gbc.gridx = 1;
        panel.add(cityField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("DOB (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        panel.add(dobField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Aadhar No:"), gbc);
        gbc.gridx = 1;
        panel.add(aadharField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("PAN No:"), gbc);
        gbc.gridx = 1;
        panel.add(panField, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        panel.add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(new JLabel("State:"), gbc);
        gbc.gridx = 1;
        panel.add(stateField, gbc);

        gbc.gridx = 0; gbc.gridy = 9;
        panel.add(new JLabel("Country:"), gbc);
        gbc.gridx = 1;
        panel.add(countryField, gbc);

        gbc.gridx = 0; gbc.gridy = 10;
        panel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        panel.add(genderField, gbc);

        gbc.gridx = 0; gbc.gridy = 11;
        panel.add(new JLabel("Occupation:"), gbc);
        gbc.gridx = 1;
        panel.add(occupationField, gbc);

        gbc.gridx = 0; gbc.gridy = 12;
        panel.add(new JLabel("Available Balance:"), gbc);
        gbc.gridx = 1;
        panel.add(balanceField, gbc);

        gbc.gridx = 0; gbc.gridy = 13;
        panel.add(new JLabel("Account Type:"), gbc);
        gbc.gridx = 1;
        panel.add(accountTypeField, gbc);

        gbc.gridx = 0; gbc.gridy = 14;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Show the form in a dialog box
        int result = JOptionPane.showConfirmDialog(parentFrame, panel, "Create New Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Capture form data
            String name = nameField.getText();
            String email = emailField.getText();
            String contact = contactField.getText();
            String city = cityField.getText();
            String dob = dobField.getText();
            String aadhar = aadharField.getText();
            String pan = panField.getText();
            String address = addressField.getText();
            String state = stateField.getText();
            String country = countryField.getText();
            String gender = genderField.getText();
            String occupation = occupationField.getText();
            String balanceText = balanceField.getText();
            String accountType = accountTypeField.getText();
            String password = new String(passwordField.getPassword());

            // Validate inputs
            if (!isValidName(name) || !isValidEmail(email) || !isValidContactNo(contact) || !isValidDateOfBirth(dob)
                    || !isValidAadhar(aadhar) || !isValidPAN(pan) || accountType.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "Invalid input! Please check your entries.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double balance = Double.parseDouble(balanceText);
                if (balance < 0) throw new NumberFormatException();

                insertToDatabase(name, email, contact, city, dob, aadhar, pan, address, state, country, gender, occupation, balance, accountType, password, parentFrame);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentFrame, "Invalid balance amount!", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void insertToDatabase(String name, String email, String contact, String city, String dob, String aadhar,
                                         String pan, String address, String state, String country, String gender, String occupation,
                                         double balance, String accountType, String password, JFrame parent) {

        try (Connection con = Db_Config.getConnection()) {
            // Check Aadhar
            String queryCheckAadhar = "SELECT user_id FROM customerinfo WHERE aadhar_no = ?";
            PreparedStatement ps = con.prepareStatement(queryCheckAadhar);
            ps.setString(1, aadhar);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(parent, "Account already exists for this Aadhar number.", "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Insert into customerinfo
            String insertCustomer = "INSERT INTO customerinfo (name, email, contact_no, city, date_of_birth, pan_no, address, state, country, aadhar_no, gender, occupation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(insertCustomer, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, name); ps.setString(2, email); ps.setString(3, contact); ps.setString(4, city); ps.setString(5, dob);
            ps.setString(6, pan); ps.setString(7, address); ps.setString(8, state); ps.setString(9, country);
            ps.setString(10, aadhar); ps.setString(11, gender); ps.setString(12, occupation);

            int x = ps.executeUpdate();

            if (x > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    int userId = keys.getInt(1);
                    long accNo = generateAcc_no();

                    String insertAcc = "INSERT INTO account (account_no, user_id, account_type, avail_balance, password, created_at, updated_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
                    PreparedStatement accPs = con.prepareStatement(insertAcc);
                    accPs.setLong(1, accNo); accPs.setInt(2, userId); accPs.setString(3, accountType);
                    accPs.setDouble(4, balance); accPs.setString(5, password);

                    if (accPs.executeUpdate() > 0) {
                        JOptionPane.showMessageDialog(parent, "Account created successfully!\nAccount No: " + accNo, "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(parent, "Failed to create account.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------- Validations ------------------

    private static boolean isValidName(String name) {
        return Pattern.matches("^[a-zA-Z]+ [a-zA-Z]+$", name);
    }

    private static boolean isValidEmail(String email) {
        return Pattern.matches("^[\\w-]+@([\\w-]+\\.)+[a-zA-Z]{2,7}$", email);
    }

    private static boolean isValidContactNo(String contact_no) {
        return Pattern.matches("^\\d{10}$", contact_no);
    }

    private static boolean isValidAadhar(String aadhar_no) {
        return Pattern.matches("^\\d{12}$", aadhar_no);
    }

    private static boolean isValidPAN(String pan_no) {
        return Pattern.matches("^[A-Z]{5}[0-9]{4}[A-Z]{1}$", pan_no);
    }

    private static boolean isValidDateOfBirth(String dob) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            Date date = sdf.parse(dob);
            return date.before(new Date());
        } catch (ParseException e) {
            return false;
        }
    }

    private static long generateAcc_no() {
        return (long) (Math.random() * 900000000000L) + 100000000000L;
    }
}
