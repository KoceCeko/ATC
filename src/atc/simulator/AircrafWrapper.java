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
import java.util.Objects;
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
    
    private Aircraft aircraft;
    
    protected transient  Simulator simulator;
    
    protected Field field;

    private transient boolean isHunter;
    
    private transient AircrafWrapper chasedAircraft;
    
    public AircrafWrapper(Simulator simulator, Field field,AircrafWrapper chasedAircraft) {
        isHunter = true;
        this.chasedAircraft = chasedAircraft;
        this.simulator = simulator;
        this.field = field;
        this.direction = chasedAircraft.direction;
        aircraft = new AirHunter();
        finished = false;
    }

    private void addRandomValues(Aircraft generated) {
        generated.setHeight(SimulatorUtil.getRandomHight());
        generated.setSpeed(SimulatorUtil.getRandomSpeed());
    }
    
    public enum Direction{
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
    
    protected Direction direction;
    
    protected boolean finished;
    
    public AircrafWrapper(Simulator simulator){
        this.simulator = simulator;
        aircraft = generateAircraft();
        if (aircraft == null){
            aircraft = new CommercialAirplane();
        }
        
        direction = generateDirection();
        field = simulator.getStartingField(direction);
        isHunter = false;
        finished = false;
    }
    
    public AircrafWrapper(Simulator simulator, boolean hasForeignAircraft) {
        this.simulator = simulator;
        if (hasForeignAircraft)
            aircraft = generateMilitaryAircraft();
        else
            aircraft = generateAircraft();
        
        direction = generateDirection();
        field = simulator.getStartingField(this.direction);
        finished = false;
        isHunter = false;
    }
        
    
    @Override
    public void run(){
        while(!finished){
            if(isHunter)
                chase();
            else
                move();
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AircrafWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("finished aircraft: "+this.getId());
        simulator.removeAircraft(this);
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
    private synchronized void chase() {
        if (chasedAircraft.finished || chasedAircraft == null){
            isHunter = false;
            move();
            return;
        }
        
        if (field.nextToField(chasedAircraft.field,direction)){
            chasedAircraft.finished = true;
            System.out.println("DEAD: "+chasedAircraft.field.toString()+" "+field.toString());
            isHunter = false;
        }
        move();
        
        //todo: fix chasing
    }
        
    private synchronized void move() {
        if (field != null){
            field.removeAircraft(this);
            field = simulator.getNextFiled(this);
            if (field!=null){
                if (field.addAircraft(this))
                    simulator.planesColided(this);
            }else
                finish();
        }else{
            finish();
        }
        
    }
    
    private Aircraft generateAircraft() {
        
        Aircraft generated = null;
        
        Integer random = SimulatorUtil.random.nextInt(7);
        switch(random){
            case 0:
                generated = new CommercialAirplane();
                break;
            case 1:
                generated = new CommercialHelicopter();
                break;
            case 2:
                generated = new PilotlessPlane();
                break;
            case 3:
                generated = new AirTanker();
                break;
            case 4:
                generated = new CargoHelicopter();
                break;
            case 5:
                generated = new CargoPlane();
                break;
            case 6:
                generated = new FireHelicopter();
                break;
        }
        
        if(simulator.getConfig().random)
            addRandomValues(generated);
        return generated;
    }
    
    private Aircraft generateMilitaryAircraft() {
      return new AirHunter();
    }
    
    
    public AircrafWrapper finish() {
        finished = true;
        return this;
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
        if(field == null){
            return;
        }
        
        Integer toNorth = simulator.getConfig().sizeY - field.getY();
        Integer toSouth = field.getY();
        Integer toEast = simulator.getConfig().sizeX - field.getX();
        Integer toWest = field.getY();
        
        if((toNorth <= toSouth) && (toNorth <= toEast) && (toNorth <= toWest))
            direction = Direction.NORTH;
        else if((toSouth < toNorth) && (toSouth <= toEast) && (toSouth <= toWest))
            direction = Direction.SOUTH;
        else if((toEast < toNorth) && (toEast < toSouth) && (toEast < toWest))
            direction = Direction.EAST;
        else
            direction = Direction.WEST;
        
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.aircraft);
        hash = 13 * hash + Objects.hashCode(this.direction);
        return hash;
    }

    

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AircrafWrapper other = (AircrafWrapper) obj;
        if (!Objects.equals(this.aircraft, other.aircraft)) {
            return false;
        }
        if (this.direction != other.direction) {
            return false;
        }
        return true;
    }
}
