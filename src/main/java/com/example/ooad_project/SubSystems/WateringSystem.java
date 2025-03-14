package com.example.ooad_project.SubSystems;

import com.example.ooad_project.GardenGrid;
import com.example.ooad_project.Plant.Plant;
import com.example.ooad_project.ThreadUtils.EventChannel;
import com.example.ooad_project.Events.RainEvent;
import com.example.ooad_project.Events.SprinklerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

public class WateringSystem implements Runnable {
    private final AtomicBoolean rainRequested = new AtomicBoolean(false);
    private static final Logger logger = LogManager.getLogger("WateringSystemLogger");
    private final GardenGrid gardenGrid;

    public WateringSystem() {
        logger.info("Watering System Initialized");

        // Subscribe to RainEvent and SprinklerActivationEvent
        EventChannel.subscribe("RainEvent", event -> handleRain((RainEvent) event));
        EventChannel.subscribe("SprinklerActivationEvent", event -> sprinkle());

        // Get the GardenGrid instance to access plants
        this.gardenGrid = GardenGrid.getInstance();
    }

    /**
     * Continuously monitors the watering system's status.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000); // Check every second
            } catch (InterruptedException e) {
                logger.error("Watering System interrupted", e);
                Thread.currentThread().interrupt(); // Re-set the interruption status
                return; // Exit the loop
            }
        }
        logger.info("Watering System stopped.");
    }

    /**
     * Handles rain events by watering all plants uniformly with the specified rain amount.
     *
     * @param event the RainEvent containing the amount of rain.
     */
    private void handleRain(RainEvent event) {
        int rainAmount = event.getAmount();
        logger.info("RainEvent received. Watering plants with {} units of water.", rainAmount);

        for (int row = 0; row < gardenGrid.getNumRows(); row++) {
            for (int col = 0; col < gardenGrid.getNumCols(); col++) {
                Plant plant = gardenGrid.getPlant(row, col);
                if (plant != null) {
                    plant.addWater(rainAmount);
                    logger.info("Watered {} at position ({}, {}) with {} units of rainwater.", 
                                plant.getName(), row, col, rainAmount);
                }
            }
        }
    }

    /**
     * Activates sprinklers to water plants based on their individual water requirements.
     */
    private void sprinkle() {
        logger.info("Sprinklers activated!");
        int wateredPlantCount = 0;

        for (int row = 0; row < gardenGrid.getNumRows(); row++) {
            for (int col = 0; col < gardenGrid.getNumCols(); col++) {
                Plant plant = gardenGrid.getPlant(row, col);
                if (plant != null && !plant.getIsWatered()) {
                    int waterNeeded = plant.getWaterRequirement() - plant.getCurrentWater();

                    if (waterNeeded > 0) {
                        EventChannel.publish("SprinklerEvent", new SprinklerEvent(row, col, waterNeeded));
                        plant.addWater(waterNeeded);
                        logger.info("Sprinkled {} at position ({}, {}) with {} units of water.", 
                                    plant.getName(), row, col, waterNeeded);
                        wateredPlantCount++;
                    } else {
                        logger.info("{} at position ({}, {}) does not need water.", plant.getName(), row, col);
                    }
                }
            }
        }

        logger.info("Sprinklers watered a total of {} plants.", wateredPlantCount);
    }
}