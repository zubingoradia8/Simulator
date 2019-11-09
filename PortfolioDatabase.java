package Database;

import GUI.AlertMessage;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PortfolioDatabase implements DatabaseConstants {

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;
    private static ResultSet resultSet;

    public static void updateDatabase(String username, String ticker, int qty, String type) {
        try {
            establishConnection();
            statement.executeUpdate("use javabook");

            String query = "insert into Portfolio values (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, ticker);
            preparedStatement.setInt(3, qty);
            preparedStatement.setInt(4, type.trim().equals("Buy")? 1 : 0);
            preparedStatement.setString(5, new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            AlertMessage.getAlert("Error. Cannot connect to SQL database.");
        }
    }

    public static ResultSet getDatabase(String username) {
        try {
            establishConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select ticker, qty, typeOfTransaction, dateAndTime from Portfolio where username = '" + username + "'");
        } catch (SQLException e) {
            AlertMessage.getAlert("Error. Cannot connect to the SQL database.");
        }

        return resultSet;
    }

    private static void establishConnection() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
            AlertMessage.getAlert("Error. Cannot connect to the SQL database.");
        }
    }

}
