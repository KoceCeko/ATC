/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.simulator;

import atc.util.Field;
import atc.util.SimulatorMatrix;
import atc.util.SimulatorUtil;
import java.util.HashSet;
import model.aircraft.AirHunter;

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
        
        matrix = new SimulatorMatrix(config.size,config.size,this);
        System.out.println("created simulation");
        aircrafts = new HashSet<>();
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
                System.err.println("foregin aircraft entered!");
                AircrafWrapper aircraft = new AircrafWrapper(this,config.hasForeignAircraft);
                changeCourseOfFlights();
                aircraft.start();
                alreadyEntered = true;
            }else{
                if (i++ < config.numerOfMaxAC && !alreadyEntered){
                    System.out.println("adding aircraft");
                    AircrafWrapper aircraft = new AircrafWrapper(this);
                    aircraft.start();
                    aircrafts.add(aircraft);
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
        
        Integer speed = aircrafWrapper.aircraft.getSpeed();
        
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
                x = SimulatorUtil.random.nextInt(10);
                y = 0;
                break;
            case SOUTH:
                x = SimulatorUtil.random.nextInt(10);
                y = 9;
                break;
            case EAST:
                x = 0;
                y = SimulatorUtil.random.nextInt(10);
                break;
            case WEST:
                x = 9;
                y = SimulatorUtil.random.nextInt(10);
                break;
        }
        Integer x1 = x;
        Integer y1 = y;
        Field f = matrix.getMap().stream().parallel().filter(e -> e.equals(new Field(x1, y1))).findFirst().get();
        System.out.println("starting Field: "+f.toString());
        return f;
    }

    public synchronized void planesColided(AircrafWrapper aircraft) {
        System.out.println("Colision detected at: "+aircraft.getField().toString());
        aircrafts.stream().forEach(ac -> ac.finish());

    }

    public Configuration getConfig() {
        return config;
    }

    private void changeCourseOfFlights() {
        aircrafts.stream().forEach(e -> e.findClosestExit());
        
    }
}
