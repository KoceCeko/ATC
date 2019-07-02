/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.util;

/**
 *
 * @author cekov
 */
public class Configuration {
    
    
    public Integer sizeX;
    
    public Integer sizeY;
    
    public Integer creationTime;
    
    public boolean hasForeignAircraft;
    
    public boolean stoped;
    
    public boolean random;
    
    public Integer numerOfMaxAC;
    

    public Configuration() {
        
        sizeX = 10;
        
        sizeY = 10;
        
        numerOfMaxAC = 5;
        
        hasForeignAircraft = false;
        
        creationTime = 1;
        
        random = false;
    }
    
    public Configuration(Integer sizeX,Integer sizeY,Integer  creationTime , boolean hasForeignAircraft, Integer numberOfMaxAC,boolean stoped,boolean random) {
        this.numerOfMaxAC = numberOfMaxAC;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.creationTime = creationTime;
        this.hasForeignAircraft = hasForeignAircraft;
        this.stoped = stoped;
        this.random = random;
    }
}
