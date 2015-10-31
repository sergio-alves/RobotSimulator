/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotsimulator.events;

import ch.santosalves.robotsimulator.robots.api.RobotPositionAndDirection;

/**
 *
 * @author Sergio
 */
public interface RobotMoveEvent {
    public void robotLocationChanged(RobotPositionAndDirection current);    
}