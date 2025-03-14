package com.example.ooad_project.SubSystems;

import com.example.ooad_project.Events.ParasiteShowEvent;
import com.example.ooad_project.Events.ParasiteEvent;
import com.example.ooad_project.GardenGrid;
import com.example.ooad_project.Parasite.Parasite;
import com.example.ooad_project.Plant.Plant;
import com.example.ooad_project.ThreadUtils.EventChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PesticideSystem implements Runnable {

    private final GardenGrid gardenGrid;
    private static final Logger logger = LogManager.getLogger(PesticideSystem.class);

    public PesticideSystem() {
        this.gardenGrid = GardenGrid.getInstance();
        logger.info("Pesticide System Initialized");

        // Subscribe to ParasiteEvent
        EventChannel.subscribe("ParasiteEvent", event -> handlePesticideEvent((ParasiteEvent) event));
    }

    /**
     * Handles the pesticide event by applying pesticides to affected plants.
     * @param event the parasite event containing details about the parasite and affected plants.
     */
    private void handlePesticideEvent(ParasiteEvent event) {
        Parasite parasite = event.getParasite();
        logger.info("Handling ParasiteEvent for parasite: {}, damage: {}", parasite.getName(), parasite.getDamage());

        for (int row = 0; row < gardenGrid.getNumRows(); row++) {
            for (int col = 0; col < gardenGrid.getNumCols(); col++) {
                Plant plant = gardenGrid.getPlant(row, col);
                if (plant != null && parasite.getAffectedPlants().contains(plant.getName())) {
                    // Publish an event to display the parasite in the UI
                    EventChannel.publish("DisplayParasiteEvent", new ParasiteShowEvent(parasite, row, col));

                    // Apply parasite damage to the plant
                    parasite.affectPlant(plant);
                    logger.info("Parasite {} applied to plant {} at ({}, {}). Damage: {}",
                            parasite.getName(), plant.getName(), row, col, parasite.getDamage());

                    // Heal the plant using the pesticide system
                    int healAmount = parasite.getDamage() / 2;
                    plant.healPlant(healAmount);
                    logger.info("Plant {} healed by {} at ({}, {})", plant.getName(), healAmount, row, col);
                }
            }
        }
    }

    /**
     * Continuous thread loop to manage system operations.
     * Currently, it checks plant safety status every second.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                logger.debug("Pesticide system running. All plants are monitored.");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Pesticide system interrupted.", e);
                Thread.currentThread().interrupt();
            }
        }
        logger.info("Pesticide system stopped.");
    }
}