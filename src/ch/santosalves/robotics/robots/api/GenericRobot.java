/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotics.robots.api;

import ch.santosalves.robotics.helpers.Vector2D;
import ch.santosalves.robotics.inputs.RobotSensorInput;
import ch.santosalves.robotics.events.RobotEvent;
import ch.santosalves.robotics.playground.api.MultiLayeredPlayground;
import ch.santosalves.robotics.playground.api.Playground;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import javax.swing.JComponent;
import static ch.santosalves.robotics.robots.api.RobotWorldMap.WorldConsts;
import static ch.santosalves.robotics.robots.api.GenericRobot.RobotAction.*;

/**
 *
 * @author Sergio
 */
public abstract class GenericRobot extends JComponent {
    private RobotWorldMap map;
    private JComponent worldPrintingSupport;
    private boolean scanned = false;
    
    //The class logger
    protected static final Logger log = Logger.getLogger(GenericRobot.class.getName());
    
    //A random numbers generator
    protected final Random rnd = new Random(Calendar.getInstance().getTimeInMillis());

    //A listener to get the last changes from robot
    protected final List<RobotEvent> movingListeners = new ArrayList<>();
    
    private Deque<RobotAction> actions = new ArrayDeque<>();
    private Deque<RobotAction> rollbackActions = new ArrayDeque<>();
    
    //Queue is filled by a producer and emptied by a consumer thread
    private final Deque<RobotAction> moves = new LinkedList<>();
    
    //keep a track of the angle (z axis) of the robot
    private double angle = 0.0;
        
    //The real playground where the robot is evoluating
    protected MultiLayeredPlayground playground;    
    
    // All inputs related to the robot
    private List<RobotSensorInput> inputs = new ArrayList<>();

    private Thread robotExploring;
    private Thread robotExecuting;
    private Thread robotThread;
            
    //enter or exit the rollback mode
    private boolean rollback = false;
        
    /**
     * gives the robot absolute position and direction with origin (0, 0) 
     * We assume that the topleft most point corresponds to the origin and x 
     * axis is horizontal and positive values to the right and y axis vertical 
     * and positive values below
     * 
     *  0----(x)
     *  |
     *  ˇ
     * (y)
     * 
     * /!\ position is different with location because location uses the form 
     * coordinate system while position uses the robot coordinate system
     * 
     */
    private RobotPositionAndDirection position;

    public GenericRobot() {
        super();
    }

    public GenericRobot(String name) {
        super();
        this.setName(name);
        revalidate();
    }

    public GenericRobot(String name, MultiLayeredPlayground playground, RobotPositionAndDirection pos) {
        super();
        this.setName(name);
        this.playground = playground;
        this.position = pos;
        //updateRobotUI();
    }

