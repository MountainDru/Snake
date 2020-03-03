/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.bejinariu.snakegame.objects;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;

/**
 *
 * @author Dru
 */
public class Snake {

    private static int identifierCounter = 1;
    private final int identifier;
    private final List<Integer> rows = new ArrayList<>();
    private final List<Integer> cols = new ArrayList<>();
    private Direction direction;
    private Speed speed;
    private Color color;
    private final IntegerProperty points = new SimpleIntegerProperty(0, null);

    public int getPoints() {
        return points.get();
    }

    public void setPoints(int value) {
        points.set(value);
    }

    public IntegerProperty pointsProperty() {
        return points;
    }

    public Snake() {
        this(new ArrayList<>(), new ArrayList<>(), Direction.RIGHT, Speed.Regular, Color.AQUA, 0);
    }

    public Snake(List<Integer> rows, List<Integer> cols, Direction direction, Speed speed, Color color, int points) {
        this.rows.addAll(rows);
        this.cols.addAll(cols);
        this.direction = direction;
        this.speed = speed;
        this.color = color;
        this.points.set(points);
        this.identifier = identifierCounter++;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Speed getSpeed() {
        return speed;
    }

    public void setSpeed(Speed speed) {
        this.speed = speed;
    }

    public int getIdentifier() {
        return identifier;
    }

    public List<Integer> getRows() {
        return new ArrayList<>(rows);
    }

    public List<Integer> getCols() {
        return new ArrayList<>(cols);
    }

    public void addNewSnakePart(int row, int col) {
        rows.add(0, row);
        cols.add(0, col);
    }

    public void deleteLastPart() {
        rows.remove(rows.size() - 1);
        cols.remove(cols.size() - 1);
    }

    public int getFirstRow() {
        return rows.get(0);
    }

    public int getFirstCol() {
        return cols.get(0);
    }

    public int getLastRow() {
        return rows.get(rows.size() - 1);
    }

    public int getLastCol() {
        return cols.get(cols.size() - 1);
    }

   public enum Speed {
        Fast, Regular, Slow;

        public int getSpeedMillies() {
            switch (this) {
                case Fast:
                    return 40;
                case Regular:
                    return 80;
                case Slow:
                    return 100;
            }
            return 100;
        }
    }

    public enum Direction {
        UP, RIGHT, DOWN, LEFT
    }
}
