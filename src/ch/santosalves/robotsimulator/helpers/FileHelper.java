/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotsimulator.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

/**
 *
 * @author Sergio
 */
public class FileHelper {
    private static final Logger LOGGER = Logger.getLogger(FileHelper.class.getName());
    
    public static enum ResourcesTypes{
        Images,
        Layouts,
        Templates                
    }
   
    /**
     * Gets a resource from user home directory.
     * 
     * @param fdir The directory name part
     * @param fname The filename part
     * 
     * @return The wished resource
     * 
     * @throws FileNotFoundException if could not create directory
     *//*
    public static File getResourceInAppDir(String fdir, String fname) throws FileNotFoundException {
        //File userHome = new File(System.getProperty("user.home"));
        
        File userDir = new File(System.getProperty("user.dir"));
        File f = new File(userDir.getAbsolutePath() + "/" + fdir);
                
        if(!f.exists() && !f.mkdirs())
            throw new FileNotFoundException("File "+ f.getAbsolutePath() + " could not be found.");

        f = new File(f.getAbsolutePath() + "/" + fname);
        
        return f;
    }    */
        
    public static String PLUGINS_DIRECTORY_NAME = "plugins";
    public static String LAYOUTS_DIRECTORY_NAME = "layouts";

    private static String getDirectory(String subDir) throws FileNotFoundException, IOException {
        File f = new File(System.getProperty("user.dir")+ "/" + subDir);
        
        if(!f.exists() && !f.mkdirs())
            throw new FileNotFoundException("File "+ f.getAbsolutePath() + " could not be found.");        
        
        return f.getCanonicalPath();        
    }
    
    public static String getPluginsDirectory() throws FileNotFoundException, IOException {
        return getDirectory(PLUGINS_DIRECTORY_NAME);
    }
    
    public static String getPluginsDirectory(String dir) throws FileNotFoundException, IOException {
        return getDirectory(PLUGINS_DIRECTORY_NAME+ "/" + dir);
    }
    
    public static File getPlugin(String dir, String fname) throws IOException {
        return new File(getDirectory(PLUGINS_DIRECTORY_NAME + "/" + dir) + "/" + fname );
    }
    
    public static String getLayoutsDirectory() throws FileNotFoundException, IOException {
        return getDirectory(LAYOUTS_DIRECTORY_NAME);
    }

    public static File getLayout(String dir, String fname) throws IOException {
        return new File(getDirectory(LAYOUTS_DIRECTORY_NAME + "/" + dir) + "/" + fname );
    }    
}
