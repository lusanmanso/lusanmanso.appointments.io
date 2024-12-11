import java.sql.Connection;
// import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDatabase {
    public static void main(String[] args) {
        try {
            Connection conn = MySQLConnection.getConnection();
            System.out.println("Conexión exitosa a la base de datos.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}