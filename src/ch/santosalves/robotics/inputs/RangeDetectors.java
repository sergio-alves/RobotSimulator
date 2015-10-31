/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotics.inputs;

import ch.santosalves.robotics.robots.api.RobotPositionAndDirection;
import java.awt.Point;

/**
 * Common interface for range sensors
 * 
 * @author Sergio
 */
public interface RangeDetectors {

    /**
     * Gets the range (in tiles) for the current sensor
     * @return the range value
     */
    double getRange();
    
    /**
     * asks the detector to give the position of the obstacle
     * @param p The current position point
     * @return null if no obstacle in range or the obstacle position
     */
    Point getObstaclePosition(RobotPositionAndDirection p);
    
    /**
     * The point in front of current position
     * 
     * @param p the front point
     * 
     * @return true if free false otherwise
     */
    boolean isFreeAhead(RobotPositionAndDirection p);
}
