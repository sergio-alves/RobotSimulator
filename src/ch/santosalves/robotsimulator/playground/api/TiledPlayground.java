/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotsimulator.playground.api;

/**
 *
 * @author Sergio
 */
public interface TiledPlayground extends Playground{
        /**
     * @return the tileSize
     */
    int getTileSize();

    /**
     * @param tileSize the tileSize to set
     */
    void setTileSize(int tileSize);
        
}
