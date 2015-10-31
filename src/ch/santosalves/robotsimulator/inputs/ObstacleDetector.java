/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotsimulator.inputs;

import ch.santosalves.robotsimulator.robots.api.RobotPositionAndDirection;
import ch.santosalves.robotsimulator.playground.api.Playground;
import ch.santosalves.robotsimulator.playground.TileType;
import java.awt.Point;

/**
 * Commonly called subsonic sensor this is one kind of obstacle detector like 
 * bumpers or more sophisticated laser sensors
 * 
 * @author Sergio
 */
public class ObstacleDetector  implements RobotSensorInput , RangeDetectors {
    private Playground playground;
    private double range = 10.0;
            
    
    /**
     * Reads the value of the sensor. Because those values can be quite different
     * from one sensor to another the return type is a double.
     * This sensor returns a value from 0 - range*tileSize mm or a négative value if no
     * obstacle detected
     * 
     * @param p The current robot position
     * 
     * @return the sensor value (1.0 if no void 0.0 if void)
     */          
    @Override
    public double readValue(RobotPositionAndDirection p){        
        double distance = Double.NEGATIVE_INFINITY;
        
        RobotPositionAndDirection rpad = p.whatIsNextStep();
        
        for(int i=0; i<getRange();i++) {
            if(!(rpad.getX() < 0 || rpad.getX() > playground.getPlaygroundWidth() || 
                 rpad.getY() < 0 || rpad.getY() > playground.getPlaygroundWidth())){
                
                if(playground.getGround()[rpad.getX()][rpad.getY()] != TileType.Wall.getValue()){
                    rpad = rpad.whatIsNextStep();
                }else{
                    distance = ++i;            
                    break;
                }                
            }else{
                //outside bounds. and no obstacle found 
                break;
            }
        } 
        
        return distance;
    }    

    /**
     * @return the range
     */
    @Override
    public double getRange() {
        return range;
    }

    /**
     * @param range the range to set
     */
    public void setRange(double range) {
        this.range = range;
    }    
        
    /**
     * @param playground the playground to set
     */    
    @Override
    public void setPlayground(Playground playground) {
        this.playground = playground;
    }
    
    /**
     * Returns the position of the next obstacle detected by the sensor
     * @param p the next point if no direction change is made to the robot
     * 
     * @return null if no obstacle detected or a point with coordinates
     */
    @Override
    public Point getObstaclePosition(RobotPositionAndDirection p ) {
        RobotPositionAndDirection rpad = new RobotPositionAndDirection(p);
        double distance = readValue(p);
               
        if(distance < 0.0) {
            return null;
        } else {
            for (int i=0; i < (int)distance;i++) {
                rpad = rpad.whatIsNextStep();
            }
        }
        
        return rpad.getPosition();
    }

    /**
     * Check if the robot can move to the front relative to the robot
     * 
     * @param p the robot position
     * @return true if case is normal or false if void or wall
     */
    @Override
    public boolean isFreeAhead(RobotPositionAndDirection p) {
        double distance = readValue(p);
        return (distance < 0 || distance > 1);
    }    
    
        @Override
    public String toString(){
        return "Obstacle Detector";
    }    
}