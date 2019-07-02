/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.flightRadar;

import atc.ATC;
import atc.util.ATCEvent;
import atc.simulator.AircrafWrapper;
import atc.util.Configuration;
import atc.util.CrashAlert;
import atc.util.Field;
import atc.util.SimulatorUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.aircraft.Aircraft;
import model.aircraft.Airplane;
import model.aircraft.Helicopter;
import model.aircraft.MilitaryAircraft;
import model.aircraft.PilotlessPlane;

public class FlightRadarController extends Thread implements Initializable{

    private GridPane map;
    
    HashSet<AircrafWrapper> aircrafts;
    
    HashMap<AircrafWrapper,Field> oldPositions;
    
    
    ObjectInputStream ois;
    
    DirectoryWatcher watcher;
    
    @FXML
    private AnchorPane anchorPane;
    
    @FXML
    private Button openListBtn;
    
    @FXML
    private Button alertsBtn;

    @FXML
    private ToggleButton toggleUnwanted;
    
    @FXML
    private ToggleButton toggleStop;
    
    private HashSet<ATCEvent> events;
    
    private HashSet<CrashAlert> alerts;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Initialized!");
        Configuration config = SimulatorUtil.readConfiguration();
        map = new GridPane();
        this.oldPositions  = new HashMap<>();
        this.events = new HashSet<>();
        this.alerts = new HashSet<>();
        setButtonListeners();
        
        for(int i =0;i < config.sizeX;i++){
            for(int j = 0; j < config.sizeY; j++){
                FlowPane field = new FlowPane();
                field.setPrefHeight(100);
                field.setPrefWidth(100);
                Color c = Color.rgb(255, 255, 255);
                BackgroundFill fill = new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY);
                field.setBackground(new Background(fill));
                map.add(field, i, j);
            }
        }
        
        map.setPrefHeight(500);
        map.setPrefWidth(500);
        
        
        anchorPane.getChildren().add(map);
        setDaemon(true);
        
        watcher = new DirectoryWatcher(this);
        watcher.start();
    }
    
    
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }
    
    private Background getBackgroundFromAircraft(AircrafWrapper aircraft){
        
        Color c = Color.WHITE;
        Aircraft ac = aircraft.getAircraft();
        if(ac instanceof MilitaryAircraft){
            c = Color.GREEN;
        }else if (ac instanceof Helicopter){
            c = Color.BROWN;
        }else if (ac instanceof Airplane){
            c = Color.BLUE;
        }else if(ac instanceof PilotlessPlane){
            c = Color.GREY;
        }
        
        return new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY));
    }
    
    public void updateMap(HashSet<AircrafWrapper> aircrafts) {
        for(AircrafWrapper wrapper : aircrafts){
            
            
            if (oldPositions.containsKey(wrapper)){
                setOldColor(oldPositions.get(wrapper));
                oldPositions.remove(wrapper);
            }
            
            if(wrapper.getField() == null)
                continue;
            int x = wrapper.getField().getX();
            int y = wrapper.getField().getY();
            
            oldPositions.put(wrapper, wrapper.getField());
            
            ((FlowPane)getNodeFromGridPane(map, x, y)).setBackground(getBackgroundFromAircraft(wrapper));
        }
        for(AircrafWrapper onMap : oldPositions.keySet()){
            
            if(!aircrafts.contains(onMap)){
                    setOldColor(oldPositions.get(onMap));
            }
        }
    }

    private void setOldColor(Field position) {
        ((FlowPane)getNodeFromGridPane(map, position.getX(), position.getY())).setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private Background getBackgroundColor(Field position) {
        
        return new Background(new BackgroundFill(Color.rgb(255-position.getX()*2, 255-position.getY()*2, 255), CornerRadii.EMPTY, Insets.EMPTY));
    }

    private void setButtonListeners() {
       
        openListBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setController(new ListEvenetsController(events));
                    FileInputStream fis = new FileInputStream(new File("src\\atc\\flightRadar\\listEvents.fxml"));
                    Parent parent = loader.load(fis);
                    Scene scene = new Scene(parent);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.show();
                } catch (Exception ex) {
                    Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);  
                }
            }
        });
        
        alertsBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {                
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setController(new ListAlertsController(alerts));
                    FileInputStream fis = new FileInputStream(new File("src\\atc\\flightRadar\\listAlerts.fxml"));
                    Parent parent = loader.load(fis);
                    Scene scene = new Scene(parent);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.show();
                } catch (Exception ex) {
                    Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);  
                }
            }
        });
        
        toggleUnwanted.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                
                Properties prop = new Properties();
                System.out.println("foreign: " + toggleUnwanted.selectedProperty().get());
                try {
                    prop.load(new FileInputStream("config.properties"));
                    if (toggleUnwanted.selectedProperty().get())
                        prop.setProperty("hasForeignAircraft", "true");
                    else
                        prop.setProperty("hasForeignAircraft", "false");
                    prop.store(new FileOutputStream("config.properties"), null);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
                } catch(Exception ex){
                    Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
        
        toggleStop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                  
                Properties prop = new Properties();
                System.out.println("stoped: " + toggleStop.selectedProperty().get());
                try {
                    prop.load(new FileInputStream("config.properties"));
                    if (toggleStop.selectedProperty().get())
                        prop.setProperty("stoped", "true");
                    else
                        prop.setProperty("stoped", "false");
                    prop.store(new FileOutputStream("config.properties"), null);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
                } catch(Exception ex){
                    Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
    }

    void alert(CrashAlert alert) {
        alerts.add(alert);
        Platform.runLater(() ->{
            Alert a = new Alert(AlertType.WARNING);
            a.setTitle("Crash report");
            a.setHeaderText("A Crash ocured!");
            a.setContentText("position: "+alert.getField().toString()+" ::::: participants: "+alert.getFirst().getId()+" , "+alert.getSecound().getId());
            a.showAndWait();
        });
    }

    void addEvent(ATCEvent atce) {
        events.add(atce);
    }
}
