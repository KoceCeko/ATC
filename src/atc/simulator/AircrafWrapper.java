/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.simulator;

import model.aircraft.Aircraft;
import atc.util.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.aircraft.Airplane;
import model.aircraft.CommercialAirplane;

/**
 *
 * @author Ceko
 */
public class AircrafWrapper extends Thread{
    
    protected Aircraft aircraft;
    
    protected Simulator simulator;
    
    protected Field field;
    
    public AircrafWrapper(Simulator simulator){
        aircraft = new CommercialAirplane();
        this.simulator = simulator;
        field = simulator.getStartingField();
        System.out.println("field created: "+field.toString());
        System.out.println("created wrapper");
    }
    
    
    @Override
    public void run(){
        
        System.out.println("started wrapper");
        Integer i =0;
        
        for (i =0; i<3; i++){
            move();
            try {
                sleep(1250);
            } catch (InterruptedException ex) {
                Logger.getLogger(AircrafWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public Aircraft getAircraft(){
        return aircraft;
    }   
    
    public Field getField() {
        
        return field;
    }

    private void move() {
        field = simulator.getNextFiled(this);
        System.out.println("moved to: "+field.toString());
    }
}
