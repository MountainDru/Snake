/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.bejinariu.snakegame.controllers;

import at.bejinariu.snakegame.MainApp;
import at.bejinariu.snakegame.objects.Timer;
import at.bejinariu.snakegame.objects.Snake;
import at.bejinariu.snakegame.objects.Snake.Speed;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Dru
 */
public class SnakePaneController implements Initializable {

    //Konstanten des Arrays
    private static final int EMPTY = 0;
    private static final int BORDER = 1024;

    private Snake player;
    private EndgamePaneController endGameController;
    private final GridPane gamePane = new GridPane();
    private final int[][] gameArray = new int[40][40];    //Quadratisch
    @FXML
    private BorderPane root;

    private final static Random rd = new Random();
    private ImageView pointPicture;
    @FXML
    private Button btnStartGame;
    private List<String> imageNames = new ArrayList<>();
    @FXML
    private ColorPicker clrPckSnakeColor;
    @FXML
    private ChoiceBox<Snake.Speed> chsboxSpeed;
    @FXML
    private CheckBox chboxBorders;
    private Timer renderer;
    @FXML
    private Label lblPoints;
    private Stage endScreen;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gamePane.setPrefHeight(570);
        gamePane.setPrefWidth(570);
        gamePane.setHgap(0);
        gamePane.setVgap(0);
        gamePane.setPadding(new Insets(10, 0, 10, 10));
        gamePane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        pointPicture = new ImageView();
        chsboxSpeed.setItems(FXCollections.observableArrayList(Arrays.asList(Speed.values())));
        chsboxSpeed.getSelectionModel().select(0);
        clrPckSnakeColor.setValue(Color.RED);
        FXMLLoader endScreenLoader = new FXMLLoader();
        endScreenLoader.setLocation(getClass().getResource("/fxml/EndgamePane.fxml"));
        try {
            endScreen = new Stage();
            endScreen.setScene(new Scene((AnchorPane) endScreenLoader.load()));
            endGameController = endScreenLoader.getController();
            endGameController.initialize(null, null);
            Path imageDirectory = Paths.get("src", "main", "resources", "pictures", "snakefood"); 
            imageNames.addAll(
                    Files.walk(imageDirectory, FileVisitOption.FOLLOW_LINKS)
                            .map(path -> path.getName(path.getNameCount() - 1).toString())
                            .filter(imageName -> imageName.endsWith(".png") || imageName.endsWith(".jpg"))
                            .collect(Collectors.toList())
            );
           
        } catch (IOException io) {
            System.out.println(io.getClass() + " " + io.getMessage());
        }
        endScreen.getIcons().add(MainApp.APP_ICON);
    }

    public void drawGame() {
        gamePane.getChildren().clear();
        int id = player.getIdentifier();
        double radius = (gamePane.getPrefHeight()) / gameArray.length / 2;
        for (int i = 0; i < gameArray.length; i++) {
            for (int j = 0; j < gameArray[0].length; j++) {
                Rectangle part = new Rectangle(radius * 2, radius * 2);
                if (gameArray[i][j] == EMPTY) {
                    part.setFill(Color.WHITE);
                    gamePane.add(part, j, i);
                } else if (gameArray[i][j] == BORDER) {
                    part.setFill(Color.rgb(184, 184, 184)); // #B8B8B8
                    gamePane.add(part, j, i);
                } else if (gameArray[i][j] < 0) {
                    Image picture = new Image(getClass().getResource("/pictures/snakefood/" + imageNames.get(Math.abs(gameArray[i][j]) - 1)).toString());
                    pointPicture.setImage(picture);
                    pointPicture.setFitWidth(radius * 2);
                    pointPicture.setFitHeight(radius * 2);
                    gamePane.add(pointPicture, j, i);
                }
            }
        }

        double opacityFactor = 1.00 / (player.getRows().size() * 1.25);
        double opacity = 1.00;
        //Draw Head
        drawHead();
        for (int i = 1; i < player.getRows().size(); i++) {
            drawPart(opacity, player.getRows().get(i), player.getCols().get(i));
            opacity = opacity - opacityFactor;
        }
        root.setCenter(gamePane);

    }

    public BorderPane getRoot() {
        return root;
    }

    private void setUpRoot() {
        root.getScene().setOnKeyPressed((KeyEvent keyEvent) -> {
            KeyCode keyCode = keyEvent.getCode();
            Snake.Direction oldDirection = player.getDirection(), newDirection = oldDirection;
            switch (keyCode) {
                case W:
                    newDirection = Snake.Direction.UP;
                    break;
                case S:
                    newDirection = Snake.Direction.DOWN;
                    break;
                case D:
                    newDirection = Snake.Direction.RIGHT;
                    break;
                case A:
                    newDirection = Snake.Direction.LEFT;
                    break;
                case P:
                    renderer.setNotification(!renderer.isNotification());
                    break;
            }
            switch (newDirection) {
                case LEFT:
                case RIGHT:
                    if (oldDirection == Snake.Direction.LEFT || oldDirection == Snake.Direction.RIGHT) {
                        newDirection = oldDirection;
                    }
                    break;
                case DOWN:
                case UP:
                    if (oldDirection == Snake.Direction.UP || oldDirection == Snake.Direction.DOWN) {
                        newDirection = oldDirection;
                    }
                    break;
            }
            player.setDirection(newDirection);
        });

    }

    public boolean computeNextBoard() {
        int headRow = player.getFirstRow();
        int headCol = player.getFirstCol();
        int id = player.getIdentifier();
        int nextRow = headRow, nextCol = headCol;
        switch (player.getDirection()) {
            case LEFT:
                nextCol = (gameArray[0].length + (nextCol - 1)) % gameArray[0].length;
                break;
            case RIGHT:
                nextCol = (nextCol + 1) % gameArray[0].length;
                break;
            case DOWN:
                nextRow = (nextRow + 1) % gameArray.length;
                break;
            case UP:
                nextRow = (gameArray.length + (nextRow - 1)) % gameArray.length;
                break;
        }
        int tailRow = player.getLastRow();
        int tailCol = player.getLastCol();
        if (gameArray[nextRow][nextCol] == EMPTY) {
            player.deleteLastPart();
            gameArray[tailRow][tailCol] = EMPTY;
        } else if (gameArray[nextRow][nextCol] == id || gameArray[nextRow][nextCol] == BORDER) {
            return false;
        } else {
            player.pointsProperty().setValue( player.pointsProperty().get() + 100);
            addNewPoint();
        }
        player.addNewSnakePart(nextRow, nextCol);
        gameArray[nextRow][nextCol] = id;
        return true;
    }

    public Snake getPlayer() {
        return player;
    }

    public void setPlayer(Snake player) {
        this.player = player;
        for (int i = 0; i < player.getRows().size(); i++) {
            gameArray[player.getRows().get(i)][player.getCols().get(i)] = player.getIdentifier();

        }
    }

    public void addNewPoint() {
        boolean notFound = true;
        int row, col;
        while (notFound) {
            row = rd.nextInt(gameArray.length);
            col = rd.nextInt(gameArray[0].length);
            if (gameArray[row][col] == 0) {
                gameArray[row][col] = -(rd.nextInt(imageNames.size()) + 1);
                notFound = false;
            }
        }
    }

    @FXML
    private void onActionStartgame(ActionEvent event) {
        initialiseBoard();
        setPlayer(createPlayerFromGUI());
        if (chboxBorders.isSelected()) {
            setUpBorder();
        }
        lblPoints.textProperty().bind(Bindings.createStringBinding(() -> Integer.toString(player.pointsProperty().getValue()), player.pointsProperty()));
        player.pointsProperty().set(0);
        root.getRight().setDisable(true);
        startGame();
    }

    private Snake createPlayerFromGUI() {
        Snake snake = new Snake();
        int snakeSize = gameArray.length / 3;
        for (int i = 1; i < snakeSize; i++) {
            snake.addNewSnakePart(1, i);
        }
        snake.setDirection(Snake.Direction.RIGHT);
        snake.setSpeed(chsboxSpeed.getValue());
        snake.setColor(clrPckSnakeColor.getValue());
        return snake;
    }

    private void drawHead() {
        AnchorPane rootHead = new AnchorPane();
        double size = gamePane.getPrefHeight() / gameArray.length;
        rootHead.setPrefSize(size, size);
        Circle circle = new Circle(size / 2);
        circle.setFill(player.getColor());
        rootHead.getChildren().add(circle);
        AnchorPane.setTopAnchor(circle, 0.0);
        AnchorPane.setLeftAnchor(circle, 0.0);
        Circle leftEye = new Circle(size / 8, Color.WHITE);
        Circle rightEye = new Circle(size / 8, Color.WHITE);

        rootHead.getChildren().add(leftEye);
        rootHead.getChildren().add(rightEye);
        switch (player.getDirection()) {
            case UP:
                AnchorPane.setTopAnchor(leftEye, size / 4.0 - size / 8.0);
                AnchorPane.setLeftAnchor(leftEye, size / 4.0 - size / 8.0);
                AnchorPane.setTopAnchor(rightEye, size / 4.0 - size / 8.0);
                AnchorPane.setLeftAnchor(rightEye, size / 4.0 * 3.0 - size / 8.0);
                break;
            case RIGHT:
                AnchorPane.setTopAnchor(leftEye, size / 4.0 * 3.0 - size / 8.0);
                AnchorPane.setLeftAnchor(leftEye, size / 4.0 * 3.0 - size / 8.0);
                AnchorPane.setTopAnchor(rightEye, size / 4.0 - size / 8.0);
                AnchorPane.setLeftAnchor(rightEye, size / 4.0 * 3.0 - size / 8.0);
                break;
            case DOWN:
                AnchorPane.setTopAnchor(leftEye, size / 4.0 * 3.0 - size / 8.0);
                AnchorPane.setLeftAnchor(leftEye, size / 4.0 - size / 8.0);
                AnchorPane.setTopAnchor(rightEye, size / 4.0 * 3.0 - size / 8.0);
                AnchorPane.setLeftAnchor(rightEye, size / 4.0 * 3.0 - size / 8.0);
                break;
            case LEFT:
                AnchorPane.setTopAnchor(leftEye, size / 4.0 - size / 8.0);
                AnchorPane.setLeftAnchor(leftEye, size / 4.0 - size / 8.0);
                AnchorPane.setTopAnchor(rightEye, size / 4.0 * 3.0 - size / 8.0);
                AnchorPane.setLeftAnchor(rightEye, size / 4.0 - size / 8.0);
                break;
        }
        gamePane.add(rootHead, player.getFirstCol(), player.getFirstRow());
    }

    private void drawPart(double opacity, int row, int col) {
        AnchorPane rootHead = new AnchorPane();
        double size = gamePane.getPrefHeight() / gameArray.length;
        Circle part = new Circle(size / 2);
        part.setFill(player.getColor());
        rootHead.getChildren().add(part);
        AnchorPane.setTopAnchor(part, 0.0);
        AnchorPane.setLeftAnchor(part, 0.0);
//        Polygon polygon = new Polygon();
//        polygon.getPoints().addAll(new Double[]{
//            size / 8.0, size / 4.0,
//            size / 4.0, size / 8.0,
//            size / 8.0, 0.0,
//            0.0, size / 8.0,});
//        polygon.setFill(Color.WHITE);
//        rootHead.getChildren().add(polygon);
//        AnchorPane.setLeftAnchor(polygon, size / 4.0 + size / 8.0);
//        AnchorPane.setTopAnchor(polygon, size / 4.0 + size / 8.0);
        rootHead.setOpacity(opacity);
        gamePane.add(rootHead, col, row);
    }

    private void setUpBorder() {
        for (int i = 0; i < gameArray.length; i++) {
            gameArray[0][i] = BORDER;
            gameArray[gameArray.length - 1][i] = BORDER;
        }
        for (int i = 1; i < gameArray.length - 1; i++) {
            gameArray[i][0] = BORDER;
            gameArray[i][gameArray.length - 1] = BORDER;
        }
    }

    private void endGame() {
        gamePane.setOpacity(0.5);
        root.getRight().setDisable(false);
        endGameController.setPlayer(player);
        endGameController.setBordersActivated(gameArray[0][0] == BORDER);
        endGameController.updatePoints();
        endScreen.show();

    }

    private void initialiseBoard() {
        gamePane.setOpacity(1.0);
        gamePane.getChildren().clear();
        for (int[] array : gameArray) {
            Arrays.fill(array, EMPTY);
        }
    }

    private void startGame() {
        player.pointsProperty().set(0);
        setUpRoot();
        addNewPoint();
        drawGame();
        Thread rendererThread;
        renderer = new Timer(getPlayer().getSpeed().getSpeedMillies());

        try {
            renderer.valueProperty().addListener((ref, oldV, newV) -> {
                if (!renderer.isNotification()) {
                    return;
                }
                Snake snakePlayer = getPlayer();
                Speed oldSpeed = snakePlayer.getSpeed();
                if (computeNextBoard()) {
                    drawGame();
                } else {
                    endGame();
                    renderer.setNotification(false);
                }

            });
            rendererThread = new Thread(renderer);
            rendererThread.setDaemon(true);
            rendererThread.start();
        } catch (Exception ex) {
            System.out.println(ex + " " + ex.getMessage());
        }

    }

}
