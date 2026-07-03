import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/toko_retail";
    private static final String USER = "root"; // Sesuaikan username DB
    private static final String PASS = ""; // Sesuaikan password DB

    private static Connection conn;

    public static Connection getKoneksi() {
        // Cek apakah koneksi belum ada atau sudah tertutup
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
            } catch (SQLException e) {
                System.out.println("Koneksi Database Gagal: " + e.getMessage());
            }
        }
        return conn;
    }
}