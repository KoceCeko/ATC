/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.util;

import java.util.HashSet;

/**
 *
 * @author Ceko
 */
public class SimulatorMatrix {

    private Integer xSize;
    
    private Integer ySize;
    
    private HashSet<Field> map;

    public SimulatorMatrix() {
        xSize = 5;
        ySize = 5;
        
        initFields();
    }
    
    public SimulatorMatrix(Integer xSize,Integer ySize){
        this.xSize = xSize;
        this.ySize = ySize;
        
        initFields();
      
    }
    
    public Integer getXSize() {
        return xSize;
    }

    public void setXSize(Integer xSize) {
        this.xSize = xSize;
    }

    public Integer getYSize() {
        return ySize;
    }

    public void setYSize(Integer y) {
        this.ySize = ySize;
    }

    private void initFields() {  
        
        map = new HashSet<>();
        
        for(int i=0; i < xSize; i++){
            for (int j=0 ; j< ySize; j++){
                map.add(new Field(i,j));
            }
        }
    }
    
}
