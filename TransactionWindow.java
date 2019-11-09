package GUI;

import Data.Plot;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class TransactionWindow extends Application {

    private Button buy;
    private Button sell;
    private Button plot;
    private String ticker;

    public TransactionWindow() {
        start(new Stage());
    }

    public TransactionWindow(String ticker) {
        this.ticker = ticker;
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10, 50, 50, 50));

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        buy = new Button("Buy");
        sell = new Button("Sell");
        plot = new Button("Plot");

        gridPane.add(buy, 0, 0);
        gridPane.add(sell, 1, 0);
        gridPane.add(plot, 2, 0);

        borderPane.setCenter(gridPane);
        buy.setOnAction(e -> {
            primaryStage.close();
            new BuyWindow(ticker, 1);
        });

        sell.setOnAction(e -> {
            primaryStage.close();
            new BuyWindow(ticker, 0);
        });

        plot.setOnAction(e -> {
            try {
                Plot.plot(ticker);
            } catch (Throwable t) {
                AlertMessage.getAlert("Error. API limit crossed. Please try again after sometime.");
            }
        });

        Scene scene = new Scene(borderPane);
        primaryStage.setTitle("TransactionWindow");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        scene.getStylesheets().add("GUI/darktheme.css");
        primaryStage.show();
    }
}
