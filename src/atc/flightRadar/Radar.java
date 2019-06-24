/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.flightRadar;

import atc.simulator.AircrafWrapper;
import atc.util.Field;
import atc.util.SimulatorMatrix;
import atc.util.SimulatorUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cekov
 */
public class Radar extends Thread{
    
    SimulatorMatrix map;
    
    Integer refreshRate;
    
    public Radar(SimulatorMatrix map){
        this.map = map;
        readConfiguration();
        setDaemon(true);
    }

    
    
    
    private void readConfiguration() {
         try(InputStream  input = new FileInputStream("radar.properties")){
            Properties properties = new Properties();
            properties.load(input);
            //set properties
            this.refreshRate = Integer.parseInt(properties.getProperty("refreshRate"));
            
         }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    @Override
    public void run(){
        while(true){
            try {
                Thread.sleep(refreshRate*1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Radar.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            writeState();
        }
    }

    private void writeState() {
        
        File f = new File("map.txt");
        if(!f.exists()){
            try{
                f.createNewFile();
            }catch(IOException ioex){
                System.err.println("unable to create map.txt");
            }
        }
        
        HashSet<AircrafWrapper> info = new HashSet<>();
        
        for(Field field : map.getMap()){
            HashSet<AircrafWrapper> wrappers = field.getAircrafts();
            if(!wrappers.isEmpty()){
                info.addAll(wrappers);
            }
        }
        
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            oos.writeObject(info);
            System.out.println("aircratfs saved");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Radar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Radar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
