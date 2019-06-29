/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.simulator;

import atc.util.Alert;
import atc.util.Field;
import atc.util.SimulatorMatrix;
import atc.util.SimulatorUtil;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        matrix = new SimulatorMatrix(config.size,config.size,this);
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
            if(config.hasForeignAircraft && !alreadyEntered){
                AircrafWrapper aircraft = new AircrafWrapper(this,config.hasForeignAircraft);
                System.out.println("starting foreign aircraft entered!" + aircraft.direction.toString());
                aircrafts.add(aircraft);
                aircraft.start();
                try {
                    sleep(4000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Simulator.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("adding hunters");
                if (aircraft.field.isAtCorrner()){
                    addOneHunter(aircraft);
                }else{
                    addTwoHunters(aircraft);
                }
                alreadyEntered = true;
            }else if (!config.hasForeignAircraft){
                alreadyEntered = false;
                if (i++ < config.numerOfMaxAC){
                    AircrafWrapper aircraft = new AircrafWrapper(this);
                    aircrafts.add(aircraft);
                    aircraft.start();
                }else if (config.numerOfMaxAC == 0){
                    AircrafWrapper aircraft = new AircrafWrapper(this);
                    aircrafts.add(aircraft);
                    aircraft.start();
                }
                
                try{
                    Thread.sleep(config.creationTime*1000);
                }catch(InterruptedException ex){
                    System.out.println("simulator sleeps");
                }
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
                x = SimulatorUtil.random.nextInt(config.size);
                y = 0;
                break;
            case SOUTH:
                x = SimulatorUtil.random.nextInt(config.size);
                y = config.size - 1;
                break;
            case EAST:
                x = 0;
                y = SimulatorUtil.random.nextInt(config.size);
                break;
            case WEST:
                x = config.size - 1;
                y = SimulatorUtil.random.nextInt(config.size);
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
        System.out.println("colided at: "+aircraft.getField().toString()+" planes: "+colider.getId()+" , "+aircraft.getId());
        new Alert(aircraft,colider).createAlertFile();
    }

    public Configuration getConfig() {
        return config;
    }

    private void changeCourseOfFlights() {
        aircrafts.stream().forEach(e -> e.findClosestExit());
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
        else if (aircraft.direction == AircrafWrapper.Direction.SOUTH && aircraft.field.getX() == config.size - 1)
            generateHunter(new Field(config.size - 1,1),aircraft);
        else if (aircraft.direction == AircrafWrapper.Direction.EAST && aircraft.field.getX() == config.size - 1)
            generateHunter(new Field(config.size - 2,0),aircraft);
        else if (aircraft.direction == AircrafWrapper.Direction.WEST && aircraft.field.getY() == 0)
            generateHunter(new Field(1,config.size - 1),aircraft);
        else if (aircraft.direction == AircrafWrapper.Direction.NORTH && aircraft.field.getY() == 0)
            generateHunter(new Field(0,config.size - 2),aircraft);
        else if (aircraft.direction == AircrafWrapper.Direction.WEST && aircraft.field.getY() == config.size - 1)
            generateHunter(new Field(config.size - 1,config.size - 2),aircraft);
        else if (aircraft.direction == AircrafWrapper.Direction.SOUTH && aircraft.field.getY() == config.size - 1)
            generateHunter(new Field(config.size - 2,config.size - 1),aircraft);
        
        
    }

    private void addTwoHunters(AircrafWrapper aircraft) {
        if(null != aircraft.direction)switch (aircraft.direction) {
            case NORTH:
                generateHunter(new Field(aircraft.field.getX()-1,0),aircraft);
                generateHunter(new Field(aircraft.field.getX()+1,0),aircraft);
                break;
            case SOUTH:
                generateHunter(new Field(aircraft.field.getX()-1,config.size - 1),aircraft);
                generateHunter(new Field(aircraft.field.getX()+1,config.size - 1),aircraft);
                break;
            case EAST:
                generateHunter(new Field(0,aircraft.field.getY() - 1),aircraft);
                generateHunter(new Field(0,aircraft.field.getY() + 1),aircraft);
                break;
            case WEST:
                generateHunter(new Field(config.size - 1,aircraft.field.getY() - 1),aircraft);
                generateHunter(new Field(config.size - 1,aircraft.field.getY() + 1),aircraft);
                break;
            default:
                break;
        }
    }

    private void generateHunter(Field field,AircrafWrapper aircraft) {
        AircrafWrapper wrapper = new AircrafWrapper(this,field,aircraft);
        System.out.println("starting hunter: "+wrapper.getId() + " " + wrapper.direction.toString());
        wrapper.start();
        aircrafts.add(wrapper);
    }
    
    
    //TODO: FIX REDIRECTION
    //TODO: ADD CRASH REPORT (ALERT OBJECT)
    //TODO: READ CRASH REPORT AND ALERT
    //TODO: CREATE EVENT LIST SCENE
    //TODO: CREATE EVENT WATCHER
    //TODO: CREATE CRASH WATCHER
    
}
