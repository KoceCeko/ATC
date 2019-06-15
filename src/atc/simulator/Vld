/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.simulator;

import model.aircraft.Aircraft;
import atc.util.Field;
import atc.util.SimulatorUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.aircraft.AirTanker;
import model.aircraft.Airplane;
import model.aircraft.CargoHelicopter;
import model.aircraft.CargoPlane;
import model.aircraft.CommercialAirplane;
import model.aircraft.CommercialHelicopter;
import model.aircraft.FireHelicopter;
import model.aircraft.PilotlessPlane;

/**
 *
 * @author Ceko
 */
public class AircrafWrapper extends Thread{
    
    protected Aircraft aircraft;
    
    protected Simulator simulator;
    
    protected Field field;

    
    public enum Direction{
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
    
    protected Direction direction;
    
    protected boolean finished;
    
    public AircrafWrapper(Simulator simulator){
        aircraft = generateAircraft();
        this.simulator = simulator;
        finished = false;
        Integer i = SimulatorUtil.random.nextInt(4);
        System.out.println("direction random: "+i.toString());
        switch(i){
            case 0:
                direction = Direction.NORTH;
                break;
            case 1:
                direction = Direction.SOUTH;
                break;
            case 2:
                direction = Direction.EAST;
                break;
            case 3:
                direction = Direction.WEST;
                break;
        }
        System.out.println("direction: "+direction.toString());
        field = simulator.getStartingField(direction);
    }
    
    
    @Override
    public void run(){
        
        System.out.println("started wrapper");
        Integer i =0;
        
        for (i =0; i<9; i++){
            move();
            try {
                sleep(1250);
            } catch (InterruptedException ex) {
                Logger.getLogger(AircrafWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("finisehed!");
    }
    
    public Aircraft getAircraft(){
        return aircraft;
    }   
    
    public Field getField() {
        
        return field;
    }

    public Direction getDirection() {
        return direction;
    }

    
    
    private void move() {
        field = simulator.getNextFiled(this);
        if (field != null)
            System.out.println("moved to: "+field.toString());
        else{
            finished = true;
            System.err.println("finished");
        }
    }
    
    private Aircraft generateAircraft() {
        
        Aircraft generated = null;
        
        Integer random = SimulatorUtil.random.nextInt(5);
        switch(random){
            case 0:
                generated = new CommercialAirplane();
                break;
            case 1:
                generated = new CommercialHelicopter();
                break;
            case 3:
                generated = new PilotlessPlane();
                break;
            case 4:
                generated = new AirTanker();
                break;
            case 5:
                generated = new CargoHelicopter();
                break;
            case 6:
                generated = new CargoPlane();
                break;
            case 7:
                generated = new FireHelicopter();
                break;
        }
        
        
        return  new CommercialAirplane();
        
    }
}
