/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotics.inputs;

import ch.santosalves.robotics.robots.api.RobotPositionAndDirection;
import ch.santosalves.robotics.playground.api.Playground;
import ch.santosalves.robotics.playground.TileType;
import java.awt.Point;


/**
 *
 * @author Sergio
 */
public class VoidDetector implements RobotSensorInput, RangeDetectors{
    private Playground playground;
    private final double range = 1.0;
   
    /**
     * Reads the value of the sensor. Because those values can be quite different
     * from one sensor to another the return type is a double.
     * 
     * @param p The current robot position
     * 
     * @return the sensor value (1.0 if no void 0.0 if void)
     */      
    @Override
    public double readValue(RobotPositionAndDirection p){
        RobotPositionAndDirection next = p.whatIsNextStep();
        return (playground.getGround()[next.getPosition().x][next.getPosition().y] == TileType.Void.getValue()) ? 0.0 : 1.0;
    }

    /**
     * Gets the detector range in tiles
     * 
     * @return 
     */
    @Override
    public double getRange() {
        return range;
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
     * 
     * @param p the robot current position
     * 
     * @return null if no obstacle detected or a point with coordinates
     */
    @Override    
    public Point getObstaclePosition(RobotPositionAndDirection p){
        if(readValue(p) == 0.0 )
            return p.whatIsNextStep().getPosition();
        else
            return null;
    }

    /**
     * Using captor readValue determine if the next position can be explored or not
     * 
     * @param p the robot current position  position
     * 
     * @return 
     */
    @Override
    public boolean isFreeAhead(RobotPositionAndDirection p) {
        return readValue(p) == 1.0;
    }
    
        @Override
    public String toString(){
        return "Void Detector";
    }
}
