/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.util;

import atc.simulator.Configuration;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

/**
 *
 * @author Ceko
 */
public class SimulatorUtil {
    
    public static Random random = new Random();
    
    public static Configuration readConfiguration(){
        
        try(InputStream  input = new FileInputStream("config.properties")){
            Properties properties = new Properties();
            properties.load(input);
            //set properties
            Integer size = Integer.parseInt(properties.getProperty("size"));
            Integer creationTime = Integer.parseInt(properties.getProperty("creationTime"));
            Boolean hasForeignAircraft = Boolean.parseBoolean(properties.getProperty("hasForeignAircraft"));
            Integer numberOfMaxAC = Integer.parseInt(properties.getProperty("numberOfMaxAC"));
            return new Configuration(size,creationTime,hasForeignAircraft,numberOfMaxAC);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
        return new Configuration();
    }
    
}
