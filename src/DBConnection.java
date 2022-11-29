import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private String url = "jdbc:sqlserver://LEONARDS-ACER\\SQLEXPRESS;database=Tikuzo;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
    private Connection con;

    public DBConnection() {
        getConnection();
    }

    public void getConnection() {
        try {
            con = DriverManager.getConnection(getUrl());
        } catch(
                SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
    }

    public Connection getCon() {
        return con;
    }

}
