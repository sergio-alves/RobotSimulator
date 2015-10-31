/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotics.robots.api;

import ch.santosalves.robotics.helpers.Vector2D;
import java.awt.Point;

/**
 *
 * @author Sergio
 */
public class RobotPositionAndDirection {
    private Point position;
    private Vector2D direction;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new RobotPositionAndDirection(new Point(position.x, position.y), new Vector2D(direction));
    }    
    
    public RobotPositionAndDirection(RobotPositionAndDirection r) {
        this.direction = new Vector2D(r.getDirection().getA1(), r.getDirection().getA2());
        this.position = new Point(r.getPosition());
    }
    
    public RobotPositionAndDirection(Point pos, Vector2D dir) {
        position = pos;
        direction = dir;
    }
    
    public void addToX(int v) {
        this.position.x += v;
    }
    
    public void addToY(int v) {
        this.position.y += v;
    }

    public int getX() {
        return position.x;
    }

    public int getY() {
        return position.y;
    }

    /**
     * @return the position
     */
    public Point getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(Point position) {
        this.position = position;
    }

    public void setPosition(int x, int y) {
        this.position.x = x;
        this.position.y = y;
    }
    
    /**
     * @return the direction
     */
    public Vector2D getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(Vector2D direction) {
        this.direction = direction;
    }
    
    public void moveFront() {
        this.position.x += direction.getA1() ;
        this.position.y += direction.getA2() ;
    }

    public void moveBack() {
        this.position.x -= direction.getA1() ;
        this.position.y -= direction.getA2() ;
    }

    /**
     * the same as moveFront but does not change the robot position
     * @return A point object with the coordinates of next step if direction remains
     */
    public RobotPositionAndDirection whatIsNextStep() {
        return new RobotPositionAndDirection(new Point(this.position.x + direction.getA1(), this.position.y + direction.getA2() ), this.getDirection());
    }
}
