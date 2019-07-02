/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.util;

import atc.simulator.AircrafWrapper;
import atc.util.CrashAlert;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cekov
 */
public class ATCEvent implements Serializable{
    
    public static int i=0;
    
    Integer id;
    
    String date;
    
    AircrafWrapper foreignAircraft;

    
    public ATCEvent(AircrafWrapper aircraft){
        foreignAircraft = aircraft;
        date = (new Date()).toString();
        id = i++;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.foreignAircraft);
        hash = 37 * hash + Objects.hashCode(this.date);
        return hash;
    }

   

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ATCEvent other = (ATCEvent) obj;
        if (!Objects.equals(this.foreignAircraft, other.foreignAircraft)) {
            return false;
        }
        return true;
    }

    public void writeToFile() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("events\\event_No_"+String.valueOf(id)+".evt"));
            oos.writeUnshared(this);
            oos.flush();
            oos.close();
        } catch (IOException ex) {
            Logger.getLogger(CrashAlert.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getDate() {
        return date;
    }

    public Integer getId() {
        return id;
    }
    
    public AircrafWrapper getWrapper() {
        return foreignAircraft;
    }
    
}
