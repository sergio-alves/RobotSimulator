/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotsimulator.playground.api;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Sergio
 */
public interface Playground{
    
    /**
     * 
     * @return 
     */
    int[][] getGround();
       
    /**
     * 
     * @param ground 
     */
    void setGround(int[][] ground);
  

    /**
     * @return the playgroundHeight
     */
    int getPlaygroundHeight();
    
    /**
     * @param playgroundHeight the playgroundHeight to set
     */
    void setPlaygroundHeight(int playgroundHeight);

    /**
     * @return the playgroundWidth
     */
    int getPlaygroundWidth();

    /**
     * @param playgroundWidth the playgroundWidth to set
     */
    void setPlaygroundWidth(int playgroundWidth);
}
