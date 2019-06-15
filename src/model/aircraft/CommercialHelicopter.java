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
public class CommercialHelicopter extends Helicopter implements Commercial{
    
    protected Integer numberOfSeats;

    public CommercialHelicopter() {
        super();
        numberOfSeats = 100;
    }
    
    
}
