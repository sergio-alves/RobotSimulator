/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotsimulator.playground;


import ch.santosalves.robotsimulator.playground.api.LightType;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.swing.JComponent;

/**
 *
 * @author Sergio
 */
public class LightSource extends JComponent implements ch.santosalves.robotsimulator.playground.api.LightSource{
    public static final int LIGHT_WIDTH = 32;
    public static final int LIGHT_HEIGHT = 32; 
    
    private double spotAngle = 15;
    private double angle = 90;
    private int z;
    private int lumens;
    private LightType type = LightType.Spot;
    
    /**
     * @return the type
     */
    @Override
    public LightType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    @Override
    public void setType(LightType type) {
        this.type = type;
    }

    /**
     * @return the spotAngle
     */
    @Override
    public double getSpotAngle() {
        return spotAngle;
    }

    /**
     * @param spotAngle the spotAngle to set
     */
    @Override
    public void setSpotAngle(double spotAngle) {
        this.spotAngle = spotAngle;
    }
    
    //high of the lamp 
    @Override
    public int getZ(){
        return z;
    }
        
    @Override
    public void setZ(int z) {
        this.z = z;
    }    
    
    @Override
    public int getLumens() {
        return this.lumens;
    }
    
    @Override
    public void setLumens(int lumens) {
        this.lumens = lumens;
    }
    
    protected double getDistanceBetweenPoints(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
    }
            
    
    @Override
    public double getLux(Point p) {
        double groundDistance = getDistanceBetweenPoints(this.getLocation(), p);
        double angle = Math.atan(groundDistance/z);
        
        switch (type) {
            case Directional:
                
            case Omni:
                
            case Spot:
                if(spotAngle/2 <= angle) {
                    return 0;
                } else {
                    double circleRadius = z * Math.tan(spotAngle/2);
                    double surfaceCircle = getCircleSurface(circleRadius);

                    // for simplification purpose
                    double lux = lumens / surfaceCircle;
                    
                    //calc surface exposed to light for the point p
//                    if()
                }
                break;
        }                
        
        
        return 0.0;
    }
    
    
    public double getSphereSurface(double radius) {
        return 4 * Math.PI*Math.pow(radius,2);           
    }
    
    public double getCircleSurface(double radius) {
        return 2 * Math. PI * radius;   
    }
    
    @Override
    public double getEmissionAngle() {
        return this.angle;
    }
    
    @Override
    public void setEmissionAngle(double angle) {
        this.angle = angle;
    }
    
    
    
    
    
    
    public LightSource() {
        Dimension d = new Dimension(LIGHT_WIDTH, LIGHT_HEIGHT);
        
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
        setSize(LIGHT_WIDTH, LIGHT_HEIGHT);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); //To change body of generated methods, choose Tools | Templates.
        g.setColor(Color.YELLOW);
        ((Graphics2D)g).setStroke(new BasicStroke(2));
                
        g.drawLine(6, 6, LIGHT_WIDTH - 6, LIGHT_HEIGHT - 6);
        
        g.drawLine(0, LIGHT_HEIGHT/2, LIGHT_WIDTH, LIGHT_HEIGHT/2);
        
        g.drawLine(6, LIGHT_HEIGHT-6, LIGHT_WIDTH-6, 6);
        
        g.drawLine(LIGHT_WIDTH/2, 0, LIGHT_WIDTH/2, LIGHT_HEIGHT);
        
        g.fillOval(7, 7, LIGHT_WIDTH-14, LIGHT_HEIGHT-14);
    }    

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x - x % LIGHT_WIDTH, y - y % LIGHT_HEIGHT, width, height); //To change body of generated methods, choose Tools | Templates.         
        repaint();
    }
}
