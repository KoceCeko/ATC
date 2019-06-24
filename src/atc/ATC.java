/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc;

import atc.flightRadar.Radar;
import atc.simulator.Simulator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 *
 * @author cekov
 */
public class ATC extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage startingStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("\\flightRadar\\flightRadar.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        startingStage.setScene(scene);
        startingStage.show();
        System.out.println("test");
    }
    
}
