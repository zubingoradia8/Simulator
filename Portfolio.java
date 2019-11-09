package GUI;

import Database.PortfolioDatabase;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Portfolio extends Application {

    private String ticker;
    private String type;
    private int qty;
    private TableView tableView;
    private ObservableList<Stonks> data;

    public Portfolio() {
        start(new Stage());
    }

    public Portfolio(String ticker, String type, int qty) {
        this.ticker = ticker;
        this.type = type;
        this.qty = qty;
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        tableView = new TableView<>();
        data = FXCollections.observableArrayList();
        tableView.setItems(data);
        setColumns();

        try {
            createTable();
        } catch (SQLException e) {
            AlertMessage.getAlert("Error. Cannot connect to the SQL database.");
        }

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(tableView);

        Scene scene = new Scene(borderPane, 1000, 500);
        primaryStage.setTitle("Portfolio");
        primaryStage.setScene(scene);
        scene.getStylesheets().add("GUI/darktheme.css");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void createTable() throws SQLException {
        String username = LoginWindow.getCurrentUser();
        ResultSet set = PortfolioDatabase.getDatabase(username);

        while(set.next())
            data.add(new Stonks(set.getString("ticker"), set.getInt("typeOfTransaction") == 1? "Buy" : "Sell", set.getInt("qty"), set.getString("dateAndTime")));
    }

    public void setColumns() {
        TableColumn tickerColumn = new TableColumn("Tickers");
        tickerColumn.setMinWidth(1000 / 4);
        tickerColumn.setCellValueFactory(new PropertyValueFactory<TradingWindow.Tickers, String>("ticker"));

        TableColumn typeColumn = new TableColumn("Type");
        typeColumn.setMinWidth(1000 / 4);
        typeColumn.setCellValueFactory(new PropertyValueFactory<TradingWindow.Tickers, Double>("type"));

        TableColumn qtyColumn = new TableColumn("Quantity");
        qtyColumn.setMinWidth(1000 / 4);
        qtyColumn.setCellValueFactory(new PropertyValueFactory<TradingWindow.Tickers, Double>("qty"));

        TableColumn date = new TableColumn("Date");
        date.setMinWidth(1000 / 4);
        date.setCellValueFactory(new PropertyValueFactory<TradingWindow.Tickers, String>("date"));

        tableView.getColumns().addAll(tickerColumn, typeColumn, qtyColumn, date);
    }

    public static class Stonks {

        private SimpleStringProperty ticker;
        private SimpleStringProperty type;
        private SimpleIntegerProperty qty;
        private SimpleStringProperty date;

        private Stonks(String ticker, String type, int qty, String date) {
            this.ticker = new SimpleStringProperty(ticker);
            this.type = new SimpleStringProperty(type);
            this.qty = new SimpleIntegerProperty(qty);
            this.date = new SimpleStringProperty(date);
        }

        public String getTicker() {
            return ticker.get();
        }

        public void setTicker(String ticker) {
            this.ticker.set(ticker);
        }

        public String getType() {
            return type.get();
        }

        public void setType(String type) {
            this.type.set(type);
        }

        public int getQty() {
            return qty.get();
        }

        public void setQty(int qty) {
            this.qty.set(qty);
        }

        public String getDate() {
            return date.get();
        }

        public void setDate(String date) {
            this.date.set(date);
        }
    }
}
