/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.simulator;

import atc.util.Configuration;
import atc.util.ATCEvent;
import atc.util.CompressFiles;
import atc.util.CrashAlert;
import atc.util.Field;
import atc.util.SimulatorMatrix;
import atc.util.SimulatorUtil;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.aircraft.MilitaryAircraft;

/**
 *
 * @author cekov
 */
public class Simulator extends Thread {
    
    
    private Configuration config;
    
    private SimulatorMatrix matrix; 
    
    private HashSet<AircrafWrapper> aircrafts;
    
    public Simulator(){
        config = SimulatorUtil.readConfiguration();
        aircrafts = new HashSet<>();
        matrix = new SimulatorMatrix(config.sizeX,config.sizeY,this);
        System.out.println("created simulation");
        setDaemon(true);
    }
    
    @Override
    public void run(){
        System.out.println("started simulation");
        int i = 0;
        boolean alreadyEntered = false;
        while(true){
            config = SimulatorUtil.readConfiguration();
            
            if(config.hasForeignAircraft && !config.stoped){
                if (!alreadyEntered){
                    changeCourseOfFlights();
                    alreadyEntered = true;
                }
                AircrafWrapper aircraft = new AircrafWrapper(this,config.hasForeignAircraft);
                aircrafts.add(aircraft);
                aircraft.start();
                ATCEvent atce = new ATCEvent(aircraft);
                atce.writeToFile();
                try {
                    sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Simulator.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (aircraft.field.isAtCorrner()){
                    addOneHunter(aircraft);
                }else{
                    addTwoHunters(aircraft);
                }
            }else if (!config.hasForeignAircraft && (!config.stoped)){
                if (i++ < config.numerOfMaxAC){
                    createAircraft();
                }else if (config.numerOfMaxAC == 0){
                    createAircraft();
                }
                alreadyEntered = false;
            }
            try{
                Thread.sleep(config.creationTime*1000);
            }catch(InterruptedException ex){
                Logger.getLogger(CompressFiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    Field getNextFiled(AircrafWrapper aircrafWrapper) {
        
        if (aircrafWrapper.getField() != null){
            
            return matrix.getNextField(aircrafWrapper);
        }
        return null;
        
    }

    Field getStartingField(AircrafWrapper.Direction direction) {
        Integer x = 0;
        Integer y = 0;
        switch(direction){
            case NORTH:
                x = SimulatorUtil.random.nextInt(config.sizeX);
                y = 0;
                break;
            case SOUTH:
                x = SimulatorUtil.random.nextInt(config.sizeX);
                y = config.sizeY - 1;
                break;
            case EAST:
                x = 0;
                y = SimulatorUtil.random.nextInt(config.sizeY);
                break;
            case WEST:
                x = config.sizeX - 1;
                y = SimulatorUtil.random.nextInt(config.sizeY);
                break;
        }
        Integer x1 = x;
        Integer y1 = y;
        Field f = matrix.getMap().stream().parallel().filter(e -> e.equals(new Field(x1, y1))).findFirst().get();
        return f;
    }

    public synchronized void planesColided(AircrafWrapper aircraft) {
        aircraft.finish();
        aircrafts.remove(aircraft);
        AircrafWrapper colider = aircraft.field.removeAircraftsOnAltitude(aircraft.getAircraft().getHeight());
        aircrafts.remove(colider);
        new CrashAlert(aircraft,colider).createAlertFile();
    }

    public Configuration getConfig() {
        if (config == null)
            return new Configuration();
        return config;
    }

    private void changeCourseOfFlights() {
        aircrafts.stream().forEach(e -> {
            if (!(e.getAircraft() instanceof MilitaryAircraft))
                e.findClosestExit();
                });
    }

    public SimulatorMatrix getMatrix() {
        return matrix;
    }

    synchronized void removeAircraft(AircrafWrapper aircraft) {
        aircrafts.remove(aircraft);
    }

    synchronized public HashSet<AircrafWrapper> getAircrafts() {
        return aircrafts;
    }

    private void addOneHunter(AircrafWrapper aircraft) {
        if (aircraft.direction == AircrafWrapper.Direction.NORTH && aircraft.field.getX() == 0)
            generateHunter(new Field(1,0),aircraft);
        else if (aircraft.direction == AircrafWrapper.Direction.EAST && aircraft.field.getX() == 0)
            generateHunter(new Field(0,1),aircraft);
        else if (aircraft.direction == AircrafWrapper.Direction.SOUTH && aircraft.field.getX() == config.sizeX - 1)
            generateHunter(new Field(config.sizeX - 1,1),aircraft);
        else if (aircraft.direction == AircrafWrapper.Direction.EAST && aircraft.field.getX() == config.sizeX - 1)
            generateHunter(new Field(config.sizeX - 2,0),aircraft);
        else if (aircraft.direction == AircrafWrapper.Direction.WEST && aircraft.field.getY() == 0)
            generateHunter(new Field(1,config.sizeY - 1),aircraft);
        else if (aircraft.direction == AircrafWrapper.Direction.NORTH && aircraft.field.getY() == 0)
            generateHunter(new Field(0,config.sizeY - 2),aircraft);
        else if (aircraft.direction == AircrafWrapper.Direction.WEST && aircraft.field.getY() == config.sizeY - 1)
            generateHunter(new Field(config.sizeX - 1,config.sizeY - 2),aircraft);
        else if (aircraft.direction == AircrafWrapper.Direction.SOUTH && aircraft.field.getY() == config.sizeY - 1)
            generateHunter(new Field(config.sizeX - 2,config.sizeY - 1),aircraft);
        
        
    }

    private void addTwoHunters(AircrafWrapper aircraft) {
        if(null != aircraft.direction)switch (aircraft.direction) {
            case NORTH:
                generateHunter(new Field(aircraft.field.getX()-1,0),aircraft);
                generateHunter(new Field(aircraft.field.getX()+1,0),aircraft);
                break;
            case SOUTH:
                generateHunter(new Field(aircraft.field.getX()-1,config.sizeY - 1),aircraft);
                generateHunter(new Field(aircraft.field.getX()+1,config.sizeY - 1),aircraft);
                break;
            case EAST:
                generateHunter(new Field(0,aircraft.field.getY() - 1),aircraft);
                generateHunter(new Field(0,aircraft.field.getY() + 1),aircraft);
                break;
            case WEST:
                generateHunter(new Field(config.sizeX - 1,aircraft.field.getY() - 1),aircraft);
                generateHunter(new Field(config.sizeX - 1,aircraft.field.getY() + 1),aircraft);
                break;
            default:
                break;
        }
    }

    private void generateHunter(Field field,AircrafWrapper aircraft) {
        AircrafWrapper wrapper = new AircrafWrapper(this,field,aircraft);
        wrapper.start();
        synchronized (Simulator.class){
            aircrafts.add(wrapper);
        }
    }
     private void createAircraft() {
        AircrafWrapper aircraft = new AircrafWrapper(this);
        aircrafts.add(aircraft);
        aircraft.start();
    }
    
    //TODO: M-L FIX REDIRECTION                   -check
    //TODO: H ADD CRASH REPORT (ALERT OBJECT)     -check
    //TODO: H READ CRASH REPORT AND ALERT         -check
    //TODO: M ADD X, Y SIZE TO PROPERTIES         -check
    //TODO: H CREATE EVENT LIST SCENE             -check
    //TODO: H CREATE EVENT WATCHER                -check
    //TODO: H CREATE BUTTON FOR ALERT LIST        -check
    //TODO: L ADD unnecessary METHODES IN MODEL
    //TODO: H ZIP FILES                           -check
    //TODO: M ADD RANDOMNES (HIGHT AND SPEED)     -check

   
    
}
