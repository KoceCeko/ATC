/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.flightRadar;

import atc.simulator.AircrafWrapper;
import atc.simulator.Configuration;
import atc.util.SimulatorUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import model.aircraft.FireFight;
import model.aircraft.Helicopter;
import model.aircraft.MilitaryAircraft;
import model.aircraft.PilotlessPlane;

public class FlightRadarController extends Thread implements Initializable{

    private GridPane map;
    
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
        map = new GridPane();
        
        System.out.println("config size: "+config.size.toString());
        
        
        for(int i =0;i < config.size;i++){
            for(int j = 0; j < config.size; j++){
                FlowPane field = new FlowPane();
                field.setPrefHeight(100);
                field.setPrefWidth(100);
                Color c = Color.rgb(255, 255 - i*2, 255-j*2);
                BackgroundFill fill = new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY);
                field.setBackground(new Background(fill));
                map.add(field, i, j);
            }
        }
        
        
        ((FlowPane) getNodeFromGridPane(map,5, 5)).setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
        
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
        }else if(ac instanceof  FireFight){
            c = Color.RED;
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
        
        final Path atcPath = FileSystems.getDefault().getPath("C:\\Users\\cekov\\OneDrive\\Documents\\NetBeansProjects\\ATC");
        final Path eventPath = FileSystems.getDefault().getPath("C:\\Users\\cekov\\OneDrive\\Documents\\NetBeansProjects\\ATC\\events");
        System.out.println(atcPath);
        try ( WatchService watchService = FileSystems.getDefault().newWatchService()) {
            WatchKey watchKey = atcPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                final WatchKey wk = watchService.take();
                
                List<WatchEvent<?>> events = wk.pollEvents();
                if(!events.isEmpty()){
                    for (WatchEvent<?> event : events) {
                    //we only register "ENTRY_MODIFY" so the context is always a Path.
                        final Path changed = (Path) event.context();
                        System.out.println(changed);
                        if (changed.endsWith("map.txt")) {
                            System.out.println("map updated");
                            HashSet<AircrafWrapper> aircrafts = deserializeMap();
                            if (aircrafts != null)
                                updateTable(aircrafts);
                        }
                }
                    
                    
                }
                // reset the key
                boolean valid = wk.reset();
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
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("map.txt")));
            return (HashSet<AircrafWrapper>) ois.readObject();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
        }
    return null;
    }

    private void updateTable(HashSet<AircrafWrapper> aircrafts) {
        for(AircrafWrapper wrapper : aircrafts){
            int x = wrapper.getField().getX();
            int y = wrapper.getField().getY();
            
            ((FlowPane)getNodeFromGridPane(map, x, y)).setBackground(getBackgroundFromAircraft(wrapper));
            System.out.println("updated color");
            
        }
    }
}
