/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotics.helpers;


import ch.santosalves.robotics.playground.TiledPlayground;
import static ch.santosalves.robotics.robots.api.RobotWorldMap.origin;
import java.awt.Point;

/**
 *
 * @author Sergio
 */
public class CoordinatesTranslator {
/**
     * Translate robot to world coordinates. Robot origin = (0,0) and world 
     * origin = (25,25);
     * 
     * @param p the robot position
     * 
     * @return the position in the world
     */
    public static Point r2WC(Point p) {
        return new Point(p.x + origin.x, p.y + origin.y);
    }

    /**
     * Returns a world point in robot coordinate system
     * 
     * @param p the world point
     * 
     * @return robot point
     */
    public static Point w2RC(Point p) {
        return new Point(p.x - origin.x, p.y - origin.y);
    }
    
    
    /**
     * translates a tile coordinate to pixels model
     * 
     * @param v
     * 
     * @return 
     */
//    public static int t2p(int v) {
//        return v * TiledPlayground.tileSize;
  //  } 
    
    /**
     * Translate a coordinate from the image (in pixels) into the tile model.
     * => for a v = 64 pixels and a tile width of 32 the tile value = 64/32=2
     * 
     * @param v
     * @return 
     */
    //public static int p2t(int v) {
    //    return v/TiledPlayground.tileSize;
   // }
    
    /**
     * Converts from Robot coordinate system to the playground coordinate system
     * 
     * @param p the position in robot coordinate system
     * 
     * @return a position in the playground coordinate system
     */
    public static Point r2PCoordinates(Point p) {
        //return new Point(p.x * TiledPlayground.tileSize + robotStartPoint.x, p.y * playground.getTileSize() + robotStartPoint.y);
        return null;
    }
    
    /**
     * Converts a playground coordinate to robot coordinate system
     * @param p the position in the playground
     * @return a position in robot playground 
     */
    public Point p2RCoordinates(Point p) {
        //return new Point((p.x - p.x % WIDTH) / WIDTH - robotStartPoint.x, (p.y - p.y % HEIGHT) / HEIGHT - robotStartPoint.y);
        return null;
    }
        
}
