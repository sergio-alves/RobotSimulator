package ch.santosalves.robotsimulator.robots;

import ch.santosalves.robotsimulator.inputs.RangeDetectors;
import ch.santosalves.robotsimulator.inputs.RobotSensorInput;
import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.santosalves.robotsimulator.robots.api.RobotPositionAndDirection;
import ch.santosalves.robotsimulator.robots.api.RobotWorldMap;
import ch.santosalves.robotsimulator.robots.api.GenericRobot;

import static ch.santosalves.robotsimulator.robots.api.RobotWorldMap.WorldConsts.*;
import static ch.santosalves.robotsimulator.robots.api.RobotWorldMap.WorldConsts;
import static ch.santosalves.robotsimulator.robots.api.GenericRobot.RobotAction.*;

public class RobotMarkIV extends GenericRobot{
    public RobotMarkIV() {
        super("RobotMarkIV");
        appendInputs();
    }
    
    private void appendInputs() {
        getInputs().add(new ch.santosalves.robotsimulator.inputs.VoidDetector());
        getInputs().add(new ch.santosalves.robotsimulator.inputs.ObstacleDetector());
    }//end appendInputs
    
    /**
     * Starts robot exploration and execution
     */
    @Override
    public void start() { 
        start( new Runnable() {
            private void checkSensorsAndUpdateMap(RobotPositionAndDirection pos) {
                RangeDetectors rd;
                Point p;

                for (RobotSensorInput input : getInputs()) {

                    if (input instanceof RangeDetectors) {
                        rd = (RangeDetectors) input;

                        getMap().updateWorldTile(pos.whatIsNextStep().getPosition(), 
                                getRightWorldConstant(rd, pos));

                        if (rd.getRange() > 1) {
                            if ((p = rd.getObstaclePosition(pos)) != null) {
                                getMap().updateWorldTile(p, OBSTACLE);
                            }
                        }
                    }
                }
            }

            private WorldConsts getRightWorldConstant(RangeDetectors rd, RobotPositionAndDirection pos) {
                WorldConsts nextPosWC = getWorldConstant(pos);
                return rd.isFreeAhead(pos) ? ( nextPosWC == UNKNOWN ? TO_VISIT : nextPosWC) : OBSTACLE;
            }

            private WorldConsts getWorldConstant(RobotPositionAndDirection pos) {
                return getMap().getWorld()[pos.whatIsNextStep().getPosition().x][pos.whatIsNextStep().getPosition().y];
            }
            
            private void algorithmF() {
                //look left and right and check that no to-visit cell is available
                RobotPositionAndDirection pos = new RobotPositionAndDirection(getPosition());
                //turn left and check is some case are marked to_visit
                pos.setDirection(pos.getDirection().rotate(-Math.PI / 2));
                if (getWorldConst(pos.whatIsNextStep()) == TO_VISIT) {
                    getMoves().push(TURN_LEFT);
                    getMoves().push(CONTINUE);
                    setRollback(false);
                    return;
                }
                //turn right and check is some case are marked to_visit
                pos = new RobotPositionAndDirection(getPosition());
                pos.setDirection(pos.getDirection().rotate(Math.PI / 2));
                if (getWorldConst(pos.whatIsNextStep()) == TO_VISIT) {
                    getMoves().push(TURN_RIGHT);
                    getMoves().push(CONTINUE);
                    setRollback(false);
                    return;
                }
                //Before going back scan the current position and do the previous
                //recheck only if there is in surroundings an unknow or to_visit block
                if (isScanned() == false && isSurroundingsVisited(getPosition()) == false) {
                    setScanned(true);
                    getMoves().push(SCAN);
                    return;
                }

                //Ok, a full scan with turn left and right on position does not give any results then
                //Start rollback till a explorable path is detected => TO_VISIT detected
                setRollback(true);
                RobotAction action = getActions().pop();
                switch (action) {
                    case BACK:
                        getMoves().push(CONTINUE);
                    case CONTINUE:
                        getMoves().push(BACK);
                        break;
                    case TURN_LEFT:
                        getMoves().push(TURN_RIGHT);
                        break;
                    case TURN_RIGHT:
                        getMoves().push(TURN_LEFT);
                        break;
                }                
            }
            
            @Override
            public void run() {
                RobotAction actionExecuted = null;
                RobotPositionAndDirection next;
                
                while (true) {
                    //Can have one or more instructions to execute
                    do {
                        try {
                            actionExecuted = executeNext();
                            
                            if (actionExecuted != null) {
                                
                                //If the command is a move command then update the world matrix
                                switch (actionExecuted) {
                                    case INITIALIZE:
                                    case CONTINUE:
                                    case BACK:
                                        setScanned(false);
                                        getMap().setPosition(getPosition().getPosition());
                                        break;
                                }
                                
                                checkSensorsAndUpdateMap(new RobotPositionAndDirection(getPosition()));
                                
                                getMap().drawRobotWorld(getWorldPrintingSupport().getGraphics(), (int) (getWorldPrintingSupport().getWidth() / getMap().getMapWidth()));
                            }
   
                            Thread.sleep(100);
                            
                        } catch (InterruptedException ex) {
                            Logger.getLogger(GenericRobot.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }while(getMoves().size() > 0);
                    
                    //After command execution we can get next commands to be executed
                    

                    next = getPosition().whatIsNextStep();
                    RobotWorldMap.WorldConsts cst = getMap().getWorld()[next.getX()][next.getY()];
                    
                    switch (cst) {
                    //The next step; if we don't change direction will be :
                        case TO_VISIT:
                            setRollback(false);
                            getMoves().push(CONTINUE);
                            break;
                        case UNKNOWN:
                        case OBSTACLE:
                        case VISITED:
                        case START_POINT:
                            algorithmF();
                            break;
                    }
                }                
            }
                            
            // end of the runnable anonymous class
        });
    }
    /**
     * 
     * @param pos
     * @return 
     */
    public boolean isSurroundingsVisited(RobotPositionAndDirection pos) {
        WorldConsts wc;
        
        wc = getWorldConst(pos.getX()+1, pos.getY());        
        if(wc == UNKNOWN || wc == TO_VISIT) {
            return false;
        }

        wc = getWorldConst(pos.getX()-1, pos.getY());        
        if(wc == UNKNOWN || wc == TO_VISIT) {
            return false;
        }
        
        wc = getWorldConst(pos.getX(), pos.getY()+1);        
        if(wc == UNKNOWN || wc == TO_VISIT) {
            return false;
        }

        wc = getWorldConst(pos.getX(), pos.getY()-1);        
        if(wc == UNKNOWN || wc == TO_VISIT) {
            return false;
        }

        return true;        
    }

    @Override
    protected void beforeActionExecution(RobotAction ra) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void afterActionExecution(RobotAction ra) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
