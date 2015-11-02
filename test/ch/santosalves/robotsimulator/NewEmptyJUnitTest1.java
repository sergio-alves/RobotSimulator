/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotsimulator;

import ch.santosalves.robotsimulator.helpers.Vector2D;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sergio
 */
public class NewEmptyJUnitTest1 {
    
    public NewEmptyJUnitTest1() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void testAngles () {
        /**
         *  --------------------> x
         *  |
         *  |
         *  |
         *  v
         *  y
         */
        Vector2D right = new Vector2D(1, 0); //direction des x horizontal
        Vector2D down = new Vector2D(0, 1); //direction des y horizontal 
        Vector2D up = new Vector2D(0, -1); //direction des y horizontal 
        Vector2D left = new Vector2D(-1, 0); //direction des y horizontal 
        
        // v1 perpendiculaire à v2        
        assertEquals("Angle is pi/2 =  90°", Math.PI/2, right.getAngleBetweenVectors(down), 0.0000001);
        assertEquals("Angle is pi/2 =  90°", -Math.PI/2, right.getAngleBetweenVectors(up), 0.0000001);
        assertEquals("Angle is pi/2 =  90°", -Math.PI/2, down.getAngleBetweenVectors(right), 0.0000001);
        assertEquals("Angle is pi/2 =  90°", Math.PI/2, down.getAngleBetweenVectors(left), 0.0000001);
        
    }
}
