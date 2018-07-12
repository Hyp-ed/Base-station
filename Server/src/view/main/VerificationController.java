package view.main;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * VerificationController class
 *
 * @author Kofi, HYPED 17/18
 */
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

