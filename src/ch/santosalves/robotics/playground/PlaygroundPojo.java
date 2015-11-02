/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotics.playground;

/**
 *
 * @author Sergio
 */
public class PlaygroundPojo {
    private int width;
    private int height;
    private int size;
    private int[][] data;

    public PlaygroundPojo() {

    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the data
     */
    public int[][] getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(int[][] data) {
        this.data = data;
    }
    
}
