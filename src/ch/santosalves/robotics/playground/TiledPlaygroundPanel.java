/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotics.playground;

import ch.santosalves.robotics.playground.api.MultiLayeredPlayground;
import ch.santosalves.robotics.playground.api.TiledPlayground;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author Sergio
 */
public class TiledPlaygroundPanel extends JPanel implements TiledPlayground{
    private MultiLayeredPlayground playground;  //use composition instead of having lots of duplicated src code    
    private TileType defaultTile = TileType.Wall;
    
    private boolean showPopupMenu = false;
    private boolean allowEdition = false;
    
    public TiledPlaygroundPanel() {
        playground = new ch.santosalves.robotics.playground.TiledPlayground();
        updatePlaygroundUI(getBounds().getSize());

        initializeComponents();

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(!isAllowEdition()&& !mousePressed)
                    return;
                // TODO add your handling code here:
                int x = (e.getX() - e.getX() % playground.getTileSize()) / playground.getTileSize();
                int y = (e.getY() - e.getY() % playground.getTileSize()) / playground.getTileSize();
                setPlaygroundCell(new Point(x, y), getDefaultTile().getValue());
                repaint();
            }
/*
            @Override
            public void mouseMoved(MouseEvent e) {
                if(!isAllowEdition() && !mousePressed)
                    return;
                // TODO add your handling code here:
                int x = (e.getX() - e.getX() % playground.getTileSize()) / playground.getTileSize();
                int y = (e.getY() - e.getY() % playground.getTileSize()) / playground.getTileSize();
                setPlaygroundCell(new Point(x, y), getDefaultTile().getValue());
                repaint();

            }   */            
        });
        
        addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e) {
                if(!isAllowEdition())
                    return;
                // TODO add your handling code here:
                int x = (e.getX() - e.getX() % playground.getTileSize()) / playground.getTileSize();
                int y = (e.getY() - e.getY() % playground.getTileSize()) / playground.getTileSize();
                setPlaygroundCell(new Point(x, y), getDefaultTile().getValue());
                repaint();
                mousePressed = true;
                e.consume();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed=false;
            }            
        });
    }
    
    boolean mousePressed = false;
    JRadioButtonMenuItem item;

    private void initializeComponents() {
        ButtonGroup bg = new ButtonGroup();
        JPopupMenu popupMenu = new JPopupMenu("PopupMenu");
        JMenu groundType = new JMenu("Ground Type");
    
        bg.clearSelection();

        for (TileType t : TileType.values()) {
            item = new JRadioButtonMenuItem(t.toString());
            groundType.add(item);
            bg.add(item);

            item.getModel().addChangeListener((ChangeEvent e) -> {
                if (bg.getSelection() == item.getModel()) {
                    defaultTile = t;
                }
            });
            if (t == defaultTile) {
                bg.setSelected(item.getModel(), true);
            }
        }

        popupMenu.add(groundType);
        popupMenu.pack();

        if (!isShowPopupMenu()) {
            popupMenu.setVisible(false);
        }
        
        popupMenu.add(new JMenuItem("Grid Width")).addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                playground.setTileSize(new Integer(JOptionPane.showInputDialog("Tile Size (px)", 16)));
            }
        });

        setComponentPopupMenu(popupMenu);
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g); //To change body of generated methods, choose Tools | Templates.
        drawPlayground(g);
    }


    public void setPlaygroundCell(Point p, int type) {
        playground.getGround()[p.x][p.y] = type;
    }

    public void drawPlayground(Graphics g) {
        Color borderColor = new Color(100, 100, 100);
        Color voidColor = new Color(80, 80, 80);
        Color wallColor = new Color(150, 200, 80);

        for (int i = 0; i < playground.getPlaygroundWidth(); i++) {
            for (int j = 0; j < playground.getPlaygroundHeight(); j++) {
                switch (TileType.getByValue(playground.getGround()[i][j])) {
                    case Void:
                        g.setColor(voidColor);
                        g.fillRect(i * playground.getTileSize(), j * playground.getTileSize(), playground.getTileSize(), playground.getTileSize());
                        break;
                    case Wall:
                        g.setColor(wallColor);
                        g.fillRect(i * playground.getTileSize(), j * playground.getTileSize(), playground.getTileSize(), playground.getTileSize());
                        break;
                    default:
                }

                g.setColor(borderColor);
                g.drawRect(i * playground.getTileSize(), j * playground.getTileSize(), playground.getTileSize(), playground.getTileSize());
            }
        }
    }

    private void updatePlaygroundUI(Dimension d) {
        this.setBounds(0, 0, d.width, d.height);
        this.setSize(d);
        this.getPreferredSize().setSize(d);
        this.getMinimumSize().setSize(d);
        
        if(playground != null)
            playground.initializeLayers();
        repaint();
    }

    /**
     * @return the defaultTile
     */
    public TileType getDefaultTile() {
        return defaultTile;
    }

    /**
     * @param defaultTile the defaultTile to set
     */
    public void setDefaultTile(TileType defaultTile) {
        this.defaultTile = defaultTile;
    }
    
    public MultiLayeredPlayground getPlayground() {
        return this.playground;
    }
    public void setPlayground(MultiLayeredPlayground pg) {;
        this.playground = pg;

        Dimension d = new Dimension(getPlaygroundWidth()*getTileSize(), getPlaygroundHeight()*getTileSize());
                
        this.setBounds(0, 0, d.width, d.height);
        this.setSize(d);
        this.getPreferredSize().setSize(d);
        this.getMinimumSize().setSize(d);
        repaint();        
        //updatePlaygroundUI(new Dimension(getPlaygroundWidth()*getTileSize(), getPlaygroundHeight()*getTileSize()));
    }

    @Override
    public int[][] getGround() {
        return playground.getGround();
    }

    @Override
    public void setGround(int[][] ground) {
        playground.setGround(ground);
        Dimension d = new Dimension(getPlaygroundWidth()*getTileSize(), getPlaygroundHeight()*getTileSize());
                
        this.setBounds(0, 0, d.width, d.height);
        this.setSize(d);
        this.getPreferredSize().setSize(d);
        this.getMinimumSize().setSize(d);
        repaint();        
    }

    @Override
    public int getPlaygroundHeight() {
        return playground.getPlaygroundHeight();
    }

    @Override
    public void setPlaygroundHeight(int playgroundHeight) {
        playground.setPlaygroundHeight(playgroundHeight);
        updatePlaygroundUI(new Dimension(getPlaygroundWidth()*getTileSize(), getPlaygroundHeight()*getTileSize()));
    }

    @Override
    public int getPlaygroundWidth() {
        return playground.getPlaygroundWidth();
    }

    @Override
    public void setPlaygroundWidth(int playgroundWidth) {
        playground.setPlaygroundWidth(playgroundWidth);
        updatePlaygroundUI(new Dimension(getPlaygroundWidth()*getTileSize(), getPlaygroundHeight()*getTileSize()));
    }

    @Override
    public int getTileSize() {
        return playground.getTileSize();
    }

    @Override
    public void setTileSize(int tileSize) {
        playground.setTileSize(tileSize);
        updatePlaygroundUI(new Dimension(getPlaygroundWidth()*getTileSize(), getPlaygroundHeight()*getTileSize()));
    }

    /**
     * @return the showPopupMenu
     */
    public boolean isShowPopupMenu() {
        return showPopupMenu;
    }

    /**
     * @param showPopupMenu the showPopupMenu to set
     */
    public void setShowPopupMenu(boolean showPopupMenu) {
        this.showPopupMenu = showPopupMenu;
        getComponentPopupMenu().setVisible(this.showPopupMenu);
    }

    /**
     * @return the allowEdition
     */
    
    public boolean isAllowEdition() {
        return allowEdition;
    }

    /**
     * @param allowEdition the allowEdition to set
     */
   
    public void setAllowEdition(boolean allowEdition) {
        this.allowEdition = allowEdition;
    }
}
