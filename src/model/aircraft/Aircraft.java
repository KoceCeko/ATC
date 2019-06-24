/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.aircraft;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import model.person.Person;
import model.person.Value;

/**
 *
 * @author cekov
 */
public abstract class Aircraft implements Serializable{
    
    protected String modelName;
    
    public Integer id;
    
    private static int numOfAC;
    
    protected Integer height;
    
    protected Integer speed;
    
    protected HashMap<Integer,Value> characteristic;
    
    protected HashSet<Person> persons;

    public Aircraft() {
        id = numOfAC++;
        modelName = "default";
        height = 100;
        speed = 1;
        characteristic = new HashMap<>();
        persons = new HashSet<>();
    }
    
    public Aircraft(String modelName, Integer height, Integer speed, HashMap<Integer, Value> characteristic, HashSet<Person> persons) {
        id = numOfAC++;
        this.modelName = modelName;
        this.height = height;
        this.speed = speed;
        this.characteristic = characteristic;
        this.persons = persons;
    }

    public String getModelName() {
        return modelName;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getSpeed() {
        return speed;
    }

    public HashMap<Integer, Value> getCharacteristic() {
        return characteristic;
    }

    public HashSet<Person> getPersons() {
        return persons;
    }

}
