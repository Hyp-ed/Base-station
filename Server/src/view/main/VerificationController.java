package view.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class VerificationController {


    @FXML
    private JFXButton confirmBtn;

//    @FXML
//    private JFXTextField password;

//    @FXML
//    private Label errorLabel;

    @FXML
    public void initialize() {
        System.out.println("Called VerificationController.initialize");
        confirmBtn.setOnAction(event -> this.handleBtnConfirm());
    }

    private void handleBtnConfirm() {
//        String pass = password.getText();
//        if(pass.equals("hyped")) {
            ((Stage) confirmBtn.getScene().getWindow()).close();
//        } else {
//            errorLabel.setOpacity(1);
//        }
    }
}

