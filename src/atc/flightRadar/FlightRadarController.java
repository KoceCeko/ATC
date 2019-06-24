/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.flightRadar;

import atc.simulator.AircrafWrapper;
import atc.simulator.Configuration;
import atc.util.SimulatorUtil;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class FlightRadarController implements Initializable{

    HashSet<AircrafWrapper> aircrafts;
    
    @FXML
    private AnchorPane anchorPane;
    
    @FXML
    private Button openListBtn;

    @FXML
    private ToggleButton toggleUnwanted;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Initialized!");
        Configuration config = SimulatorUtil.readConfiguration();
        GridPane map = new GridPane();
        
        System.out.println("config size: "+config.size.toString());
        
        
        for(int i =0;i < config.size;i++){
            for(int j = 0; j < config.size; j++){
                FlowPane field = new FlowPane();
                field.setPrefHeight(100);
                field.setPrefWidth(100);
                Color c = Color.rgb(255 - i*2, 255-j*2, 255-(i+j));
                BackgroundFill fill = new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY);
                field.setBackground(new Background(fill));
                map.add(field, i, j);
            }
        }
        
        
        ((FlowPane) getNodeFromGridPane(map,5, 5)).setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
        
        map.setPrefHeight(450);
        map.setPrefWidth(450);
        
        
        anchorPane.getChildren().add(map);
    }
    
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }
    
    
}
