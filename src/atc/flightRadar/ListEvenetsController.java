package atc.flightRadar;

import atc.util.ATCEvent;
import atc.simulator.AircrafWrapper;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class ListEvenetsController implements Initializable{

    @FXML
    private TableView<ATCEvent> eventsTable;

    @FXML
    private TableColumn<ATCEvent, Integer> idColumn;

    @FXML
    private TableColumn<ATCEvent, String> positionColumn;

    @FXML
    private TableColumn<ATCEvent, Integer> hightColumn;

    @FXML
    private TableColumn<ATCEvent, String> dateColumn;
    
    private HashSet<ATCEvent> events;

    ListEvenetsController(HashSet<ATCEvent> events){
        this.events = events;
    }
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        populateTable();
    }


    private void populateTable() {

        ObservableList<ATCEvent> observableList= FXCollections.observableArrayList();
        try{
            observableList.addAll(events);
        }catch(Exception ex){
            Logger.getLogger(FlightRadarController.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        eventsTable.setItems(observableList);
        idColumn.setCellValueFactory((CellDataFeatures<ATCEvent, Integer> atce) -> new SimpleObjectProperty<Integer>(atce.getValue().getId()));
        hightColumn.setCellValueFactory((CellDataFeatures<ATCEvent, Integer> atce) -> new SimpleObjectProperty<Integer>(atce.getValue().getWrapper().getAircraft().getHeight()));
        positionColumn.setCellValueFactory((CellDataFeatures<ATCEvent, String> atce) -> new SimpleObjectProperty<String>(atce.getValue().getWrapper().getField().toString()));
        dateColumn.setCellValueFactory((CellDataFeatures<ATCEvent, String> atce) -> new SimpleObjectProperty<String>(atce.getValue().getDate()));
        
    }
    
}
