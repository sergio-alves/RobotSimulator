/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotsimulator.robots.api;

import ch.santosalves.robotsimulator.helpers.CoordinatesTranslator;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 *
 * @author Sergio
 */
public class RobotWorldMap {

    public static Point origin = new Point(0, 0);
    private final WorldConsts[][] world;
    private Point current;
    private final int width;
    private final int height;

    public int getMapWidth() {
        return width;
    }

    public int getMapHeight() {
        return height;
    }

    public RobotWorldMap(int width, int height) {
        world = new WorldConsts[width][height];
        this.height = height;
        this.width = width;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                world[i][j] = WorldConsts.UNKNOWN;
            }
        }
    }

    /**
     * @return the world
     */
    public WorldConsts[][] getWorld() {
        return world;
    }

    /**
     * Updates the world map cell with given value
     *
     * @param p
     * @param val
     */
    public void updateWorldTile(Point p, WorldConsts val) {
        if (world[p.x][p.y] == WorldConsts.UNKNOWN || val == WorldConsts.OBSTACLE) {
            world[p.x][p.y] = val;
        }
    }

    /**
     * Sets the current position of the robot
     *
     * @param p The robot position in the robot coordinate system
     */
    public void setPosition(Point p) {
        /*
         Rules : Nothing can overwrite START_POINT
         START_POINT -> START_POINT
         UNKNOWN -> TO_VISIT | OBSTACLE
         TO_VISIT -> CURRENT
         CURRENT -> VISITED
         */
        if (current == null) {
            //happens only one
            current = CoordinatesTranslator.r2WC(p);
            world[current.x][current.y] = WorldConsts.START_POINT;
        } else {
            if (world[current.x][current.y] == WorldConsts.CURRENT) {
                world[current.x][current.y] = WorldConsts.VISITED;
                current = CoordinatesTranslator.r2WC(p);
                world[current.x][current.y] = WorldConsts.CURRENT;
            } else if (world[current.x][current.y] == WorldConsts.START_POINT) {
                current = CoordinatesTranslator.r2WC(p);
                world[current.x][current.y] = WorldConsts.CURRENT;
            }
        }
    }

    /**
     * Draws the current explored world like a pixel map.
     *
     * @param g the graphics object where we will print the map
     * @param cellSizeInPixels the "pixel" size
     */
    public void drawRobotWorld(Graphics g, int cellSizeInPixels) {
        //Foreach different draws the cell with right color
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                switch (world[i][j]) {
                    case UNKNOWN:
                        g.setColor(Color.BLACK);
                        g.fillRect(i * cellSizeInPixels, j * cellSizeInPixels, cellSizeInPixels, cellSizeInPixels);
                        break;
                    case CURRENT:
                        g.setColor(Color.RED);
                        g.fillRect(i * cellSizeInPixels, j * cellSizeInPixels, cellSizeInPixels, cellSizeInPixels);
                        break;
                    case BOUNDS:
                        g.setColor(Color.BLUE);
                        g.fillRect(i * cellSizeInPixels, j * cellSizeInPixels, cellSizeInPixels, cellSizeInPixels);
                        break;
                    case OBSTACLE:
                        g.setColor(Color.DARK_GRAY);
                        g.fillRect(i * cellSizeInPixels, j * cellSizeInPixels, cellSizeInPixels, cellSizeInPixels);
                        break;
                    case START_POINT:
                        g.setColor(Color.GREEN);
                        g.fillRect(i * cellSizeInPixels, j * cellSizeInPixels, cellSizeInPixels, cellSizeInPixels);
                        break;
                    case TO_VISIT:
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillRect(i * cellSizeInPixels, j * cellSizeInPixels, cellSizeInPixels, cellSizeInPixels);
                        break;
                    case VISITED:
                        g.setColor(Color.YELLOW);
                        g.fillRect(i * cellSizeInPixels, j * cellSizeInPixels, cellSizeInPixels, cellSizeInPixels);
                        break;
                }

            }
        }
    }

    public static enum WorldConsts {

        TO_VISIT(0), START_POINT(1), VISITED(2), BOUNDS(3), OBSTACLE(4), CURRENT(5), UNKNOWN(6);

        //4
        private final int value;

        private WorldConsts(int v) {
            value = v;
        }

        /**
         * @return the value
         */
        public int getValue() {
            return value;
        }

    }

}
