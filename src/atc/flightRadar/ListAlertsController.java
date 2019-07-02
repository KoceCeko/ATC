/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.flightRadar;

import atc.util.CrashAlert;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author cekov
 */
public class ListAlertsController implements Initializable {

    
    @FXML
    private TableView<CrashAlert> eventsTable;

    @FXML
    private TableColumn<CrashAlert, String> idsColumn;

    @FXML
    private TableColumn<CrashAlert, String> positionColumn;

    @FXML
    private TableColumn<CrashAlert, Integer> hightColumn;

    @FXML
    private TableColumn<CrashAlert, String> dateColumn;

    private HashSet<CrashAlert> alerts;
    
    public ListAlertsController(HashSet<CrashAlert> alerts) {
        this.alerts = alerts;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populateTable();
    }    

    private void populateTable() {
        ObservableList<CrashAlert> observableList= FXCollections.observableArrayList();
        try{
            observableList.addAll(alerts);
            
        }catch (Exception ex){
            java.util.logging.Logger.getLogger(CrashAlert.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        eventsTable.setItems(observableList);
        idsColumn.setCellValueFactory((CellDataFeatures<CrashAlert, String> atce) -> new SimpleObjectProperty<String>(String.valueOf(atce.getValue().getFirst().getId())+" : "+String.valueOf(atce.getValue().getSecound().getId())));
        hightColumn.setCellValueFactory((CellDataFeatures<CrashAlert, Integer> atce) -> new SimpleObjectProperty<Integer>(atce.getValue().getFirst().getAircraft().getHeight()));
        positionColumn.setCellValueFactory((CellDataFeatures<CrashAlert, String> atce) -> new SimpleObjectProperty<String>(atce.getValue().getField().toString()));
        dateColumn.setCellValueFactory((CellDataFeatures<CrashAlert, String> atce) -> new SimpleObjectProperty<String>(atce.getValue().getOcuredDate()));
        
    }
    
}
