package duck.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Properties props = new Properties();
                FileInputStream input = new FileInputStream("../release/config.properties");
                props.load(input);

                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");

                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Ket noi thanh cong");
            } catch (IOException e) {
                System.out.println("loi doc file cau hinh: " + e.getMessage());
                throw new SQLException("khong the tai cau hinh");
            } catch (SQLException e) {
                System.out.println("ket noi khong thanh cong: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("dong ket noi");
            }
        } catch (SQLException e) {
            System.out.println("dong ket noi khong thanh cong: " + e.getMessage());
        }
    }
}