    public void updateRobot() {
        //Dimension d = new Dimension(playground.getTileSize(), playground.getTileSize());
        //setMaximumSize(d);
        //setMinimumSize(d);
        //setPreferredSize(d);
        //setSize(d.width, d.height);
        
        setLocation(
            position.getX() * playground.getTileSize(), 
            position.getY() * playground.getTileSize()
        );
        //
    //    repaint();
      //  revalidate();
    }
    
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x - x % 32, y - y % 32, width, height); 
        
        if(playground != null) {
            super.setBounds(x - x % playground.getTileSize(), y - y % playground.getTileSize(), width, height); 
            this.setRobotPosition(new Point(x/playground.getTileSize(), y/playground.getTileSize()));
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        
        BufferedImage robot = new BufferedImage(playground.getTileSize(), playground.getTileSize(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D grobot = (Graphics2D) robot.getGraphics();
        grobot.setBackground(Color.WHITE);
        grobot.setColor(Color.black);

        int ts = playground.getTileSize();
        int ss = 1 + (int)(ts / 16);
        int hss = 1 + (int)(ss / 2);
        grobot.setStroke(new BasicStroke(ss));
                
        grobot.drawOval(2*hss, 2* hss, ts-4*hss, ts-4*ss);
        grobot.drawLine(hss, 0, hss, ts);
        grobot.drawLine(ts-hss, 0, ts-hss, ts);
        
        grobot.setColor(Color.red);
        grobot.drawLine(2 * ss, ts - hss, 2 * ss + (int)(ts/4), ts-hss);
        
        grobot.drawLine(ts - 2*ss - (int)(ts/4), ts - hss, ts - 2*ss, ts-hss);    
        
        
        
        
        
        BufferedImage newImg = new BufferedImage(playground.getTileSize(), playground.getTileSize(), BufferedImage.TYPE_4BYTE_ABGR);        
        Graphics2D gr = (Graphics2D)newImg.getGraphics();
        
        //do rotation
        gr.rotate(angle, newImg.getWidth() / 2, newImg.getHeight() / 2);
        gr.drawImage(robot, 0, 0, robot.getWidth(), robot.getHeight(), null);
        
        //gr.scale(robot.getWidth()/playground.getTileSize(), robot.getHeight()/playground.getTileSize());
                        
        
        if(robot != null) {
            g.setXORMode(Color.white);
            g.drawImage(newImg, 0, 0, null);
        }
    }

    public void addIdiotMovingListener(RobotEvent listener) {
        movingListeners.add(listener);
    }
    
    public void removeIdiotMovingListener(RobotEvent listener) {
        movingListeners.remove(listener);
    }
    
    public void turnLeft() {
        getMoves().addLast(RobotAction.TURN_LEFT);
    }

    public void turnRigth() {
        getMoves().addLast(RobotAction.TURN_RIGHT);
    }
    
    public void moveFront() {
        getMoves().addLast(RobotAction.CONTINUE);
    }
    
    /**
     * returns the current robot position respective to the robot origin (0,0)
     * 
     * @return the current position
     */
    public Point getRobotPosition() {
        return getPosition().getPosition();
    }
    
    public void setRobotPosition(Point p) {
        getPosition().setPosition(p);
    }
    
    public Vector2D getDirection() {
        return getPosition().getDirection();
    }

    public void setDirection(Vector2D d) {
        getPosition().setDirection(d);
    }
    /*
    protected void startExploring(Runnable exploringAlgorithm) {
        //The start point of the robot in the playground coordinates
        robotExploring = new Thread(exploringAlgorithm);        
        robotExploring.setName("Exploring Robot");
        robotExploring.start();
    }
    
    protected void startExecuting(Runnable executingCommands) {
        robotExecuting = new Thread(executingCommands);
        robotExecuting.setName("Executing Robot");
        robotExecuting.start();
    }
    
    */
    protected void start(Runnable algorithm) {
        setMap(new RobotWorldMap(playground.getPlaygroundWidth(), playground.getPlaygroundHeight()));

        //Initialize all the inputs
        getInputs().stream().forEach((input) -> {
            input.setPlayground(playground);
        });

        //Set the initialization command to be executed by the robot
        getMoves().push(INITIALIZE);
        
        robotThread = new Thread(algorithm);
        robotThread.setName("Robot Thread");
        robotThread.start();
    }
    
    public abstract void start();

    
    protected void stopExploring() {
        robotExploring.interrupt();
    }
    
    //Some commands use more than a loop cycle 
    private int command_execution_index = 0;
            
    public RobotAction executeNext() throws InterruptedException {
        //RobotAction action = moves.pollFirst();
        RobotAction action = getMoves().peekLast();
        
        if(action == null)
            return null;
 
        switch(action) {
            case INITIALIZE:
                if(isRollback()) 
                    getRollbackActions().push(getMoves().pollLast());
                else
                    getActions().push(getMoves().pollLast());
                break;               
            case BACK:
                getPosition().moveBack();
                setLocation(getPosition().getX() * playground.getTileSize(), getPosition().getY()* playground.getTileSize());
                
                movingListeners.stream().forEach((event) -> {
                    event.MovingRelative(this.getDirection().getA1(), this.getDirection().getA2(), "Moving to (x,y)->("+this.getDirection().getA1()+","+this.getDirection().getA2()+")");
                });                
                if(isRollback()) 
                    getRollbackActions().push(getMoves().pollLast());
                else
                    getActions().push(getMoves().pollLast());
                break;
            case CONTINUE:
                getPosition().moveFront();
                //setLocation(getLocation().x + this.getDirection().getA1() * playground.getTileSize(), getLocation().y + this.getDirection().getA2()* playground.getTileSize()); 
                setLocation(getPosition().getX() * playground.getTileSize(), getPosition().getY()* playground.getTileSize());
                
                movingListeners.stream().forEach((event) -> {
                    event.MovingRelative(this.getDirection().getA1(), this.getDirection().getA2(), "Moving to (x,y)->("+this.getDirection().getA1()+","+this.getDirection().getA2()+")");
                });

                if(isRollback()) 
                    getRollbackActions().push(getMoves().pollLast());
                else
                    getActions().push(getMoves().pollLast());
                break;
                
            case TURN_LEFT:
                getPosition().setDirection(getPosition().getDirection().rotate(-Math.PI/2));
                this.angle -= Math.PI/2;
                repaint();
                if(isRollback()) 
                    getRollbackActions().push(getMoves().pollLast());
                else
                    getActions().push(getMoves().pollLast());
                break;
                
            case TURN_RIGHT:
                getPosition().setDirection(getPosition().getDirection().rotate(Math.PI/2));
                this.angle += Math.PI/2;              
                repaint();
                
                if(isRollback()) 
                    getRollbackActions().push(getMoves().pollLast());
                else
                    getActions().push(getMoves().pollLast());
                break;
        
            case SCAN:
                getPosition().setDirection(getPosition().getDirection().rotate(-Math.PI/2));
                this.angle -= Math.PI/2;                        
                command_execution_index++;
                repaint();
                if(command_execution_index == 4) {
                    command_execution_index = 0;
                    /*actions.push(*/getMoves().pollLast()/*)*/;
                }                
            break;
        }
        
        //Update actions list (usefull if robot is stucked and needs to rollback
        //to a previous position.

        return action;
    }

    /**
     * @param playground the playground to set
     */
    public void setPlayground(MultiLayeredPlayground playground) {
        this.playground = playground;
        //updateRobotUI();
    }
    
    /**
     * 
     * @return playground
     */
    public Playground getPlayground() {
        return this.playground;
    }

    /**
     * @return the inputs
     */
    public List<RobotSensorInput> getInputs() {
        return inputs;
    }

    /**
     * @return the moves
     */
    public Deque<RobotAction> getMoves() {
        return moves;
    }

    /**
     * @param inputs the inputs to set
     */
    public void setInputs(List<RobotSensorInput> inputs) {
        this.inputs = inputs;
    }

    /**
     * @return the rollback
     */
    public boolean isRollback() {
        return rollback;
    }

    /**
     * @param rollback the rollback to set
     */
    public void setRollback(boolean rollback) {
        this.rollback = rollback;
    }

    /**
     * @return the position
     */
    public RobotPositionAndDirection getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(RobotPositionAndDirection position) {
        this.position = position;
        //updateRobotUI();
    }

    /**
     * @return the actions
     */
    public Deque<RobotAction> getActions() {
        return actions;
    }

    /**
     * @param actions the actions to set
     */
    public void setActions(Deque<RobotAction> actions) {
        this.actions = actions;
    }

    /**
     * @return the rollbackActions
     */
    public Deque<RobotAction> getRollbackActions() {
        return rollbackActions;
    }

    /**
     * @param rollbackActions the rollbackActions to set
     */
    public void setRollbackActions(Deque<RobotAction> rollbackActions) {
        this.rollbackActions = rollbackActions;
    }

    
/**
     * @return the worldPrintingSupport
     */
    public JComponent getWorldPrintingSupport() {
        return worldPrintingSupport;
    }

    /**
     * @param worldPrintingSupport the worldPrintingSupport to set
     */
    public void setWorldPrintingSupport(JComponent worldPrintingSupport) {
        this.worldPrintingSupport = worldPrintingSupport;
    }
    /**
     * 
     * @param x
     * @param y
     * @return 
     */
    public WorldConsts getWorldConst(int x, int y) {
        return getMap().getWorld()[x][y];
    }

    /**
     * 
     * @param pos
     * @return 
     */
    public WorldConsts getWorldConst(RobotPositionAndDirection pos) {
        return getWorldConst(pos.getX(),pos.getY());
    }

    /**
     * @return the map
     */
    public RobotWorldMap getMap() {
        return map;
    }

    /**
     * @param map the map to set
     */
    public void setMap(RobotWorldMap map) {
        this.map = map;
    }

    /**
     * @return the haveBeenScanned
     */
    public boolean isScanned() {
        return scanned;
    }

    /**
     * @param scanned the haveBeenScanned to set
     */
    public void setScanned(boolean scanned) {
        this.scanned = scanned;
    }    
    
    
    public static enum RobotAction {
        BACK, 
        INITIALIZE, 
        TURN_LEFT, 
        TURN_RIGHT, 
        CONTINUE, 
        SCAN
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
