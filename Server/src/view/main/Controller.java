package view.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;



import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {

    private static final Logger LOGGER = Logger.getLogger(Controller.class.getSimpleName());
    private static Socket masterSubSock = null;
    private static PrintWriter masterOut = null;

    @FXML
    private Button btnStop;

    @FXML
    private Button btnKillPower;

    @FXML
    private Button btnLaunch;


    @FXML
    public void initialize() {
        btnStop.setOnAction(event -> this.handleBtnStop());

        btnKillPower.setOnAction(event -> this.handleBtnKillPower());

        btnLaunch.setOnAction(event -> this.handleBtnLaunch());
    }

    //sets socket and opens printwriter on it which can then be used to issue commands
    public void setMasterSock(Socket sock)
    {
        masterSubSock = sock;
        try
        {
            masterOut = new PrintWriter(masterSubSock.getOutputStream());
            LOGGER.log(Level.INFO, "ready to issue commands");
        }
        catch (Exception e)
        {
            LOGGER.log(Level.INFO, "ERROR INITIALISING PRINTWRITER TO MASTER.");
        }
    }


//when a button is pressed the corresponding function sends a command to the client

    private void handleBtnStop() {
        //masterOut.println("STOP");
        //masterOut.flush();
        LOGGER.log(Level.INFO, "stop button called");
    }


    private void handleBtnKillPower() {
        masterOut.println("KILLPOWER");
        masterOut.flush();
        LOGGER.log(Level.INFO, "kill power button called");
    }



    private void handleBtnLaunch() {
        masterOut.println("LAUNCH");
        masterOut.flush();
        LOGGER.log(Level.INFO, "launch button called");
    }



}
