/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.flightRadar;

import atc.util.ATCEvent;
import atc.simulator.AircrafWrapper;
import atc.util.CrashAlert;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import static java.lang.Thread.sleep;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cekov
 */
public class DirectoryWatcher extends Thread{
    
    FlightRadarController controller;
    
    public DirectoryWatcher(FlightRadarController controller){
        this.controller = controller;
        setDaemon(true);
    }
    
    @Override
    public void run(){
        final Path atcPath = FileSystems.getDefault().getPath("");
        final Path alertPath = FileSystems.getDefault().getPath("alert");
        final Path eventPath = FileSystems.getDefault().getPath("events");
        try ( WatchService watchService = FileSystems.getDefault().newWatchService()) {
            WatchKey watchKey = atcPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            WatchKey alertsKey = alertPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            WatchKey eventsKey = eventPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            while (true) {
                
                List<WatchEvent<?>> events = watchKey.pollEvents();
                events.addAll(alertsKey.pollEvents());
                events.addAll(eventsKey.pollEvents());
                boolean readable = false;
                if(!events.isEmpty()){
                    for (WatchEvent<?> event : events) {
                        final Path changed = (Path) event.context();
                        if (changed.endsWith("map.txt")) {
                            readable = true;
                        }else if (changed.toString().contains(".alt")){
                            CrashAlert alert = deserializeAlert(Path.of("alert\\"+changed.toString()));
                            if (alert != null && alert.getFirst() != null){
                                controller.alert(alert);
                            }
                        }else if (changed.toString().contains(".evt")){
                            ATCEvent atce = deserializeEvent(Path.of("events\\"+changed.toString()));
                            if(atce != null){
                                controller.addEvent(atce);
                            }
                        }
                    }
                }
                
                Thread.sleep(50);
                // reset the key
                boolean valid = watchKey.reset();
                valid = alertsKey.reset() || valid;
                valid = eventsKey.reset() || valid;
                if(readable){
                    HashSet<AircrafWrapper> aircrafts = deserializeMap();
                    if (aircrafts != null)
                        controller.updateMap(aircrafts);
                }
                if (!valid) {
                    System.out.println("Key has been unregisterede");
                }
            }
        } catch(IOException ioex){
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ioex);
        } catch (InterruptedException ex) {
                Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private CrashAlert deserializeAlert(Path changed) {
        try {
            return (CrashAlert) (new ObjectInputStream(new FileInputStream(changed.toFile()))).readObject();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        }catch (Exception ex){
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private HashSet<AircrafWrapper> deserializeMap() {
        
        try {
            
            HashSet<AircrafWrapper> aircrafts;
            
            Path p = Path.of("map.txt");
            while(!Files.isReadable(p))
            {
                System.out.println("ITS NOT READABLE");
                try {
                    sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            FileInputStream fis = new FileInputStream(p.toFile());
            ObjectInputStream ois = new ObjectInputStream(fis);
            aircrafts = (HashSet<AircrafWrapper>) ois.readUnshared();
            //todo: fix syncronious reading/writing
            return aircrafts;
            
        }catch (EOFException ex) {
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.FINE, null, ex);
        }  catch (FileNotFoundException ex) {
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        }catch (Exception ex){
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        }
    return null;
    }

    private ATCEvent deserializeEvent(Path path) {
        try {
            return (ATCEvent) (new ObjectInputStream(new FileInputStream(path.toFile()))).readObject();
        }catch(EOFException ex){
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.FINE, null, ex);
        }catch (FileNotFoundException ex) {
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        }catch (Exception ex){
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
