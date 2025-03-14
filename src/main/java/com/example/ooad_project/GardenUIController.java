package com.example.ooad_project;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.ooad_project.API.GardenSimulationAPI;
import com.example.ooad_project.Events.DayRollOver;
import com.example.ooad_project.Events.ParasiteEvent;
import com.example.ooad_project.Events.ParasiteInflictionEvent;
import com.example.ooad_project.Events.ParasiteShowEvent;
import com.example.ooad_project.Events.PlantImageUpdateEvent;
import com.example.ooad_project.Events.PlantVitalUpdateEvent;
import com.example.ooad_project.Events.RainEvent;
import com.example.ooad_project.Events.SprinklerEvent;
import com.example.ooad_project.Events.TemperatureCoolEvent;
import com.example.ooad_project.Events.TemperatureEvent;
import com.example.ooad_project.Events.TemperatureHeatEvent;
import com.example.ooad_project.Parasite.Parasite;
import com.example.ooad_project.Parasite.ParasiteManager;
import com.example.ooad_project.Plant.Children.Flower;
import com.example.ooad_project.Plant.Children.Tree;
import com.example.ooad_project.Plant.Children.Vegetable;
import com.example.ooad_project.Plant.Plant;
import com.example.ooad_project.Plant.PlantManager;
import com.example.ooad_project.ThreadUtils.EventChannel;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;


public class GardenUIController {

    @FXML
    private Button sidButton;

    @FXML
    private Label currentDay;

//    @FXML
//    private MenuButton parasiteMenuButton;

//    @FXML
//    private Button pestTestButton;

    @FXML
    private Label getPLantButton;

    @FXML
    private Label rainStatusLabel;
    @FXML
    private Label temperatureStatusLabel;
    @FXML
    private Label parasiteStatusLabel;

    @FXML
    private GridPane gridPane;
    @FXML
    private MenuButton vegetableMenuButton;
    @FXML
    private MenuButton flowerMenuButton;
    @FXML
    private MenuButton treeMenuButton;

    @FXML
    private AnchorPane anchorPane;


    private final Random random = new Random();
    private GardenGrid gardenGrid;

//    This is the plant manager that will be used to get the plant data
//    from the JSON file, used to populate the menu buttons
    private PlantManager plantManager = PlantManager.getInstance();

//    Same as above but for the parasites
    private ParasiteManager parasiteManager = ParasiteManager.getInstance();

    public GardenUIController() {
        gardenGrid = GardenGrid.getInstance();
    }

//    This is the method that will print the grid
    @FXML
    public void printGrid(){
        gardenGrid.printGrid();
    }

    @FXML
    public void sidButtonPressed() {
        System.out.println("SID Button Pressed");
        plantManager.getVegetables().forEach(flower -> System.out.println(flower.getCurrentImage()));
    }

//    @FXML
//    private TextArea logTextArea;

    private static final Logger logger = LogManager.getLogger("GardenUIControllerLogger");


    @FXML
    public void getPLantButtonPressed() {
        GardenSimulationAPI api = new GardenSimulationAPI();
//        api.getPlants();
        api.getState();
    }


//    This is the UI Logger for the GardenUIController
//    This is used to log events that happen in the UI
    private Logger log4jLogger = LogManager.getLogger("GardenUIControllerLogger");

