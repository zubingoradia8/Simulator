package GUI;

import Data.GetData;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TradingWindow extends Application {

    private String tickerSelected;
    private ObservableList<Tickers> data;
    private ObservableList<String> tickerList;
    private ComboBox comboBox;
    private ListView<String> listView;
    private TextField DJIA, SP500, NASDAQ;
    private Button portfolio, logout;

    private volatile TableView<Tickers> tableView;
    private volatile LinkedList<String> addedTicker;

    private Lock lock;

    public TradingWindow() {
        start(new Stage());

        lock = new ReentrantLock();
        new Thread(() -> {
            try {
                while (true) {
                    refreshTable();
                    Thread.sleep(60 * 1000);
                }
            } catch (InterruptedException ex) {
                // do nothing
            }
        }).start();

        new Thread(() -> {
            while (true) {
                try {
                    DJIA.setText(GetData.loadData("DJI") + "");
                    SP500.setText(GetData.loadData("INX") + "");
                    //NASDAQ.setText(GetData.loadData("IXIC") + "");
                    Thread.sleep(60 * 1000);
                } catch (Exception ex) {
                    //ex.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void start(Stage primaryStage) {

        FlowPane flowPane = new FlowPane(6, 3);

        tableView = new TableView<>();
        data = FXCollections.observableArrayList();
        tableView.setItems(data);
        setColumns();


        tickerList = FXCollections.observableArrayList();
        listView = new ListView<>();
        listView.setPrefSize(1000, 500);
        listView.setEditable(true);
        tickerList.addAll("MSFT", "AMZN", "AAPL", "CSCO", "INTC", "GE", "AA");
        listView.setItems(tickerList);
        comboBox = new ComboBox(tickerList);
        comboBox.setPrefWidth(200);

        addedTicker = new LinkedList<>();
        comboBox.setOnAction(e -> {
            tickerSelected = comboBox.getValue().toString();
            if (!addedTicker.contains(tickerSelected)) {
                addedTicker.add(tickerSelected);
                try {
                    updateTable();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        DJIA = new TextField("Fetching Data");
        SP500 = new TextField("Fetching Data");
        NASDAQ = new TextField("Fetching Data");
        portfolio = new Button("Portfolio");
        logout = new Button("Logout");
        flowPane.getChildren().addAll(new Label("Tickers: "), comboBox, new Label("DJIA: "), DJIA, new Label("S&P 500: "), SP500, new Label("NASDAQ: "), NASDAQ, portfolio, logout);
        DJIA.setEditable(false);
        SP500.setEditable(false);
        NASDAQ.setEditable(false);


        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(tableView);
        borderPane.setTop(flowPane);

        tableView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String passTicker = tableView.getSelectionModel().getSelectedItem().getTicker();
                if (!passTicker.equals(null))
                    new TransactionWindow(passTicker);
            }
        });

        portfolio.setOnAction(e -> {
            new Portfolio();
        });

        logout.setOnAction(e -> {
            primaryStage.close();
            System.exit(0);
        });

        Scene scene = new Scene(borderPane, 1000, 500);
        primaryStage.setTitle("TradingWindow");
        primaryStage.setScene(scene);
        scene.getStylesheets().add("GUI/darktheme.css");
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public void setColumns() {
        TableColumn tickerColumn = new TableColumn("Tickers");
        tickerColumn.setMinWidth(1000 / 6);
        tickerColumn.setCellValueFactory(new PropertyValueFactory<Tickers, String>("ticker"));

        TableColumn openColumn = new TableColumn("Open");
        openColumn.setMinWidth(1000 / 6);
        openColumn.setCellValueFactory(new PropertyValueFactory<Tickers, Double>("open"));

        TableColumn highColumn = new TableColumn("High");
        highColumn.setMinWidth(1000 / 6);
        highColumn.setCellValueFactory(new PropertyValueFactory<Tickers, Double>("high"));

        TableColumn lowColumn = new TableColumn("Low");
        lowColumn.setMinWidth(1000 / 6);
        lowColumn.setCellValueFactory(new PropertyValueFactory<Tickers, Double>("low"));

        TableColumn closeColumn = new TableColumn("Close");
        closeColumn.setMinWidth(1000 / 6);
        closeColumn.setCellValueFactory(new PropertyValueFactory<Tickers, Double>("close"));

        TableColumn volumeColumn = new TableColumn("Volume");
        volumeColumn.setMinWidth(1000 / 6);
        volumeColumn.setCellValueFactory(new PropertyValueFactory<Tickers, Double>("volume"));


        tableView.getColumns().addAll(tickerColumn, openColumn, highColumn, lowColumn, closeColumn, volumeColumn);
    }

    public void refreshTable() {
        lock.lock();
        Object[] arr = addedTicker.toArray();

        for (Object ticker : arr) {
            Object a[] = new Object[6];
            Object b[] = new Object[6];
            Arrays.fill(b, -1);
            try {
                GetData.loadData(ticker.toString(), a);
                updateValue(ticker, a);
                tableView.refresh();

            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

        lock.unlock();
    }

    public void updateValue(Object ticker, Object arr[]) {
        int index = addedTicker.indexOf(ticker);
        tableView.getItems().get(index).setOpen(Double.parseDouble(arr[1].toString()));
        tableView.getItems().get(index).setHigh(Double.parseDouble(arr[2].toString()));
        tableView.getItems().get(index).setLow(Double.parseDouble(arr[3].toString()));
        tableView.getItems().get(index).setClose(Double.parseDouble(arr[4].toString()));
        tableView.getItems().get(index).setVolume(Double.parseDouble(arr[5].toString()));
    }

    public void updateTable() throws Exception {
        String ticker = comboBox.getValue().toString();
        System.out.println(ticker);
        Object[] arr = new Object[6];
        arr[0] = ticker;

        data.add(new Tickers(ticker, 0, 0, 0, 0, 0));
        refreshTable();
    }

    public static class Tickers {

        private SimpleStringProperty ticker;
        private SimpleDoubleProperty open;
        private SimpleDoubleProperty high;
        private SimpleDoubleProperty low;
        private SimpleDoubleProperty close;
        private SimpleDoubleProperty volume;

        private Tickers(String ticker, double open, double high, double low, double close, double volume) {
            this.ticker = new SimpleStringProperty(ticker);
            this.open = new SimpleDoubleProperty(open);
            this.high = new SimpleDoubleProperty(high);
            this.low = new SimpleDoubleProperty(low);
            this.close = new SimpleDoubleProperty(close);
            this.volume = new SimpleDoubleProperty(volume);
        }

        public String getTicker() {
            return ticker.get();
        }

        public void setTicker(String ticker) {
            this.ticker.set(ticker);
        }

        public double getOpen() {
            return open.get();
        }

        public void setOpen(double open) {
            this.open.set(open);
        }

        public double getHigh() {
            return high.get();
        }

        public void setHigh(double high) {
            this.high.set(high);
        }

        public double getLow() {
            return low.get();
        }

        public void setLow(double low) {
            this.low.set(low);
        }

        public double getClose() {
            return close.get();
        }

        public void setClose(double close) {
            this.close.set(close);
        }

        public double getVolume() {
            return volume.get();
        }

        public void setVolume(double volume) {
            this.volume.set(volume);
        }
    }

}
