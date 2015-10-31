/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotics;

import ch.santosalves.robotics.helpers.Vector2D;
import ch.santosalves.robotics.helpers.MoveHelper;
import ch.santosalves.robotics.robots.api.GenericRobot;
import ch.santosalves.robotics.robots.api.GenericRobot.RobotAction;
import ch.santosalves.robotics.robots.api.RobotPositionAndDirection;
import ch.santosalves.robotics.robots.api.RobotWorldMap;
import ch.santosalves.robotics.robots.api.RobotWorldMap.WorldConsts;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
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
public class MoveHelperTest {
    List<RobotAction> actions = new ArrayList<>();        
        
        
    public MoveHelperTest() {
            
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
actions.add(RobotAction.INITIALIZE);
        actions.add(RobotAction.CONTINUE);
        actions.add(RobotAction.CONTINUE);
        actions.add(RobotAction.TURN_LEFT);
        actions.add(RobotAction.CONTINUE);
        actions.add(RobotAction.CONTINUE);
        actions.add(RobotAction.CONTINUE);
        actions.add(RobotAction.TURN_RIGHT);
        actions.add(RobotAction.CONTINUE);
        actions.add(RobotAction.TURN_RIGHT);
        actions.add(RobotAction.CONTINUE);
        actions.add(RobotAction.CONTINUE);
        actions.add(RobotAction.CONTINUE);
        actions.add(RobotAction.CONTINUE);
        actions.add(RobotAction.TURN_RIGHT);
        actions.add(RobotAction.CONTINUE);
        actions.add(RobotAction.CONTINUE);
        actions.add(RobotAction.CONTINUE);        
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    @Test
    public void testPathSetting() throws Exception {
        MoveHelper mh = new MoveHelper(new RobotPositionAndDirection(new Point(1,1), new Vector2D(0, 1)), 5, 5);                
        mh.playActions(actions.toArray(new RobotAction[actions.size()]));
        
        assertEquals(mh.playground[1][1], WorldConsts.START_POINT.ordinal());
        assertEquals(mh.playground[1][2], WorldConsts.VISITED.ordinal());
        assertEquals(mh.playground[1][3], WorldConsts.VISITED.ordinal());        
        assertEquals(mh.playground[0][1], WorldConsts.CURRENT.ordinal());
    
        System.out.println(mh);
    }
    
    @Test
    public void testRolback() {
        MoveHelper mh = new MoveHelper(new RobotPositionAndDirection(new Point(1,1), new Vector2D(0, 1)), 5, 5);                
        mh.playActions(actions.toArray(new RobotAction[actions.size()]));        
        
        mh.invertActions(1);
        
        assertEquals(mh.playground[1][1], WorldConsts.START_POINT.ordinal());
        assertEquals(mh.playground[1][2], WorldConsts.VISITED.ordinal());
        assertEquals(mh.playground[1][3], WorldConsts.VISITED.ordinal());        
        assertEquals(mh.playground[0][2], WorldConsts.CURRENT.ordinal());   
        
        System.out.println(mh);        
    }
    
    @Test
    public void testRolbackOfMultipleMoves() {
        MoveHelper mh = new MoveHelper(new RobotPositionAndDirection(new Point(1,1), new Vector2D(0, 1)), 5, 5);                
        mh.playActions(actions.toArray(new GenericRobot.RobotAction[actions.size()]));        
        
        mh.invertActions(10);
        
        assertEquals(mh.playground[1][1], WorldConsts.START_POINT.ordinal());
        assertEquals(mh.playground[1][2], WorldConsts.VISITED.ordinal());
        assertEquals(mh.playground[1][3], WorldConsts.VISITED.ordinal());        
        assertEquals(mh.playground[4][3], RobotWorldMap.WorldConsts.CURRENT.ordinal());   
        
        System.out.println(mh);        
    }
    
}
