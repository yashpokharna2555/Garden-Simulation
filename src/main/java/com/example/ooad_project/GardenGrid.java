package com.example.ooad_project;

import com.example.ooad_project.Plant.Plant;
import com.example.ooad_project.ThreadUtils.EventChannel;

import java.util.ArrayList;

public class GardenGrid {

    private static volatile GardenGrid instance = null; // Use volatile for thread-safe Singleton
    private final Plant[][] plantGrid;

    private static final int NUM_ROWS = 5; // Final for constants
    private static final int NUM_COLS = 7;

    private static final String PLANT_DEATH_EVENT = "PlantDeathEvent";
    private static final String PLANT_DEATH_UI_EVENT = "PlantDeathUIChangeEvent";

    private GardenGrid() {
        plantGrid = new Plant[NUM_ROWS][NUM_COLS];

        // Subscribe to events
        EventChannel.subscribe(PLANT_DEATH_EVENT, event -> handlePlantDeath((Plant) event));
    }

    // Thread-safe Singleton implementation using Double-Checked Locking
    public static GardenGrid getInstance() {
        if (instance == null) {
            synchronized (GardenGrid.class) {
                if (instance == null) {
                    instance = new GardenGrid();
                }
            }
        }
        return instance;
    }

    // Handle plant death event
    private void handlePlantDeath(Plant plant) {
        EventChannel.publish(PLANT_DEATH_UI_EVENT, plant);
        plantGrid[plant.getRow()][plant.getCol()] = null;
    }

    // Print grid for debugging
    public void printGrid() {
        System.out.println("\nGarden Grid:");
        System.out.print("   "); // Top row header
        for (int col = 0; col < NUM_COLS; col++) {
            System.out.print(col + "\t");
        }
        System.out.println("\n" + "-".repeat(8 * NUM_COLS));

        for (int row = 0; row < NUM_ROWS; row++) {
            System.out.print(row + " | "); // Row header
            for (int col = 0; col < NUM_COLS; col++) {
                System.out.print((plantGrid[row][col] != null ? plantGrid[row][col].getName() : "Empty") + "\t");
            }
            System.out.println();
        }
    }

    // Get a list of all plants
    public synchronized ArrayList<Plant> getPlants() {
        ArrayList<Plant> plants = new ArrayList<>();
        for (Plant[] row : plantGrid) {
            for (Plant plant : row) {
                if (plant != null) {
                    plants.add(plant);
                }
            }
        }
        return plants;
    }

    // Add a plant to a specific grid spot
    public synchronized void addPlant(Plant plant, int row, int col) {
        if (row < 0 || row >= NUM_ROWS || col < 0 || col >= NUM_COLS) {
            throw new IllegalArgumentException("Invalid grid position: (" + row + ", " + col + ")");
        }
        if (plantGrid[row][col] != null) {
            throw new IllegalArgumentException("Spot at (" + row + ", " + col + ") is already occupied");
        }
        plantGrid[row][col] = plant;
    }

    // Get a plant from a specific grid spot
    public synchronized Plant getPlant(int row, int col) {
        if (row < 0 || row >= NUM_ROWS || col < 0 || col >= NUM_COLS) {
            throw new IllegalArgumentException("Invalid grid position: (" + row + ", " + col + ")");
        }
        return plantGrid[row][col];
    }

    // Print all plant stats
    public void printAllPlantStats() {
        System.out.println("\nPlant Stats:");
        int count = 0;

        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                Plant plant = plantGrid[row][col];
                if (plant != null) {
                    System.out.println("Plant Name: " + plant.getName());
                    System.out.println("Location: (" + row + ", " + col + ")");
                    System.out.println("Water Requirement: " + plant.getWaterRequirement());
                    System.out.println("Current Water: " + plant.getCurrentWater());
                    System.out.println("Is Watered: " + plant.getIsWatered());
                    System.out.println();
                    count++;
                }
            }
        }

        System.out.println("Total plants: " + count);
    }

    // Check if a grid spot is occupied
    public boolean isSpotOccupied(int row, int col) {
        if (row < 0 || row >= NUM_ROWS || col < 0 || col >= NUM_COLS) {
            throw new IllegalArgumentException("Invalid grid position: (" + row + ", " + col + ")");
        }
        return plantGrid[row][col] != null;
    }

    public final int getNumRows() {
        return NUM_ROWS;
    }

    public final int getNumCols() {
        return NUM_COLS;
    }
}