/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotics.playground;

import java.awt.Graphics;
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
public class LightSourceTest {
    
    public LightSourceTest() {
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
    public void testCircleSurface () {
        LightSource instance = new LightSource();
        assertEquals("Circle Surface with radius=1 -> 2.PI", new Double(2*Math.PI), new Double(instance.getCircleSurface(1)));
    }
    
    public void testSphereSurface() {
        LightSource instance = new LightSource();
        assertEquals("Circle Surface with radius=1 -> 2.PI", new Double(4*Math.PI), new Double(instance.getSphereSurface(1)));        
    }
}
