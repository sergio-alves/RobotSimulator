/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotsimulator.helpers;

import static ch.santosalves.robotsimulator.robots.api.GenericRobot.RobotAction;
import static ch.santosalves.robotsimulator.robots.api.RobotWorldMap.WorldConsts;
import ch.santosalves.robotsimulator.robots.api.RobotPositionAndDirection;

import java.util.ArrayDeque;
import java.util.Deque;



/**
 *
 * @author Sergio
 */
public class MoveHelper {

    private final int pgWidth;
    private final int pgHeight;

    public int[] moves;
    public int[][] playground;

    public RobotPositionAndDirection current;
    public Deque<RobotAction> actions = new ArrayDeque<>();
    
    
    public MoveHelper(RobotPositionAndDirection rpos, int width, int height) {
        pgWidth = width;
        pgHeight = height;
        playground = new int[width][height];
        current = rpos;
    }

    public void playActions(RobotAction[] moves) {
        for (int i = 0; i < moves.length; i++) {
            switch (moves[i]) {
                case INITIALIZE:
                    playground[current.getX()][current.getY()] = WorldConsts.START_POINT.ordinal();
                    break;
                case CONTINUE:
                    if (playground[current.getX()][current.getY()] == WorldConsts.CURRENT.ordinal()) {
                        playground[current.getX()][current.getY()] = WorldConsts.VISITED.ordinal();
                    }                    
                    current.moveFront();
                    playground[current.getX()][current.getY()] = WorldConsts.CURRENT.ordinal();
                    break;
                case TURN_LEFT:
                    current.setDirection(current.getDirection().rotate(-Math.PI / 2));
                    break;
                case TURN_RIGHT:
                    current.setDirection(current.getDirection().rotate(Math.PI / 2));
                    break;
            }
            
            actions.push(moves[i]);
        }
    }

    public void invertActions(int number) {
        RobotAction action;

        //first... change direction
        current.setDirection(current.getDirection().rotate(Math.PI));
        
        for (int i=0; i< number; i++) {
            action = actions.pop();
            
            switch(action){
                case INITIALIZE:
                    playground[current.getX()][current.getY()] = WorldConsts.START_POINT.ordinal();
                    break;
                case CONTINUE:
                    
                    if (playground[current.getX()][current.getY()] == WorldConsts.CURRENT.ordinal()) {
                        playground[current.getX()][current.getY()] = WorldConsts.VISITED.ordinal();
                    }                    
                    current.moveFront();
                    playground[current.getX()][current.getY()] = WorldConsts.CURRENT.ordinal();
                    break;
                case TURN_LEFT:
                    current.setDirection(current.getDirection().rotate(Math.PI / 2));
                    break;
                case TURN_RIGHT:
                    current.setDirection(current.getDirection().rotate(-Math.PI / 2));
                    break;
            }
        }
        
        //reset direction again.
        current.setDirection(current.getDirection().rotate(Math.PI));
    }

    public String toString() {
        String pg = "";

        for (int y = 0; y < pgHeight; y++) {
            pg += "[ ";
            for (int x = 0; x < pgWidth; x++) {
                pg += playground[x][y] + " ";
            }
            pg += "]\r\n";
        }
        
        return pg;
    }
}
