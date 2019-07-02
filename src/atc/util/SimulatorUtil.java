/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ceko
 */
public class SimulatorUtil {
    
    public static Random random = new Random();
    
    public static int sizeX = 10;
    public static int sizeY = 10;
    
    public static Configuration readConfiguration(){
        while(!Files.isReadable(Path.of("config.properties"))){
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(SimulatorUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try(InputStream  input = new FileInputStream("config.properties")){
            Properties properties = new Properties();
            properties.load(input);
            sizeX = Integer.parseInt(properties.getProperty("sizeX"));
            sizeY = Integer.parseInt(properties.getProperty("sizeY"));
            Integer creationTime = Integer.parseInt(properties.getProperty("creationTime"));
            Boolean hasForeignAircraft = Boolean.parseBoolean(properties.getProperty("hasForeignAircraft"));
            Boolean stoped = Boolean.parseBoolean(properties.getProperty("stoped"));
            Boolean random = Boolean.parseBoolean(properties.getProperty("random"));
            Integer numberOfMaxAC = Integer.parseInt(properties.getProperty("numberOfMaxAC"));
            input.close();
            return new Configuration(sizeX,sizeY,creationTime,hasForeignAircraft,numberOfMaxAC,stoped,random);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return new Configuration();
    }

    public static Integer getRandomHight() {
        return 100*(random.nextInt(4)+8);
    }
    
    public static Integer getRandomSpeed(){
        return random.nextInt(4)+1;
    }
    
    
}
