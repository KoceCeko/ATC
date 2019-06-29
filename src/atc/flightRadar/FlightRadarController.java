/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.flightRadar;

import atc.simulator.AircrafWrapper;
import atc.simulator.Configuration;
import atc.util.Alert;
import atc.util.Field;
import atc.util.SimulatorUtil;
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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.paint.Color;
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
        map = new GridPane();
        oldPositions  = new HashMap<>();
        System.out.println("config size: "+config.size.toString());
        
        setButtonListeners();
        
        for(int i =0;i < config.size;i++){
            for(int j = 0; j < config.size; j++){
                FlowPane field = new FlowPane();
                field.setPrefHeight(100);
                field.setPrefWidth(100);
                Color c = Color.rgb(255-i*2, 255 - j*2, 255);
                BackgroundFill fill = new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY);
                field.setBackground(new Background(fill));
                map.add(field, i, j);
            }
        }
        
        map.setPrefHeight(500);
        map.setPrefWidth(500);
        
        
        anchorPane.getChildren().add(map);
        setDaemon(true);
        start();
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

    @Override
    public void run(){
        setDirectoryListener();
    }
    
    private void setDirectoryListener(){
        
        final Path atcPath = FileSystems.getDefault().getPath("");
        final Path alertPath = FileSystems.getDefault().getPath("alert");
        try ( WatchService watchService = FileSystems.getDefault().newWatchService()) {
            WatchService watchServiceAlert = FileSystems.getDefault().newWatchService();
            WatchKey watchKey = atcPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            WatchKey alertsKey = alertPath.register(watchServiceAlert, StandardWatchEventKinds.ENTRY_CREATE);
            while (true) {
                
                List<WatchEvent<?>> events = watchKey.pollEvents();
                events.addAll(alertsKey.pollEvents());
                boolean readable = false;
                if(!events.isEmpty()){
                    for (WatchEvent<?> event : events) {
                        final Path changed = (Path) event.context();
                        System.out.println("event: "+ changed.toString());
                        if (changed.endsWith("map.txt")) {
                            readable = true;
                        }else if (changed.toString().contains(".alt")){
                            System.out.println(event.kind().toString());
                            Alert alert = deserializeAlert(changed);
                            if (alert != null)
                                System.out.println(alert.getDate().toString());
                        }
                    }
                }
                
                Thread.sleep(50);
                // reset the key
                boolean valid = watchKey.reset();
                valid = alertsKey.reset() || valid;
                if(readable){
                    HashSet<AircrafWrapper> aircrafts = deserializeMap();
                    if (aircrafts != null)
                        updateTable(aircrafts);
                }
                if (!valid) {
                    System.out.println("Key has been unregisterede");
                }
            }
        } catch(IOException ioex){
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ioex);
        } catch (InterruptedException ex) {
                Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private HashSet<AircrafWrapper> deserializeMap() {
        
        try {
            
            
            Path p = Path.of("map.txt");
            while(!Files.isReadable(p))
            {
                System.out.println("ITS NOT READABLE");
                try {
                    sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            FileInputStream fis = new FileInputStream(p.toFile());
            ois = new ObjectInputStream(fis);
            aircrafts = (HashSet<AircrafWrapper>) ois.readUnshared();
            //todo: fix syncronious reading/writing
            return aircrafts;
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        }catch (Exception ex){
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        }
    return null;
    }

    private void updateTable(HashSet<AircrafWrapper> aircrafts) {
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
        ((FlowPane)getNodeFromGridPane(map, position.getX(), position.getY())).setBackground(getBackgroundColor(position));
    }

    private Background getBackgroundColor(Field position) {
        
        return new Background(new BackgroundFill(Color.rgb(255-position.getX()*2, 255-position.getY()*2, 255), CornerRadii.EMPTY, Insets.EMPTY));
    }

    private void setButtonListeners() {
       
        openListBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                System.out.println("BUTTON PRESSED");
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
        
    }

    private Alert deserializeAlert(Path changed) {
        try {
            return (Alert) (new ObjectInputStream(new FileInputStream(changed.toFile()))).readObject();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.FINE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.FINE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.FINE, null, ex);
        }catch (Exception ex){
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
