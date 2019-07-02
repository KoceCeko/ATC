/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.util;

import atc.simulator.AircrafWrapper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cekov
 */
public class CrashAlert implements Serializable{
    
    static int i = 0;
    
    AircrafWrapper first;
    
    AircrafWrapper secound;
    
    String ocuredDate;
    
    public CrashAlert(AircrafWrapper first,AircrafWrapper secound){
        this.first = first;
        this.secound = secound;
        ocuredDate = new Date().toString();
        System.out.println(ocuredDate.toString());
    }
    
    public void createAlertFile(){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("alert\\crashReport_No_"+String.valueOf(i++)+".alt"));
            oos.writeUnshared(this);
            oos.flush();
            oos.close();
        } catch (IOException ex) {
            Logger.getLogger(CrashAlert.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getDate() {
        return ocuredDate;
    }
    
    public Field getField(){
        return first.getField();
    }

    public String getParticipantsIds() {
        return String.valueOf(first.getId())+" , "+String.valueOf(secound.getId());
    }

    public AircrafWrapper getFirst() {
        return first;
    }

    public AircrafWrapper getSecound() {
        return secound;
    }

    public String getOcuredDate() {
        return ocuredDate;
    }
    
}
