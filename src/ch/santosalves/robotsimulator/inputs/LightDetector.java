/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotsimulator.inputs;

import ch.santosalves.robotsimulator.robots.api.RobotPositionAndDirection;
import ch.santosalves.robotsimulator.playground.api.Playground;
import ch.santosalves.robotsimulator.playground.TiledPlayground;

/**
 *
 * @author Sergio
 */
public class LightDetector implements RobotSensorInput{
    Playground Playground;
    
    @Override
    public void setPlayground(Playground playground) {
        this.Playground = playground;
    }    

    @Override
    public double readValue(RobotPositionAndDirection p) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "Light Detector";
    }
}