    @FXML
    public void initialize() {

        initializeLogger();

        showSunnyWeather();

        showOptimalTemperature();

        showNoParasites();

//        Stage stage = (Stage) anchorPane.getScene().getWindow();
//        Scene scene = anchorPane.getScene();
//        anchorPane.prefWidthProperty().bind(scene.widthProperty());
//        anchorPane.prefHeightProperty().bind(scene.heightProperty());
//
//         Load the background image
//         Load the background image
        Image backgroundImage = new Image("file:/C:/Users/SID/IdeaProjects/Automated-Garden-OOAD/src/main/resources/images/b5.png");

//        // Create an ImageView
//        ImageView imageView = new ImageView(backgroundImage);
//        imageView.setFitWidth(anchorPane.getPrefWidth());
//        imageView.setFitHeight(anchorPane.getPrefHeight());
//        imageView.setPreserveRatio(false);
//
//        // Add the ImageView as the first child of the AnchorPane
//        anchorPane.getChildren().add(0, imageView);


        // Create an ImageView
        ImageView imageView = new ImageView(backgroundImage);
        imageView.setPreserveRatio(false);

        // Add the ImageView as the first child of the AnchorPane
        anchorPane.getChildren().add(0, imageView);

        // Bind ImageView's size to the AnchorPane's size
        anchorPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            imageView.setFitWidth(newVal.doubleValue());
        });
        anchorPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            imageView.setFitHeight(newVal.doubleValue());
        });

        // Add ColumnConstraints
        for (int col = 0; col < gardenGrid.getNumCols(); col++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPrefWidth(80); // Adjust the width as needed
            gridPane.getColumnConstraints().add(colConst);
        }

        // Add RowConstraints
        for (int row = 0; row < gardenGrid.getNumRows(); row++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPrefHeight(80); // Adjust the height as needed
            gridPane.getRowConstraints().add(rowConst);
        }

        // Load plants data from JSON file and populate MenuButtons
        loadPlantsData();
//        loadParasitesData();

        log4jLogger.info("GardenUIController initialized");



        EventChannel.subscribe("RainEvent", event -> changeRainUI((RainEvent) event));
        EventChannel.subscribe("DisplayParasiteEvent", event -> handleDisplayParasiteEvent((ParasiteShowEvent) event));
        EventChannel.subscribe("PlantImageUpdateEvent", event -> handlePlantImageUpdateEvent((PlantImageUpdateEvent) event));
        EventChannel.subscribe("DayChangeEvent",event -> handleDayChangeEvent((DayRollOver) event));
        EventChannel.subscribe("TemperatureEvent", event -> changeTemperatureUI((TemperatureEvent) event));
        EventChannel.subscribe("ParasiteEvent", event -> changeParasiteUI((ParasiteEvent) event));

//      Gives you row, col and waterneeded
        EventChannel.subscribe("SprinklerEvent", event -> handleSprinklerEvent((SprinklerEvent) event));


//        When plant is cooled by x
        EventChannel.subscribe("TemperatureCoolEvent", event -> handleTemperatureCoolEvent((TemperatureCoolEvent) event));


//      When plant is heated by x
        EventChannel.subscribe("TemperatureHeatEvent", event -> handleTemperatureHeatEvent((TemperatureHeatEvent) event));


//        When plant is damaged by x
//        Includes -> row, col, damage
        EventChannel.subscribe("ParasiteDamageEvent", event -> handleParasiteDamageEvent((ParasiteInflictionEvent) event));

        EventChannel.subscribe("InitializeGarden", event -> handleInitializeGarden());

