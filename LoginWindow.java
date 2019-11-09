package GUI;

import Database.Hashing;
import Database.LoginDatabase;
import Email.OTPGeneration;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class LoginWindow extends Application {

    private static String chkUser, chkPassword;

    private Stage stage;

    public LoginWindow() {

    }

    public LoginWindow(int val) {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10, 50, 50, 50));

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(20, 20, 20, 30));

        GridPane gridPane = getGridPane();

        Label labelUsername = new Label("Username");
        final TextField textUsername = new TextField();
        Label labelPassword = new Label("Password");
        final PasswordField passwordField = new PasswordField();
        Button buttonLogin = new Button("Login");
        final Label labelMessage = new Label();
        textUsername.setText("Username");
        passwordField.setText("Password");
        Hyperlink hyperlink = new Hyperlink("Forgot Password?");

        gridPane.add(labelUsername, 0, 0);
        gridPane.add(textUsername, 1, 0);
        gridPane.add(labelPassword, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(buttonLogin, 1, 2);
        gridPane.add(labelMessage, 1, 3);
        gridPane.add(hyperlink, 0, 4);

        Text text = new Text("LOGIN WINDOW");

        borderPane.setId("bp");
        gridPane.setId("root");
        buttonLogin.setId("buttonLogin");
        text.setId("text");

        buttonLogin.setOnAction(e -> {
            chkUser = textUsername.getText();
            chkPassword = passwordField.getText();
            if (validate(chkUser, chkPassword)) {
                primaryStage.close();
                new TradingWindow();
            } else {
                labelMessage.setText("Invalid Credentials");
            }
            textUsername.setText("");
            passwordField.setText("");
        });

        hyperlink.setOnAction(e -> {
            primaryStage.close();
            primaryStage.setScene(getNewScene());
            primaryStage.setResizable(false);
            primaryStage.show();
        });

        borderPane.setTop(hBox);
        borderPane.setCenter(gridPane);

        Scene scene = new Scene(borderPane);
        primaryStage.setTitle("LoginWindow");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        scene.getStylesheets().add("GUI/darktheme.css");
        primaryStage.show();
    }

    private boolean validate(String username, String password) {
        LoginDatabase.createTable();
        try {
            return Hashing.validatePassword(password, LoginDatabase.getPassword(username));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String getCurrentUser() {
        return chkUser;
    }

    private Scene getNewScene() {
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10, 50, 50, 50));

        GridPane gridPane = getGridPane();

        Label labelUsername = new Label("Username");
        final TextField textUsername = new TextField();
        Button buttonLogin = new Button("Generate OTP");
        Text errorMessage = new Text("Invalid username. Please try again.");//Error message
        errorMessage.setFill(Color.RED);
        errorMessage.setVisible(false);

        Text flow = new Text("OTP will be sent to your registered Email ID");
        flow.setFill(Color.WHITE);
        gridPane.add(labelUsername, 0, 0);
        gridPane.add(textUsername, 1, 0);
        gridPane.add(errorMessage, 0, 1);
        gridPane.add(buttonLogin, 0, 2);

        borderPane.setCenter(gridPane);
        borderPane.setBottom(flow);

        buttonLogin.setOnAction(e -> {
            try {
                OTPGeneration.getOTP(textUsername.getText());
                stage.close();
                setOTPScene();
                stage.setResizable(false);
            } catch (IllegalArgumentException ex) {
                errorMessage.setVisible(true);
            } catch (RuntimeException ex) {

            }
        });

        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add("GUI/darktheme.css");
        return scene;
    }

    private GridPane getGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        return gridPane;
    }

    private void setOTPScene() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10, 50, 50, 50));

        GridPane gridPane = getGridPane();

        Label OTP = new Label("Enter OTP");
        TextField textField = new TextField();
        Button submit = new Button("Submit");
        Text errorMessage = new Text("Invalid OTP. Please try again.");
        errorMessage.setFill(Color.RED);
        errorMessage.setVisible(false);

        gridPane.add(OTP, 0, 0);
        gridPane.add(textField, 1, 0);
        gridPane.add(submit, 1, 1);

        pane.setCenter(gridPane);
        pane.setBottom(errorMessage);

        submit.setOnAction(e -> {
            int otp = Integer.parseInt(textField.getText());
            if(OTPGeneration.validate(otp)) {
                updatePassword();
            } else {
                errorMessage.setVisible(true);
            }
        });

        Scene scene = new Scene(pane);
        scene.getStylesheets().add("GUI/darktheme.css");
        stage.setScene(scene);
        stage.show();
    }

    private void updatePassword() {
        stage.close();

        BorderPane borderPane = new BorderPane();
        GridPane gridPane = getGridPane();
        borderPane.setPadding(new Insets(10, 50, 50, 50));

        Label label1 = new Label("Enter Password");
        Label label2 = new Label("Confirm Password");
        Button submit = new Button("Submit");
        TextField pass1 = new TextField();
        TextField pass2 = new TextField();
        Text errorMessage = new Text("Passwords don't match. Please try again.");
        errorMessage.setVisible(false);
        errorMessage.setFill(Color.RED);

        gridPane.add(label1, 0, 0);
        gridPane.add(label2, 0, 1);
        gridPane.add(pass1, 1, 0);
        gridPane.add(pass2, 1, 1);
        gridPane.add(submit, 0, 2);

        submit.setOnAction(e -> {
            String s1 = pass1.getText();
            String s2 = pass2.getText();

            if(s1.equals(s2)) {
                OTPGeneration.updatePassword(chkUser, s1);
                chkPassword = s1;
                stage.close();
                new LoginWindow(0);
            } else {
                errorMessage.setVisible(true);
            }
        });

        borderPane.setCenter(gridPane);
        borderPane.setBottom(errorMessage);

        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add("GUI/darktheme.css");

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
