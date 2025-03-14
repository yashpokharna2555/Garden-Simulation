package com.example.ooad_project.Parasite;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ParasiteManager {
    private static volatile ParasiteManager instance;
    private Map<String, Parasite> parasiteMap;

    private ParasiteManager() {
        parasiteMap = new HashMap<>();
        loadParasitesData();
    }

    public static ParasiteManager getInstance() {
        if (instance == null) {
            synchronized (ParasiteManager.class) {
                if (instance == null) {
                    instance = new ParasiteManager();
                }
            }
        }
        return instance;
    }

    public Parasite getParasiteByName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Parasite name cannot be null or empty");
        }
        return parasiteMap.get(name.toLowerCase());
    }

    private void loadParasitesData() {
        String filePath = "parasites.json"; // You could externalize this path

        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);
            JSONArray parasitesArray = jsonObject.getJSONArray("parasites");

            for (int i = 0; i < parasitesArray.length(); i++) {
                JSONObject parasiteJson = parasitesArray.getJSONObject(i);
                JSONArray targetPlantsJsonArray = parasiteJson.getJSONArray("targetPlants");
                ArrayList<String> targetPlants = new ArrayList<>();
                for (int j = 0; j < targetPlantsJsonArray.length(); j++) {
                    targetPlants.add(targetPlantsJsonArray.getString(j));
                }

                // Create the parasite and add it to the map
                Parasite parasite = ParasiteFactory.createParasite(
                        parasiteJson.getString("name"),
                        parasiteJson.getInt("damage"),
                        parasiteJson.getString("imageName"),
                        targetPlants
                );
                if (parasite != null) {
                    parasiteMap.put(parasite.getName().toLowerCase(), parasite);
                }
            }
        } catch (IOException e) {
            // Log the error or handle it appropriately
            System.err.println("Error loading parasites data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ArrayList<Parasite> getParasites() {
        return new ArrayList<>(parasiteMap.values());
    }
}