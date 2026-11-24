package db;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection {
    private static String url;
    private static String user;
    private static String password;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        Properties props = new Properties();
        try (InputStream in = getPropertiesInputStream()) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            System.err.println("Помилка завантаження db.properties: " + e.getMessage());
        }

        url = props.getProperty("db.url", "jdbc:sqlite:company.db");
        user = props.getProperty("db.user", "");
        password = props.getProperty("db.password", "");
    }

    private static InputStream getPropertiesInputStream() {
        InputStream in = DatabaseConnection.class.getResourceAsStream("/db.properties");
        if (in == null) {
            in = DatabaseConnection.class.getResourceAsStream("/resources/db.properties");
        }
        if (in == null) {
            try {
                return new FileInputStream("resources/db.properties");
            } catch (Exception ignored) {
            }
        }
        if (in == null) {
            try {
                return new FileInputStream("db.properties");
            } catch (Exception ignored) {
            }
        }
        return in;
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, password);
        if (url.startsWith("jdbc:sqlite:")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
        }
        return conn;
    }
}
