/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc;

import atc.flightRadar.Radar;
import atc.simulator.Simulator;
import atc.util.Alert;
import atc.util.SimulatorUtil;
import java.util.Scanner;
import jdk.jshell.execution.Util;

/**
 *
 * @author cekov
 */
public class SimulatorStarter {
    
    public static void main(String []args){
               
        Simulator simulator = new Simulator();
        Radar radar = new Radar(simulator);
        simulator.start();
        radar.start();
        new Alert(null,null).createAlertFile();
        while(true){
            if ((new Scanner(System.in).next()).equals("exit")){
                break;
            }
        }
    }
}
