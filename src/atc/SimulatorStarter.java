/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc;

import atc.flightRadar.Radar;
import java.util.Scanner;

/**
 *
 * @author cekov
 */
public class SimulatorStarter {
    
    public static void main(String []args){
               
        atc.simulator.Simulator simulator = new atc.simulator.Simulator();
        simulator.start();
        Radar radar = new Radar(simulator.getMatrix());
        radar.start();
        
        while(true){
            if ((new Scanner(System.in).next()).equals("exit")){
                break;
            }
        }
    }
}
