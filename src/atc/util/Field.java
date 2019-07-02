/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.util;

import atc.simulator.AircrafWrapper;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;

/**
 *
 * @author Ceko
 */
public class Field implements Serializable{
    
    private Integer x;
    
    private Integer y;
    
    private transient HashSet<AircrafWrapper> aircrafts;

    public Field(Integer x, Integer y) {
        this.x = x;
        this.y = y;
        aircrafts = new HashSet<>();
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.x);
        hash = 83 * hash + Objects.hashCode(this.y);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Field other = (Field) obj;
        if (!Objects.equals(this.x, other.x)) {
            return false;
        }
        if (!Objects.equals(this.y, other.y)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Field{" + "x= " + x + ", y= " + y + '}';
    }

    public void removeAircraft(AircrafWrapper oldAircraft) {
        aircrafts.remove(oldAircraft);
    }

    public boolean addAircraft(AircrafWrapper newAircraft) {
        if (aircrafts.stream().anyMatch(e -> e.getAircraft().getHeight().equals(newAircraft.getAircraft().getHeight()) && !e.equals(newAircraft)))
            return true;
        
        aircrafts.add(newAircraft);
        return false;
        
    }

    public HashSet<AircrafWrapper> getAircrafts() {
        return aircrafts;
    }
    
    public synchronized AircrafWrapper removeAircraftsOnAltitude(Integer hight){
        for(AircrafWrapper wrapper : aircrafts){
            if (wrapper.getAircraft().getHeight().equals(hight)){
                AircrafWrapper ret = wrapper;
                aircrafts.remove(ret.finish());
                return ret;
            }
        }
        return null;
    }

    public boolean isAtCorrner() {
        if (x== 0 && y == 0)
            return true;
        else if(x == 0 && y == SimulatorUtil.sizeY-1)
            return true;
        else if (x == SimulatorUtil.sizeX -1 && y == 0)
            return true;
        else if (x == SimulatorUtil.sizeX -1 && y == SimulatorUtil.sizeY -1 )
            return true;
        return false;
    }

    public boolean nextToField(Field field, AircrafWrapper.Direction direction) {
        synchronized(AircrafWrapper.class){
        if (field == null)
            return true;
        if (direction == AircrafWrapper.Direction.NORTH || direction == AircrafWrapper.Direction.SOUTH){
            if (y.equals(field.getY()))
                return true;
        }else if (direction == AircrafWrapper.Direction.EAST || direction == AircrafWrapper.Direction.WEST)
            if (y.equals(field.getX()))
                return true;
        return false;
        }
    }
}
