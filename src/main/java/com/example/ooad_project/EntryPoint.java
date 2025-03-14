package com.example.ooad_project;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.example.ooad_project.API.GardenSimulationAPI;
import com.example.ooad_project.Parasite.Parasite;
import com.example.ooad_project.Parasite.ParasiteManager;
import com.example.ooad_project.SubSystems.PesticideSystem;
import com.example.ooad_project.SubSystems.TemperatureSystem;
import com.example.ooad_project.SubSystems.WateringSystem;
import com.example.ooad_project.ThreadUtils.ThreadManager;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class EntryPoint extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EntryPoint.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("OOAD - Project!");
        stage.setScene(scene) ;
        stage.show();

        initializeBackgroundServices();

        // Schedule API rain calls using JavaFX Timeline
        runAPIScheduledTasks();
    }

//    We should start all of our background services here
    private void initializeBackgroundServices() {
        Runnable wateringSystem = new WateringSystem();
        Runnable temperatureSystem = new TemperatureSystem();
        Runnable pesticideSystem = new PesticideSystem();
        DaySystem daySystem = DaySystem.getInstance();

        ThreadManager.run(wateringSystem);
        ThreadManager.run(temperatureSystem);
        ThreadManager.run(pesticideSystem);

    }


//    This is for testing the API
//    I assume Prof is going to do something similar
    private void runAPIScheduledTasks() {
        GardenSimulationAPI api = new GardenSimulationAPI();
        api.initializeGarden();
        Random rand = new Random();

//        This is for testing the parasites thread
        ParasiteManager parasiteManager = ParasiteManager.getInstance();

//        Schedule rain every 15 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(30), ev -> {
//            the api.rain is from the GardenSimulationAPI
            api.rain(rand.nextInt(40));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();


//        Schedule temperature every 10 seconds
        Timeline timeline2 = new Timeline(new KeyFrame(Duration.seconds(20), ev -> {
//            the api.temperature is from the GardenSimulationAPI
            api.temperature(rand.nextInt(70));
        }));
        timeline2.setCycleCount(Timeline.INDEFINITE);
        timeline2.play();

        // Schedule parasite every 20 seconds
        Timeline timeline3 = new Timeline(new KeyFrame(Duration.seconds(6), ev -> {
            List<Parasite> parasites = parasiteManager.getParasites();
            if (!parasites.isEmpty()) {
                Parasite randomParasite = parasites.get(rand.nextInt(parasites.size()));
                api.parasite(randomParasite.getName()); // Use a random parasite
            } else {
                System.out.println("No parasites available to simulate.");
            }
        }));
        timeline3.setCycleCount(Timeline.INDEFINITE);
        timeline3.play();


    }

    @Override
    public void stop() {
        ThreadManager.shutdown();
    }


    public static void main(String[] args) {
        launch();
    }
}





