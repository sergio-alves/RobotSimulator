/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotics.events;

/**
 *
 * @author Sergio
 */
public interface RobotEvent {

    public void MovingRelative(int x, int y, String humanRedeable);
    
}
