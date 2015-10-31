/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotsimulator.playground.api;

import ch.santosalves.robotsimulator.playground.EditablePlayground;

/**
 *
 * @author Sergio
 */
public interface MultiLayeredPlayground extends TiledPlayground, LayaredPlayground, LightsLayer, EditablePlayground{
    
}
