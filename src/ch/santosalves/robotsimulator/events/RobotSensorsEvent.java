/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotsimulator.events;

/**
 *
 * @author Sergio
 */
public interface RobotSensorsEvent {
    public void obstaclesDetected();
    public void voidDetected();
    public void wallDetected();
}
