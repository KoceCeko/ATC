/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.aircraft;

import java.util.ArrayList;
import model.person.Cargo;

/**
 *
 * @author Ceko
 */
public class CargoPlane extends Airplane implements Carrier{
    
    protected ArrayList<Cargo> cargo;
    
    protected Integer maxCargoWeight;

    public CargoPlane() {
    }
    
    
}
