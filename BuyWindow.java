package GUI;

import Database.PortfolioDatabase;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class BuyWindow extends Application {

    private String ticker;
    private int status;

    public BuyWindow() {
    }

    public BuyWindow(String ticker, int status) {
        this.ticker = ticker;
        this.status = status;
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setMinSize(300, 300);
        grid.setHgap(5);
        grid.setVgap(5);

        Label label = new Label(status == 1? "Buy " : "Sell" + ticker);
        Label qty = new Label("Enter Quantity: ");
        Label mkt = new Label("Order Type: Market Order");
        TextField getQuantity = new TextField("100");
        Button buy = new Button("Place Order");

        grid.add(label, 0, 0);
        grid.add(qty, 0, 1);
        grid.add(mkt, 0, 2);
        grid.add(getQuantity, 0, 3);
        grid.add(buy, 0, 4);

        buy.setOnAction(e1 -> {
            TextField confirmation = new TextField("Order Placed Successfully");
            confirmation.setEditable(false);
            Scene scene = new Scene(confirmation, 200, 100);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            scene.getStylesheets().add("GUI/darktheme.css");
            primaryStage.show();
            PortfolioDatabase.updateDatabase(LoginWindow.getCurrentUser(), ticker, Integer.parseInt(getQuantity.getText()), status == 1? "Buy " : "Sell");
        });

        Scene scene = new Scene(grid);
        primaryStage.setTitle(ticker + " Buy Window");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        scene.getStylesheets().add("GUI/darktheme.css");
        primaryStage.show();
    }
}
