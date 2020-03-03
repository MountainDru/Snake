/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.bejinariu.snakegame.controllers;

import at.bejinariu.snakegame.MainApp;
import at.bejinariu.snakegame.Utilities;
import at.bejinariu.snakegame.objects.Highscore;
import at.bejinariu.snakegame.objects.Snake;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 * 
 * @author Dru 
 */
public class EndgamePaneController implements Initializable {

    @FXML
    private Label lblPoints;
    private static Image ICON = MainApp.APP_ICON;
    @FXML
    private TextField lblPlayername;
    @FXML
    private AnchorPane root;
    private final BooleanProperty saveButtonDiasable = new SimpleBooleanProperty(false, null);
    @FXML
    private Button btnSave;
    private Snake player;
    private Stage highscoreScreen;
    private HighscorePaneController highscoreController; 

    public Snake getPlayer() {
        return player;
    }

    public void setPlayer(Snake player) {
        this.player = player;
    }

    private boolean bordersActivated;

    public boolean isBordersActivated() {
        return bordersActivated;
    }

    public void setBordersActivated(boolean bordersActivated) {
        this.bordersActivated = bordersActivated;
    }

    private boolean isSaveButtonDiasable() {
        return saveButtonDiasable.get();
    }

    private void setSaveButtonDiasable(boolean value) {
        saveButtonDiasable.set(value);
    }

    private BooleanProperty saveButtonDiasableProperty() {
        return saveButtonDiasable;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnSave.disableProperty().bind(saveButtonDiasable);
        if (root.getScene() != null) {
            try {
                root.getScene().getWindow().setOnCloseRequest(event -> saveButtonDiasable.set(false));
                FXMLLoader highscoreScreenLoader = new FXMLLoader();
                highscoreScreenLoader.setLocation(getClass().getResource("/fxml/HighscorePane.fxml"));
                highscoreScreen = new Stage();
                highscoreScreen.setScene(new Scene(highscoreScreenLoader.load()));
                highscoreScreen.getIcons().add(MainApp.APP_ICON);
                highscoreScreen.setResizable(false);
                highscoreController = highscoreScreenLoader.getController(); 
            } catch (Exception e) {
                System.out.println(e.getClass() + " " + e.getMessage());
            }
        }
    }

    @FXML
    private void onActionSaveScore(ActionEvent event) {
        if (lblPlayername.getText().isEmpty()) {
            Utilities.showNormalMessage("Information", "Field is empty", "In order to save your score, you must type in a valid name. ", Alert.AlertType.INFORMATION, ICON, false);
            return;
        }
        Highscore hs = new Highscore(player.getPoints(), lblPlayername.getText(), LocalDateTime.now(), bordersActivated, player.getSpeed());
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("src/main/resources/data/scores.csv"), Boolean.TRUE))) {
            bw.newLine();
            bw.write(hs.toCSVLine());
            bw.flush();
        } catch (IOException io) {
            System.out.println(io.getClass() + " " + io.getMessage());
        }
        Utilities.showNormalMessage("Information", "Score stored successfully!", "", Alert.AlertType.INFORMATION, ICON, false);
        saveButtonDiasable.set(true);

    }

    @FXML
    private void onActionList(ActionEvent event) {
        highscoreController.readEntries();
        highscoreScreen.show();
    }

    @FXML
    private void onActionCancel(ActionEvent event) {
        closeStage();
    }

    private void closeStage() {
        Stage thisStage = (Stage) this.root.getScene().getWindow();
        saveButtonDiasable.set(false);
        thisStage.close();
    }

    public void updatePoints() {
        lblPlayername.setText("");
        this.lblPoints.setText("Congratulations! You have achieved " + player.pointsProperty().get() + " points!");
    }

}
