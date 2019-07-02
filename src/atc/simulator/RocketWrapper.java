/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.simulator;

import model.rocket.Rocket;

/**
 *
 * @author cekov
 */
public class RocketWrapper extends AircrafWrapper{

    Rocket rocket;
    
    public RocketWrapper(Simulator simulator) {
        super(simulator);
    }
    
}
