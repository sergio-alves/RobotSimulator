/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotics.helpers;

/**
 * Immutable vector class
 * @author Sergio
 */
public final class Vector2D {
    private final int a1;
    private final int a2;
    
    public int getA1() {
        return this.a1;
    }
    
    public int getA2() {
        return this.a2;
    }
    
    public Vector2D(int a1, int a2) {
        this.a1=a1;
        this.a2 = a2;
    }
    
    public Vector2D add(Vector2D v) {
        return new Vector2D(this.a1+ v.a1, this.a2+v.a2);
    }
    
    public double getNorm() {
        return Math.sqrt(a1*a1 + a2*a2);
    }

    @Override
    public boolean equals(Object obj) {
        Vector2D intance; 
        if(obj instanceof Vector2D) {
            intance = (Vector2D)obj;            
            return (intance.a1 == a1 && intance.a2 == a2);
        }
        
        return false;
    }
    
    public Vector2D multiplyByScaler(int scalar) {
        return new Vector2D(this.a1 * scalar, this.a2 * scalar);
    }
        
    public double getAngleBetweenVectors(Vector2D v) {
        if(this.equals(v.multiplyByScaler(-1))) {
            return Math.PI;
        }else {
            return Math.signum(this.a1 * v.a2-v.a1*this.a2) * Math.acos((this.a1 * v.a1 + this.a2 * v.a2) / (getNorm()* v.getNorm()));
        }
    }
    
    public Vector2D rotate(double angle) {
        return new Vector2D((int)(Math.cos(angle) * a1 - Math.sin(angle) * a2), (int)(Math.sin(angle) * a1 + Math.cos(angle) * a2));
    }

    @Override
    public String toString() {
        return "(x,y)->(" + a1 + ", " + a2 + ")";
    }

    public Vector2D() {
        this.a1 = 0;
        this.a2 = 0;
    }    
    
    public Vector2D(Vector2D v) {
        this.a1 = v.a1;
        this.a2 = v.a2;
    }
}
