/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.aircraft;

/**
 *
 * @author Ceko
 */
public class CommercialAirplane extends Airplane implements Commercial{
    
    protected Integer numberOfSeats = 50;
    
    protected Integer maxLuggageWeight = 5;

    public CommercialAirplane() {
        
    }
    
    
    
}
