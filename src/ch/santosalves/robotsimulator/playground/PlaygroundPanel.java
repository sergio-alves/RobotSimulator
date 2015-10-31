/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotsimulator.playground;

import ch.santosalves.robotsimulator.playground.api.Playground;
import ch.santosalves.robotsimulator.helpers.CoordinatesTranslator;
import ch.santosalves.robotsimulator.playground.api.MultiLayeredPlayground;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *
 * @author Sergio
 */
public class PlaygroundPanel extends JPanel {
    // the taild playground 
    private final TiledPlayground playground;
        
    public PlaygroundPanel() {
        playground = new TiledPlayground(1);
        setMinimumSize(new Dimension(playground.getTileSize()*playground.getPlaygroundWidth() + 1, 
                playground.getPlaygroundHeight()*playground.getTileSize() + 1));
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g); //To change body of generated methods, choose Tools | Templates.
        drawPlayground(g);
    }
    
    public void drawPlayground(Graphics g) {
        Color borderColor =  new Color(100,100,100);
        Color voidColor = new  Color(80, 80, 80);
        Color wallColor =  new Color(150, 200, 80);
        
        
        for(int i=0; i < getPlayground().getPlaygroundWidth(); i++) {
            for(int j=0; j < getPlayground().getPlaygroundHeight(); j++) {

                switch(TileType.getByValue(getPlayground().getGround()[i][j])) {
                    case Void:
                        g.setColor(voidColor);
                        g.fillRect(i*playground.getTileSize(), j*playground.getTileSize(), playground.getTileSize(), playground.getTileSize());
                        break;
                    case Wall:
                        g.setColor(wallColor);
                        g.fillRect(i*playground.getTileSize(),j*playground.getTileSize(),playground.getTileSize(), playground.getTileSize());
                        break;
                    default:
                }
                
                g.setColor(borderColor);
                g.drawRect(i*playground.getTileSize(),j*playground.getTileSize(),playground.getTileSize(), playground.getTileSize());
            }
        }
    }
    
    /**
     * @return the playground
     */
    public MultiLayeredPlayground getPlayground() {
        return playground;
    }
    
    public void setPlayground(TiledPlayground ground) {
        //playground = ground;
    }    
}
