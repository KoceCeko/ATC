/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.aircraft;

import java.util.HashMap;
import java.util.HashSet;
import model.person.Person;
import model.person.Value;

/**
 *
 * @author cekov
 */
public class Aircraft {
    
    protected String modelName;
    
    protected Integer id;
    
    protected Integer height;
    
    protected Integer speed;
    
    protected HashMap<Integer,Value> characteristic;
    
    protected HashSet<Person> persons;
    
}