//        Event whenever there is change to plants health
        EventChannel.subscribe("PlantHealthUpdateEvent", event -> handlePlantHealthUpdateEvent((PlantVitalUpdateEvent) event));

        EventChannel.subscribe("PlantDeathUIChangeEvent", event -> handlePlantDeathUIChangeEvent((Plant) event));

    }

    private void handlePlantDeathUIChangeEvent(Plant plant){

    }

    private void handlePlantHealthUpdateEvent(PlantVitalUpdateEvent event){
        System.out.println("Plant health updated at row " + event.getRow() + " and column " + event.getCol() + " from " + event.getOldHealth() + " to " + event.getNewHealth());
    }

    private void handleInitializeGarden() {
        // Hard-coded positions for plants as specified in the layout
        Object[][] gardenLayout = {
                {"Oak", 0, 1}, {"Maple", 0, 5}, {"Pine", 0, 6},
                {"Tomato", 1, 6}, {"Carrot", 2, 2}, {"Lettuce", 1, 0},
                {"Sunflower", 3, 1}, {"Rose", 4, 4}, {"Jasmine", 4, 6},
                {"Oak", 2, 6}, {"Tomato", 3, 0}, {"Sunflower", 4, 3}
        };

        Platform.runLater(() -> {
            for (Object[] plantInfo : gardenLayout) {
                String plantType = (String) plantInfo[0];
                int row = (Integer) plantInfo[1];
                int col = (Integer) plantInfo[2];

                Plant plant = plantManager.getPlantByName(plantType);
                if (plant != null) {
                    plant.setRow(row);
                    plant.setCol(col);
                    try {
                        gardenGrid.addPlant(plant, row, col);  // Add plant to the logical grid
                        addPlantToGridUI(plant, row, col);    // Add plant to the UI
                    } catch (Exception e) {
                        logger.error("Failed to place plant: " + plant.getName() + " at (" + row + ", " + col + "): " + e.getMessage());
                    }
                }
            }
        });
    }

    private void addPlantToGridUI(Plant plant, int row, int col) {

        logger.info("Adding plant to grid: " + plant.getName() + " at row " + row + " and column " + col);

        String imageFile = plant.getCurrentImage();
        Image image = new Image(getClass().getResourceAsStream("/images/" + imageFile));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(40); // Fit the cell size
        imageView.setFitWidth(60);

        StackPane pane = new StackPane(imageView);
        pane.setStyle("-fx-alignment: center;"); // Center the image in the pane
        gridPane.add(pane, col, row); // Add the pane to the grid
        GridPane.setHalignment(pane, HPos.CENTER); // Center align in grid cell
        GridPane.setValignment(pane, VPos.CENTER);
    }

//    Function that is called when the parasite damage event is published
private void handleParasiteDamageEvent(ParasiteInflictionEvent event) {

        logger.info("Displayed plant damaged at row " + event.getRow() + " and column " + event.getCol() + " by " + event.getDamage());

    Platform.runLater(() -> {
        int row = event.getRow();
        int col = event.getCol();
        int damage = event.getDamage();

        // Create a label with the damage value prefixed by a minus sign
        Label damageLabel = new Label("-" + String.valueOf(damage));
        damageLabel.setTextFill(javafx.scene.paint.Color.RED);
        damageLabel.setStyle("-fx-font-weight: bold;");

        // Set the label's position in the grid
        GridPane.setRowIndex(damageLabel, row);
        GridPane.setColumnIndex(damageLabel, col);
        GridPane.setHalignment(damageLabel, HPos.RIGHT);  // Align to right
        GridPane.setValignment(damageLabel, VPos.TOP);    // Align to top
        gridPane.getChildren().add(damageLabel);

        // Remove the label after a pause
        PauseTransition pause = new PauseTransition(Duration.seconds(5)); // Set duration to 5 seconds
        pause.setOnFinished(_ -> gridPane.getChildren().remove(damageLabel));
        pause.play();
    });
}


    private void handleTemperatureHeatEvent(TemperatureHeatEvent event) {

        logger.info("Displayed plant heated at row " + event.getRow() + " and column " + event.getCol() + " by " + event.getTempDiff());

        Platform.runLater(() -> {
            int row = event.getRow();
            int col = event.getCol();

            String imageName = "heat.png"; // Update this to your heat image name
            Image heatImage = new Image(getClass().getResourceAsStream("/images/" + imageName));
            ImageView heatImageView = new ImageView(heatImage);
            heatImageView.setFitHeight(20);  // Match the cell size in the grid
            heatImageView.setFitWidth(20);

            GridPane.setRowIndex(heatImageView, row);
            GridPane.setColumnIndex(heatImageView, col);
            GridPane.setHalignment(heatImageView, HPos.LEFT);  // Align to left
            GridPane.setValignment(heatImageView, VPos.TOP); // Align to top
            gridPane.getChildren().add(heatImageView);

            PauseTransition pause = new PauseTransition(Duration.seconds(5)); // Set duration to 10 seconds
            pause.setOnFinished(_ -> gridPane.getChildren().remove(heatImageView));
            pause.play();
        });
    }


