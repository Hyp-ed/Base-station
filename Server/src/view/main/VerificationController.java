package view.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class VerificationController {


    @FXML
    private JFXButton confirmBtn;

    @FXML
    private JFXTextField password;

    @FXML
    public void initialize() {
        System.out.println("Called VerificationController.initialize");
        confirmBtn.setOnAction(event -> this.handleBtnConfirm());
    }

    private void handleBtnConfirm() {
        ((Stage) password.getScene().getWindow()).close();
    }
}

