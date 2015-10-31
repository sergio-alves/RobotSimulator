/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotsimulator.playground.api;

import ch.santosalves.robotsimulator.playground.Layers;

/**
 *
 * @author Sergio
 */
public interface LayaredPlayground {
    /**
     * 
     * @param layer
     * @return 
     */
    Object getLayer(Layers layer);    

    /**
     *
     * @param layer
     * @param content 
     */
    void setLayer(Layers layer, Object content);

    void initializeLayers();
}