//    Function that is called when the temperature cool event is published

    private void handleTemperatureCoolEvent(TemperatureCoolEvent event) {


        logger.info("Displayed plant cooled at row " + event.getRow() + " and column " + event.getCol() + " by " + event.getTempDiff());

        Platform.runLater(() -> {
            int row = event.getRow();
            int col = event.getCol();

            String imageName = "cool.png"; // Update this to your cool image name
            Image coolImage = new Image(getClass().getResourceAsStream("/images/" + imageName));
            ImageView coolImageView = new ImageView(coolImage);
            coolImageView.setFitHeight(20);  // Match the cell size in the grid
            coolImageView.setFitWidth(20);

            GridPane.setRowIndex(coolImageView, row);
            GridPane.setColumnIndex(coolImageView, col);
            GridPane.setHalignment(coolImageView, HPos.LEFT);  // Align to left
            GridPane.setValignment(coolImageView, VPos.TOP); // Align to top
            gridPane.getChildren().add(coolImageView);

            PauseTransition pause = new PauseTransition(Duration.seconds(5)); // Set duration to 10 seconds
            pause.setOnFinished(_ -> gridPane.getChildren().remove(coolImageView));
            pause.play();
        });
    }
//  Function that is called when the sprinkler event is published
private void handleSprinklerEvent(SprinklerEvent event) {

        logger.info("Displayed Sprinkler activated at row " + event.getRow() + " and column " + event.getCol() + " with water amount " + event.getWaterNeeded());

    Platform.runLater(() -> {
        int row = event.getRow();
        int col = event.getCol();

        String imageName = "sprinkler.png"; // Update this to your sprinkler image name
        Image sprinklerImage = new Image(getClass().getResourceAsStream("/images/" + imageName));
        ImageView sprinklerImageView = new ImageView(sprinklerImage);
        sprinklerImageView.setFitHeight(20);  // Match the cell size in the grid
        sprinklerImageView.setFitWidth(20);

        GridPane.setRowIndex(sprinklerImageView, row);
        GridPane.setColumnIndex(sprinklerImageView, col);
        GridPane.setHalignment(sprinklerImageView, HPos.LEFT);  // Align to left
        GridPane.setValignment(sprinklerImageView, VPos.BOTTOM); // Align to bottom
        gridPane.getChildren().add(sprinklerImageView);

        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(_ -> gridPane.getChildren().remove(sprinklerImageView));
        pause.play();
    });
}

    private void initializeLogger() {
//        LoggerAppender.setController(this);
    }

