/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotics.playground;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 *
 * @author Sergio
 */

@XmlEnum
public enum TileType {
    @XmlEnumValue("NORMAL")
    Normal(0), 
    @XmlEnumValue("VOID")    
    Void(1), 
    @XmlEnumValue("WALL")
    Wall(2);
    
    private int value;

    private TileType(int value) {
        this.value = value;
    }
    
    public int getValue () {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
    
    public static TileType getByValue(int value) {
        switch(value) {
            case 0:
                return Normal;
            case 1:
                return Void;
            case 2:
                return Wall;
        }
        return null;
    }
}
