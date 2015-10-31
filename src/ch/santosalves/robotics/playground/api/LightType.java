/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotics.playground.api;

/**
 * 
 * @author Sergio
 */
public enum LightType  {
    Omni(0),
    Directional(1),
    Spot(2);

    private int value;
    
    private LightType(int value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(int value) {
        this.value = value;
    }
    
    
    
}
