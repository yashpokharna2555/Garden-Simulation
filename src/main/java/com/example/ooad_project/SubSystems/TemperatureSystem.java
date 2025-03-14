package com.example.ooad_project.SubSystems;

import com.example.ooad_project.Events.TemperatureCoolEvent;
import com.example.ooad_project.Events.TemperatureEvent;
import com.example.ooad_project.Events.TemperatureHeatEvent;
import com.example.ooad_project.GardenGrid;
import com.example.ooad_project.Plant.Plant;
import com.example.ooad_project.ThreadUtils.EventChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TemperatureSystem implements Runnable {

    private final GardenGrid gardenGrid;
    private static final Logger logger = LogManager.getLogger("TemperatureSystemLogger");

    public TemperatureSystem() {
        // Initialize GardenGrid and subscribe to TemperatureEvent
        this.gardenGrid = GardenGrid.getInstance();
        logger.info("Temperature System Initialized");

        EventChannel.subscribe("TemperatureEvent", event -> handleTemperatureEvent((TemperatureEvent) event));
    }

    /**
     * Handles a temperature event by adjusting the temperature for all plants in the garden.
     *
     * @param event the TemperatureEvent containing the target temperature.
     */
    private void handleTemperatureEvent(TemperatureEvent event) {
        int currentTemperature = event.getAmount();
        logger.info("TemperatureEvent received. Target temperature: {}", currentTemperature);

        for (int row = 0; row < gardenGrid.getNumRows(); row++) {
            for (int col = 0; col < gardenGrid.getNumCols(); col++) {
                Plant plant = gardenGrid.getPlant(row, col);
                if (plant != null) {
                    adjustTemperatureForPlant(plant, currentTemperature, row, col);
                }
            }
        }
    }

    /**
     * Adjusts the temperature for an individual plant and publishes appropriate events.
     *
     * @param plant             the plant to adjust temperature for.
     * @param currentTemperature the current temperature from the event.
     * @param row               the row position of the plant in the garden grid.
     * @param col               the column position of the plant in the garden grid.
     */
    private void adjustTemperatureForPlant(Plant plant, int currentTemperature, int row, int col) {
        int tempDiff = currentTemperature - plant.getTemperatureRequirement();

        if (tempDiff > 0) {
            EventChannel.publish("TemperatureCoolEvent", new TemperatureCoolEvent(row, col, tempDiff));
            logger.info("Cooling {} at ({}, {}) by {}°F", plant.getName(), row, col, tempDiff);
        } else if (tempDiff < 0) {
            EventChannel.publish("TemperatureHeatEvent", new TemperatureHeatEvent(row, col, Math.abs(tempDiff)));
            logger.info("Heating {} at ({}, {}) by {}°F", plant.getName(), row, col, Math.abs(tempDiff));
        } else {
            logger.info("{} at ({}, {}) is already at its optimal temperature.", plant.getName(), row, col);
        }
    }

    /**
     * Continuously monitors and logs the temperature system's status.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                logger.debug("Temperature System running: All levels are optimal.");
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                logger.error("Temperature System interrupted.", e);
                Thread.currentThread().interrupt(); // Re-set the interruption status
            }
        }
        logger.info("Temperature System stopped.");
    }
}