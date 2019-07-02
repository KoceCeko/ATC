/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc;

import atc.flightRadar.Updater;
import atc.simulator.Simulator;
import atc.util.CompressFiles;
import atc.util.CrashAlert;
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
        Updater radar = new Updater(simulator);
        CompressFiles zipRoutine = new CompressFiles();
        simulator.start();
        radar.start();
        zipRoutine.start();
        while(true){
            if ((new Scanner(System.in).next()).equals("exit")){
                break;
            }
        }
    }
}
