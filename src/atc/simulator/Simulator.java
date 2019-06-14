/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.simulator;

import atc.util.Field;
import atc.util.SimulatorMatrix;
import atc.util.SimulatorUtil;
import javafx.util.Pair;

/**
 *
 * @author cekov
 */
public class Simulator extends Thread {
    
    //SIZE OF MATRIX SIZE*SIZE
    public static int size = 10;
    
    public static int i = 0;
    
    private SimulatorMatrix matrix; 
    
    public Simulator(){
        matrix = new SimulatorMatrix(10,10);
        System.out.println("created simulation");
        setDaemon(true);
    }
    
    @Override
    public void run(){
        System.out.println("started simulation");
        AircrafWrapper aircraft = new AircrafWrapper(this);
        aircraft.start();
        while(i < 100){
            
            System.out.println("sleeping for random sec between 1 and 5");
            Integer sleepSec = SimulatorUtil.random.nextInt(4);
            i+= sleepSec;
            
            try{
                Thread.sleep(sleepSec*1000);
            }catch(InterruptedException ex){
                
                System.out.println("runtime error at sleep random between 1 and 5");
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

    Field getStartingField() {
        Field f = matrix.getMap().stream().parallel().filter(e -> e.equals(new Field(1, 0))).findFirst().get();
        System.out.println("starting Field: "+f.toString());
        return f;
    }
}
