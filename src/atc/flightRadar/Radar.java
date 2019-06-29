/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.flightRadar;

import atc.simulator.AircrafWrapper;
import atc.simulator.Simulator;
import atc.util.Field;
import atc.util.SimulatorMatrix;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cekov
 */
public class Radar extends Thread{
    
    Integer refreshRate;
    
    Simulator simulator;
    
    File f = new File("map.txt");
    
    public Radar(Simulator simulator){
        readConfiguration();
        this.simulator = simulator;
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

    private synchronized void writeState() {
        if(!f.exists()){
            try{
                f.createNewFile();
            }catch(IOException ioex){
                Logger.getLogger(Radar.class.getName()).log(Level.SEVERE, null, ioex);
                System.err.println("unable to create map.txt");
            }
        }
        try {
                Path p = f.toPath();
                while(!Files.isWritable(p)){
                    try {
                        sleep(50);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Radar.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                FileOutputStream fos = new FileOutputStream(f);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeUnshared(simulator.getAircrafts());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Radar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Radar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex){
            Logger.getLogger(Radar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
