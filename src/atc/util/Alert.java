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
public class Alert implements Serializable{
    
    static int i = 0;
    
    AircrafWrapper first;
    
    AircrafWrapper secound;
    
    Date ocuredDate;
    
    public Alert(AircrafWrapper first,AircrafWrapper secound){
        this.first = first;
        this.secound = secound;
        ocuredDate = new Date();
        System.out.println(ocuredDate.toString());
    }
    
    public void createAlertFile(){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("alert\\crashReport_No_"+String.valueOf(i++)+".alt"));
            oos.writeObject(this);
        } catch (IOException ex) {
            Logger.getLogger(Alert.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Date getDate() {
        return ocuredDate;
    }
}
