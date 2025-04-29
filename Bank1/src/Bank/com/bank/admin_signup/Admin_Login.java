package Bank.com.bank.admin_signup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import Bank.database_connectivity.Db_Config;

public class Admin_Login {

    private static final String DEFAULT_USERNAME = "admin123@gmail.com";
    private static final String DEFAULT_PASSWORD = "123";

    // Login method to validate admin credentials
    public static boolean login(String username, String password) {
        Connection con = null;
        boolean adminLogin = false;

        try {
            con = Db_Config.getConnection();

            // Direct comparison of username and password
            String sql = "SELECT * FROM adminlogin WHERE username = ? AND password = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // If username and password match, login is successful
                adminLogin = true;
            }
        } catch (Exception e) {
            e.printStackTrace(); // Consider using a logger in real applications
        } finally {
            try {
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return adminLogin;
    }

    // Test method for default admin credentials
    public static void main(String[] args) {
        System.out.println(login(DEFAULT_USERNAME, DEFAULT_PASSWORD)); // This should print true if the credentials match
    }
}
