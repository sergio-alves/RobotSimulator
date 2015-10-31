/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotics.playground.api;

import java.awt.Point;

/**
 *
 * @author Sergio
 */
public interface LightSource {

    double getEmissionAngle();

    int getLumens();

    double getLux(Point p);

    /**
     * @return the spotAngle
     */
    double getSpotAngle();

    /**
     * @return the type
     */
    LightType getType();

    //high of the lamp
    int getZ();

    void setEmissionAngle(double angle);

    void setLumens(int lumens);

    /**
     * @param spotAngle the spotAngle to set
     */
    void setSpotAngle(double spotAngle);

    /**
     * @param type the type to set
     */
    void setType(LightType type);

    void setZ(int z);
    
}