//    public void appendLogText(String text) {
//        Platform.runLater(() -> logTextArea.appendText(text + "\n"));
//    }

    public void handleDayChangeEvent(DayRollOver event) {
        logger.info("Day changed to: " + event.getDay());

        Platform.runLater(() -> {
            // Store the old value
            String oldValue = currentDay.getText();
            
            // Create scale transition for zoom out effect
            javafx.animation.ScaleTransition scaleOut = new javafx.animation.ScaleTransition(Duration.millis(200), currentDay);
            scaleOut.setFromX(1.0);
            scaleOut.setFromY(1.0);
            scaleOut.setToX(0.5);
            scaleOut.setToY(0.5);
            
            // Create scale transition for zoom in effect
            javafx.animation.ScaleTransition scaleIn = new javafx.animation.ScaleTransition(Duration.millis(200), currentDay);
            scaleIn.setFromX(0.5);
            scaleIn.setFromY(0.5);
            scaleIn.setToX(1.0);
            scaleIn.setToY(1.0);
            
            // Create fade out transition
            javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(Duration.millis(200), currentDay);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.5);
            
            // Create fade in transition
            javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(Duration.millis(200), currentDay);
            fadeIn.setFromValue(0.5);
            fadeIn.setToValue(1.0);
            
            // Play the animations in sequence
            scaleOut.setOnFinished(e -> {
                // Update the text
                currentDay.setText(String.valueOf(event.getDay()));
                // Play zoom in animation
                scaleIn.play();
                fadeIn.play();
            });
            
            // Start the animation sequence
            scaleOut.play();
            fadeOut.play();
            
            System.out.println("Day changed from " + oldValue + " to: " + event.getDay());
        });
    }

    private void handlePlantImageUpdateEvent(PlantImageUpdateEvent event) {

        logger.info("Plant image updated at row " + event.getPlant().getRow() + " and column " + event.getPlant().getCol() + " to " + event.getPlant().getCurrentImage());

//        Be sure to wrap the code in Platform.runLater() to update the UI
//        This is because the event is being handled in a different thread
//        and we need to update the UI in the JavaFX Application Thread
        Platform.runLater(() -> {

            Plant plant = event.getPlant();

            // Calculate the grid position
            int row = plant.getRow();
            int col = plant.getCol();

            // Find the ImageView for the plant in the grid and remove it
            gridPane.getChildren().removeIf(node -> {
                if (GridPane.getRowIndex(node) != null && GridPane.getColumnIndex(node) != null) {
                    return GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col && node instanceof ImageView;
                }
                return false;
            });

            // Load the new image for the plant
            String imageName = plant.getCurrentImage();
            Image newImage = new Image(getClass().getResourceAsStream("/images/" + imageName));
            ImageView newImageView = new ImageView(newImage);
            newImageView.setFitHeight(40);  // Match the cell size in the grid
            newImageView.setFitWidth(40);

            // Create a pane to center the image
            StackPane pane = new StackPane();
            pane.getChildren().add(newImageView);
            gridPane.add(pane, col, row);

            System.out.println("Updated plant image at row " + row + " and column " + col + " to " + imageName);
    });
    }


    private void handleDisplayParasiteEvent(ParasiteShowEvent event) {

        logger.info("Parasite displayed at row " + event.getRow() + " and column " + event.getColumn() + " with name " + event.getParasite().getName());

        // Load the image for the rat
        String imageName = "/images/" + event.getParasite().getImageName();
        Image ratImage = new Image(getClass().getResourceAsStream(imageName));
        ImageView parasiteImageView = new ImageView(ratImage);

//        Change var name
        parasiteImageView.setFitHeight(20);  // Match the cell size in the grid
        parasiteImageView.setFitWidth(20);

        // Use the row and column from the event
        int row = event.getRow();
        int col = event.getColumn();

        // Place the rat image on the grid
//        gridPane.add(parasiteImageView, col, row);
//        System.out.println("Rat placed at row " + row + " and column " + col);


        // Place the parasite image on the grid in the same cell but with offset
        GridPane.setRowIndex(parasiteImageView, row);
        GridPane.setColumnIndex(parasiteImageView, col);
        GridPane.setHalignment(parasiteImageView, HPos.RIGHT);  // Align to right
        GridPane.setValignment(parasiteImageView, VPos.BOTTOM); // Align to bottom
        gridPane.getChildren().add(parasiteImageView);

        // Create a pause transition of 5 seconds before removing the rat image
        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(_ -> {
            gridPane.getChildren().remove(parasiteImageView);  // Remove the rat image from the grid
//            System.out.println("Rat removed from row " + row + " and column " + col);
        });
        pause.play();
    }


//    private void loadParasitesData() {
//        for (Parasite parasite : parasiteManager.getParasites()) {
//            MenuItem menuItem = new MenuItem(parasite.getName());
//            menuItem.setOnAction(e -> handleParasiteSelection(parasite));
//            parasiteMenuButton.getItems().add(menuItem);
//        }
//    }

    private void handleParasiteSelection(Parasite parasite) {
        // Implement what happens when a parasite is selected
        // For example, display details or apply effects to the garden
        System.out.println("Selected parasite: " + parasite.getName() + " with damage: " + parasite.getDamage());
    }

