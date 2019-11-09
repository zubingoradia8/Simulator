package Database;

import GUI.AlertMessage;

import java.sql.*;

public class LoginDatabase implements DatabaseConstants {

    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;

    public static void createTable() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
                    // do nothing
        }
    }

    public static String getPassword(String username) {
        try {
            statement.executeUpdate("use javabook");
            resultSet = statement.executeQuery("select password from users where username = '" + username + "'");
            resultSet.next();
            String s = resultSet.getString("password");
            return s;
        } catch (SQLException e) {
            AlertMessage.getAlert("Error. Cannot connect to SQL database.");
            return null;
        }
    }

}
