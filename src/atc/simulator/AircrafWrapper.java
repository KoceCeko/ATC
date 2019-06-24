/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.simulator;

import model.aircraft.Aircraft;
import atc.util.Field;
import atc.util.SimulatorUtil;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.aircraft.AirHunter;
import model.aircraft.AirTanker;    
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
public class AircrafWrapper extends Thread implements Serializable{
    
    protected Aircraft aircraft;
    
    protected transient  Simulator simulator;
    
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
        direction = generateDirection();
      

        System.out.println("direction: "+direction.toString()+" id: "+getId());
        field = simulator.getStartingField(direction);
    }
    
    AircrafWrapper(Simulator simulator, boolean hasForeignAircraft) {
        if (hasForeignAircraft)
            aircraft = generateMilitaryAircraft();
        else
            aircraft = generateAircraft();
        this.simulator = simulator;
        finished = false;
        direction = generateDirection();
    }
        
    
    @Override
    public void run(){
        
        while(!finished){
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

    
    
    private synchronized void move() {
        if (field != null){
            field.removeAircraft(this);
            field = simulator.getNextFiled(this);
            if (field!=null){
                if (field.addAircraft(this))
                    simulator.planesColided(this);
                System.out.println("moved to: "+field.toString());
            }else
                finished = true;
        }else{
            finished = true;
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
    
    private Aircraft generateMilitaryAircraft() {
      return new AirHunter();
    }
    
    
    void finish() {
        finished = true;
    }
    
        private Direction generateDirection() {
        Direction direction = Direction.EAST;
        Integer i = SimulatorUtil.random.nextInt(4);
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
        return direction;
    }
        
    void findClosestExit() {
        
        if(field == null)
            return;
        
        Integer toNorth = simulator.getConfig().size - field.getY();
        Integer toSouth = simulator.getConfig().size - field.getY();
        Integer toEast = simulator.getConfig().size - field.getY();
        Integer toWest = simulator.getConfig().size - field.getY();
        
        if((toNorth >= toSouth) && (toNorth >= toEast) && (toNorth >= toWest))
            direction = Direction.NORTH;
        else if((toSouth > toNorth) && (toSouth >= toEast) && (toSouth >= toWest))
            direction = Direction.SOUTH;
        else if((toEast > toNorth) && (toEast > toSouth) && (toEast >= toWest))
            direction = Direction.EAST;
        else
            direction = Direction.WEST;
        
    }
}
