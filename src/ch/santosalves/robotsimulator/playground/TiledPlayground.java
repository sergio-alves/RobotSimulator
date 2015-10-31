/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotsimulator.playground;

import ch.santosalves.robotsimulator.playground.api.MultiLayeredPlayground;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergio
 */
public class TiledPlayground implements MultiLayeredPlayground {    
    private int tileSize = 32;    
    private int playgroundWidth = 10;
    private int playgroundHeight = 10;
        
    private final Map<Layers, Object> layers = new HashMap<>();    
    private List<ch.santosalves.robotsimulator.playground.api.LightSource> ligths = new ArrayList<>();
      private boolean showPopupMenu = false;
    private boolean allowEdition = false;  
 
    public TiledPlayground() { 
        initializeLayers();
    }
        
    /**
     *
     */
    public final void initializeLayers() {
        layers.put(Layers.GROUND, new int[playgroundWidth][playgroundHeight]);
        layers.put(Layers.LIGHTS, new double[playgroundWidth][playgroundHeight]);   
        
        //Ground
        for (int x = 0; x < getPlaygroundWidth(); x++) {
            for (int y = 0; y < getPlaygroundHeight(); y++) {
                ((int[][]) layers.get(Layers.GROUND))[x][y] = TileType.Normal.getValue();
            }
        }

        //Lux
        for (int x = 0; x < getPlaygroundWidth(); x++) {
            for (int y = 0; y < getPlaygroundHeight(); y++) {
                ((double[][]) layers.get(Layers.LIGHTS))[x][y] = 0.0;
            }
        }
        
    }
    
    public TiledPlayground(int seed) {
        layers.put(Layers.GROUND, new int[playgroundWidth][playgroundHeight]);
        layers.put(Layers.LIGHTS, new double[playgroundWidth][playgroundHeight]);        
        initializeLayers();
        
        generateGround(seed);        
    }
    
    /**
     * @return the tileSize
     */
    @Override
    public int getTileSize() {
        return tileSize;
    }
    
    /**
     * Generates the ground
     * @param seed 
     */
    public final void generateGround(int seed) {
        int[][] g = {
            {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
            {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
            {2,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,2},
            {2,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,2},
            {2,0,0,0,1,1,0,0,0,1,1,0,0,0,0,0,2},
	    {2,0,0,0,1,1,0,0,1,1,1,1,1,1,0,0,2},
	    {2,0,0,0,0,1,1,1,1,0,0,1,0,0,0,0,2},
	    {2,0,0,0,0,0,1,1,0,0,0,1,0,0,0,0,2},
	    {2,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,2},
	    {2,0,0,1,0,0,0,0,0,1,1,1,0,1,0,0,2},
	    {2,0,0,1,1,1,1,1,1,1,1,1,1,1,0,0,2},
	    {2,0,0,0,0,1,1,0,0,0,1,1,0,0,0,0,2},
	    {2,0,0,0,0,1,1,0,0,0,1,1,1,0,0,0,2},
	    {2,0,0,0,1,1,1,0,0,0,1,1,0,0,0,0,2},
	    {2,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,2},
	    {2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2},
            {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2}
        };
        
        setGround(g);
    }    
    
    @Override
    public String toString() {
        int[][] g = getGround();
        String ret = "Playground:\r\n";
        
        for(int i=0; i<playgroundWidth;i++) {
            for(int j=0; j<playgroundWidth; j++) {
                ret += g[j][i];
            }
            ret += "\r\n";
        }
        
        return  ret;
    }

    @Override
    public int getPlaygroundHeight() {
        return playgroundHeight;
    }

    @Override
    public int getPlaygroundWidth() {
        return playgroundWidth;
    }

    @Override
    public void setPlaygroundHeight(int playgroundHeight) {
        this.playgroundHeight = playgroundHeight;
    }

    @Override
    public void setPlaygroundWidth(int playgroundWidth) {
        this.playgroundWidth = playgroundWidth;
    }

    @Override
    public void setTileSize(int tileSize) {
        this.tileSize=tileSize;
    }

    @Override
    public void setGround(int[][] ground) {
        this.layers.put(Layers.GROUND, ground);
    }

    @Override
    public int[][] getGround() {
        return (int[][])this.layers.get(Layers.GROUND);
    }

    @Override
    public Object getLayer(Layers layer) {
        return layers.get(layer);
    }

    @Override
    public void setLayer(Layers layer, Object content) {
        layers.put(layer, content);
    }

    @Override
    public void addLightSource(ch.santosalves.robotsimulator.playground.api.LightSource ls) {
        ligths.add(ls);
    }

    @Override
    public void removeLightSource(ch.santosalves.robotsimulator.playground.api.LightSource ls) {
        ligths.remove(ls);
    }

    @Override
    public boolean isAllowEdition() {
        return allowEdition;
    }

    @Override
    public void setAllowEdition(boolean allowEdition) {
        this.allowEdition=allowEdition;
    }
}