//
    @FXML
    public void showPestOnGrid() {}


    private void changeRainUI(RainEvent event) {

        logger.info("Displayed rain event with amount: " + event.getAmount() + "mm");

        Platform.runLater(() -> {
            // Update UI to reflect it's raining
            System.out.println("Changing UI to reflect rain event");

            // Create an ImageView for the rain icon
            Image rainImage = new Image(getClass().getResourceAsStream("/images/rain.png"));
            ImageView rainImageView = new ImageView(rainImage);
            rainImageView.setFitHeight(20);
            rainImageView.setFitWidth(20);

            // Set the text with the rain amount
            rainStatusLabel.setGraphic(rainImageView);
            rainStatusLabel.setText(event.getAmount() + "mm");

            // Create a pause transition of 5 seconds
            PauseTransition pause = new PauseTransition(Duration.seconds(5));
            pause.setOnFinished(e -> {
                // Update UI to reflect no rain after the event ends
                showSunnyWeather();
                System.out.println("Rain event ended, updating UI to show sunny weather.");
            });
            pause.play();
        });
    }

    private void showSunnyWeather() {
        logger.info("Displayed sunny weather");

        Platform.runLater(() -> {
            // Create an ImageView for the sun icon
            Image sunImage = new Image(getClass().getResourceAsStream("/images/sun.png"));
            ImageView sunImageView = new ImageView(sunImage);
            sunImageView.setFitHeight(30);
            sunImageView.setFitWidth(30);

            // Set the text with the sun status
            rainStatusLabel.setGraphic(sunImageView);
            rainStatusLabel.setText("Sunny");
        });
    }


    private void changeTemperatureUI(TemperatureEvent event) {

        logger.info("Temperature changed to: " + event.getAmount() + "°F");

        Platform.runLater(() -> {
            // Update UI to reflect the temperature change
            System.out.println("Changing UI to reflect temperature event");

            // Create an ImageView for the temperature icon
            Image tempImage = new Image(getClass().getResourceAsStream("/images/temperature.png"));
            ImageView tempImageView = new ImageView(tempImage);
            tempImageView.setFitHeight(20);
            tempImageView.setFitWidth(20);

            // Set the text with the temperature amount
            temperatureStatusLabel.setGraphic(tempImageView);
            temperatureStatusLabel.setText(event.getAmount() + "°F");

            // Create a pause transition of 5 seconds
            PauseTransition pause = new PauseTransition(Duration.seconds(5));
            pause.setOnFinished(e -> {
                // Update UI to reflect optimal temperature after the event ends
                showOptimalTemperature();
                System.out.println("Temperature event ended, updating UI to show optimal temperature.");
            });
            pause.play();
        });
    }

    private void showOptimalTemperature() {

        logger.info("Displayed optimal temperature");

        Platform.runLater(() -> {
            // Create an ImageView for the optimal temperature icon
            Image optimalImage = new Image(getClass().getResourceAsStream("/images/optimal.png"));
            ImageView optimalImageView = new ImageView(optimalImage);
            optimalImageView.setFitHeight(15);
            optimalImageView.setFitWidth(15);

            // Set the text with the optimal status
            temperatureStatusLabel.setGraphic(optimalImageView);
            temperatureStatusLabel.setText("Optimal");
        });
    }

    private void changeParasiteUI(ParasiteEvent event) {

        logger.info("Parasite event triggered: " + event.getParasite().getName());

        Platform.runLater(() -> {
            // Update UI to reflect parasite event
            System.out.println("Changing UI to reflect parasite event");

            // Create an ImageView for the sad icon
            Image sadImage = new Image(getClass().getResourceAsStream("/images/sad.png"));
            ImageView sadImageView = new ImageView(sadImage);
            sadImageView.setFitHeight(20);
            sadImageView.setFitWidth(20);

            // Set the text with the parasite name
            parasiteStatusLabel.setGraphic(sadImageView);
            parasiteStatusLabel.setText(event.getParasite().getName() + " detected");

            // Create a pause transition of 5 seconds
            PauseTransition pause = new PauseTransition(Duration.seconds(5));
            pause.setOnFinished(e -> {
                // Update UI to reflect no parasites after the event ends
                showNoParasites();
                System.out.println("Parasite event ended, updating UI to show no parasites.");
            });
            pause.play();
        });
    }

    private void showNoParasites() {

        logger.info("Displayed no parasites status");

        Platform.runLater(() -> {
            // Create an ImageView for the happy icon
            Image happyImage = new Image(getClass().getResourceAsStream("/images/happy.png"));
            ImageView happyImageView = new ImageView(happyImage);
            happyImageView.setFitHeight(20);
            happyImageView.setFitWidth(20);

            // Set the text with the no parasites status
            parasiteStatusLabel.setGraphic(happyImageView);
            parasiteStatusLabel.setText("No Parasites");
        });
    }

    //    This is the method that will populate the menu buttons with the plant data
    private void loadPlantsData() {
        logger.info("Loading plant data from JSON file");

        // Clear existing items
        treeMenuButton.getItems().clear();
        flowerMenuButton.getItems().clear();
        vegetableMenuButton.getItems().clear();

        // Add trees with proper names
        for (Tree tree : plantManager.getTrees()) {
            MenuItem menuItem = new MenuItem(tree.getName() + " Tree");
            menuItem.setStyle("-fx-font-size: 14; -fx-padding: 8 15;");
            menuItem.setOnAction(e -> addPlantToGrid(tree.getName(), tree.getCurrentImage()));
            treeMenuButton.getItems().add(menuItem);
        }

        // Add flowers with proper styling
        for (Flower flower : plantManager.getFlowers()) {
            MenuItem menuItem = new MenuItem(flower.getName());
            menuItem.setStyle("-fx-font-size: 14; -fx-padding: 8 15;");
            menuItem.setOnAction(e -> addPlantToGrid(flower.getName(), flower.getCurrentImage()));
            flowerMenuButton.getItems().add(menuItem);
        }

        // Add vegetables with proper styling
        for (Vegetable vegetable : plantManager.getVegetables()) {
            MenuItem menuItem = new MenuItem(vegetable.getName());
            menuItem.setStyle("-fx-font-size: 14; -fx-padding: 8 15;");
            menuItem.setOnAction(e -> addPlantToGrid(vegetable.getName(), vegetable.getCurrentImage()));
            vegetableMenuButton.getItems().add(menuItem);
        }
    }

    private void addPlantToGrid(String name, String imageFile) {
        logger.info("Adding plant to grid: " + name + " with image: " + imageFile);

        Plant plant = plantManager.getPlantByName(name.replace(" Tree", "")); // Remove "Tree" suffix if present
        if (plant != null) {
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 100) {
                int row = random.nextInt(gardenGrid.getNumRows());
                int col = random.nextInt(gardenGrid.getNumCols());
                if (!gardenGrid.isSpotOccupied(row, col)) {
                    System.out.println("Placing " + name + " at row " + row + " col " + col);
                    plant.setRow(row);
                    plant.setCol(col);
                    gardenGrid.addPlant(plant, row, col);
                    
                    Platform.runLater(() -> {
                        ImageView plantView = new ImageView(new Image(getClass().getResourceAsStream("/images/" + imageFile)));
                        plantView.setFitHeight(40);
                        plantView.setFitWidth(40);

                        StackPane pane = new StackPane();
                        pane.getChildren().add(plantView);
                        pane.setStyle("-fx-alignment: center;");
                        
                        gridPane.add(pane, col, row);
                        GridPane.setHalignment(pane, HPos.CENTER);
                        GridPane.setValignment(pane, VPos.CENTER);
                    });
                    
                    placed = true;
                }
                attempts++;
            }
            if (!placed) {
                System.err.println("Failed to place the plant after 100 attempts, grid might be full.");
            }
        } else {
            System.err.println("Plant not found: " + name);
        }
    }



}