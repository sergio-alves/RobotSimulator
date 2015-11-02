/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotics.inputs;

import ch.santosalves.robotics.robots.api.RobotPositionAndDirection;
import ch.santosalves.robotics.playground.api.Playground;
import java.util.logging.Logger;

/**
 * Common interface for robot sensor input
 * 
 * @author Sergio
 */
public interface RobotSensorInput {
    public static Logger log = Logger.getLogger(RobotSensorInput.class.getName());
    
    /**
     * Sets the playground 
     * 
     * @param playground the simulation playground
     */
    void setPlayground(Playground playground);
    
    /**
     * Reads the value of the sensor. Because those values can be quite different
     * from one sensor to another the return type is a double.
     * 
     * @param p The current robot position
     * 
     * @return the sensor value
     */    
    double readValue(RobotPositionAndDirection p);

    
    @Override
    public String toString();
}
