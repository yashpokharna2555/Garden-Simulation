package com.example.ooad_project.Parasite.Children;

import com.example.ooad_project.Events.ParasiteInflictionEvent;
import com.example.ooad_project.Parasite.Parasite;
import com.example.ooad_project.Plant.Plant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Random;

public class Crow extends Parasite {
    private static final double MISS_CHANCE = 0.20;  // 20% chance to miss
    private Random random = new Random();
    private static final Logger logger = LogManager.getLogger("PesticideSystemLogger");

    public Crow(String name, int damage, String imageName, ArrayList<String> affectedPlants) {
        super(name, damage, imageName, affectedPlants);
    }

    @Override
    public void affectPlant(Plant plant) {
        if (random.nextDouble() >= MISS_CHANCE) {
            // If not missed, apply the damage
            int oldHealth = plant.getCurrentHealth();
            int newHealth = Math.max(0, plant.getCurrentHealth() - this.getDamage());
            super.publishDamageEvent(new ParasiteInflictionEvent(plant.getRow(),plant.getCol(), this.getDamage()));

            plant.setCurrentHealth(newHealth);
            logger.info("Crow has successfully damaged the plant {} at position ({}, {}). Old health: {}. New health: {}",
                    plant.getName(), plant.getRow(), plant.getCol(), oldHealth, newHealth);

        } else {
            // If missed, do nothing
            logger.info("Crow attempted to damage the plant {} at position ({}, {}) but missed.",
                    plant.getName(), plant.getRow(), plant.getCol());
        }
    }

}
