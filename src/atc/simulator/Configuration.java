/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.simulator;

/**
 *
 * @author cekov
 */
public class Configuration {
    
    
    public Integer size;
    
    public Integer creationTime;
    
    public boolean hasForeignAircraft;
    
    public Integer numerOfMaxAC;

    public Configuration() {
        
        size = 10;
        
        numerOfMaxAC = 5;
        
        hasForeignAircraft = false;
        
        creationTime = 1;
    }
    
    public Configuration(Integer size,Integer  creationTime , boolean hasForeignAircraft, Integer numberOfMaxAC) {
        this.numerOfMaxAC = numberOfMaxAC;
        this.size = size;
        this.creationTime = creationTime;
        this.hasForeignAircraft = hasForeignAircraft;
    }
}
