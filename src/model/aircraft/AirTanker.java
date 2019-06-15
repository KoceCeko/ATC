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
public class AirTanker extends Airplane implements FireFight{
    
    public Boolean isFirefighting = false;
    
    public Integer waterCapacity = 0;

    public AirTanker() {
    }
    
    
}
