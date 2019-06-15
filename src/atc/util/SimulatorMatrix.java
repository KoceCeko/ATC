/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.util;

import atc.simulator.AircrafWrapper;
import atc.simulator.Simulator;
import java.util.HashMap;
import java.util.HashSet;
import javafx.util.Pair;

/**
 *
 * @author Ceko
 */
public class SimulatorMatrix {

    private Integer xSize;
    
    private Integer ySize;
    
    private HashSet<Field> map;
    
    private Simulator simulator;

    public SimulatorMatrix() {
        xSize = 5;
        ySize = 5;
        
        initFields();
    }
    
    public SimulatorMatrix(Integer xSize,Integer ySize,Simulator simulator){
        this.xSize = xSize;
        this.ySize = ySize;
        this.simulator = simulator;
        initFields();
      
    }
    
    public Integer getXSize() {
        return xSize;
    }

    public void setXSize(Integer xSize) {
        this.xSize = xSize;
    }

    public Integer getYSize() {
        return ySize;
    }

    public void setYSize(Integer y) {
        this.ySize = ySize;
    }

    private void initFields() {  
        
        map = new HashSet<>();
        
        for(int i=0; i < xSize; i++){
            for (int j=0 ; j< ySize; j++){
                map.add(new Field(i, j));
            }
        }
        System.out.println("finished creating fields");
    }

    public HashSet<Field> getMap() {
        return map;
    }
    
    public Field getNextField(AircrafWrapper wrapper){
        
        Integer nextX = 0;
        Integer nextY = 0;
        
        switch(wrapper.getDirection()){
            case NORTH:
                nextX = wrapper.getField().getX();
                nextY = wrapper.getField().getY()+wrapper.getAircraft().getSpeed();
                break;
            case SOUTH:
                nextX = wrapper.getField().getX();
                nextY = wrapper.getField().getY() - wrapper.getAircraft().getSpeed();
                break;
            case EAST:
                nextX = wrapper.getField().getX()+wrapper.getAircraft().getSpeed();
                nextY = wrapper.getField().getY();
                break;
            case WEST:
                nextX = wrapper.getField().getX()-wrapper.getAircraft().getSpeed();
                nextY = wrapper.getField().getY();
                break;
        }
        
        System.out.println("size: "+simulator.getConfig().size.toString());
        if ((nextX >= simulator.getConfig().size || nextX < 0 )|| (nextY >= simulator.getConfig().size || nextY < 0 ))
            return null;
        
        Field nextPosition = new Field(nextX, nextY);
        Field f = null;
        try {
            f = map.stream().parallel().filter(e -> e.equals(nextPosition)).findFirst().get();
            
        } catch (Exception e) {
            System.out.println("couldn't find a field for: "+nextPosition.toString());
        }
        return f;
    }
}
