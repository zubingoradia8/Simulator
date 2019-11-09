package GUI;

import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

public class AlertMessage {

    public static void getAlert(String errMsg) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setContentText(errMsg);
        alert.show();
    }

}