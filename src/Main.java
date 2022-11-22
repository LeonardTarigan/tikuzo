import java.sql.*;

public class Main {
    public static void main(String[] args) {
        new Login().setVisible(true);
        String url = "jdbc:sqlserver://LEONARDS-ACER\\SQLEXPRESS;database=University;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";

        try {
            Connection con = DriverManager.getConnection(url);
        }catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}