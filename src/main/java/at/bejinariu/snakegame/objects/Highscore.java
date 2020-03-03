/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.bejinariu.snakegame.objects;

import at.bejinariu.snakegame.objects.Snake.Speed;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Dru
 */
public class Highscore {

    public static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss");

    private final IntegerProperty playerScore = new SimpleIntegerProperty(0, null);
    private final StringProperty playerName = new SimpleStringProperty(null, null);
    private final ObjectProperty<LocalDateTime> date = new SimpleObjectProperty<>(null, null);
    private final BooleanProperty bordersEnabled = new SimpleBooleanProperty(false, null);
    private final ObjectProperty<Snake.Speed> playerSpeed = new SimpleObjectProperty<>(Speed.Regular, null);

    public Snake.Speed getPlayerSpeed() {
        return playerSpeed.get();
    }

    public void setPlayerSpeed(Snake.Speed value) {
        playerSpeed.set(value);
    }

    public ObjectProperty playerSpeedProperty() {
        return playerSpeed;
    }

    public IntegerProperty playerScoreProperty() {
        return playerScore;
    }

    public int getPlayerScore() {
        return playerScore.get();
    }

    public void setPlayerScore(int value) {
        playerScore.set(value);
    }

    public String getPlayerName() {
        return playerName.get();
    }

    public void setPlayerName(String value) {
        playerName.set(value);
    }

    public StringProperty playerNameProperty() {
        return playerName;
    }

    public LocalDateTime getDate() {
        return date.get();
    }

    public void setDate(LocalDateTime value) {
        date.set(value);
    }

    public ObjectProperty dateProperty() {
        return date;
    }

    public boolean isBordersEnabled() {
        return bordersEnabled.get();
    }

    public void setBordersEnabled(boolean value) {
        bordersEnabled.set(value);
    }

    public BooleanProperty bordersEnabledProperty() {
        return bordersEnabled;
    }

    public Highscore(int playerScore, String playerName, LocalDateTime date, boolean bordersEnabled, Snake.Speed playerSpeed) {
        this.bordersEnabled.set(bordersEnabled);
        this.date.set(date);
        this.playerName.set(playerName);
        this.playerScore.set(playerScore);
        this.playerSpeed.set(playerSpeed);
    }

    public String toCSVLine() {
        StringBuilder line = new StringBuilder();
        line.append(playerName.get());
        line.append(";");
        line.append(playerScore.get());
        line.append(";");
        line.append(date.get().format(dateFormat));
        line.append(";");
        line.append(playerSpeed.get());
        line.append(";");
        line.append(bordersEnabled.get());
        return line.toString();
    }

    public static Highscore fromCSVLine(String line) {
        String parts[] = line.split(";");
        return new Highscore(Integer.parseInt(parts[1]),
                parts[0],
                LocalDateTime.parse(parts[2], dateFormat),
                Boolean.valueOf(parts[4]),
                Snake.Speed.valueOf(parts[3]));
    }

}
