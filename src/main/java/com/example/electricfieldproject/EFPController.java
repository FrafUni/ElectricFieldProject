package com.example.electricfieldproject;

import com.example.electricfieldproject.commons.PVector;
import com.example.electricfieldproject.commons.Sprite;
import com.example.electricfieldproject.electricharges.ChargedSphere;
import com.example.electricfieldproject.electricharges.ChargesSettings;
import com.example.electricfieldproject.electricharges.MajorChargedSphere;
import com.example.electricfieldproject.forceelement.ForceLine;
import com.example.electricfieldproject.forceelement.ForceText;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.stage.Modality;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class EFPController {
    @FXML private AnchorPane anchorPane;
    @FXML private Label SphereLabel;
    @FXML private Button clearButton;
    @FXML private Sphere clickableNegativeChargedSphere;
    @FXML private Sphere clickablePositiveChargedSphere;
    @FXML private Pane root;
    @FXML private Button realityFilterButton;

    ConcurrentLinkedDeque<ChargedSphere> chargedSpheresDeque;

    ForceLine forceLine;
    ForceText forceText;
    Circle mainField;
    ChargedSphere chargedSphere;
    MajorChargedSphere majorChargedSphere;
    boolean creationChargeEnabled = true;
    boolean majorSphereHided = false;
    boolean isPositiveCharge;
    boolean realityFilter = false;
    ExecutorService executorService;

    @FXML
    void initialize() {
        chargedSpheresDeque = new ConcurrentLinkedDeque<>();
        //central field
        initializeMainField();

        // central sphere
        initializeMajorSphere();

        //selection spheres
        clickablePositiveChargedSphere.setMaterial(new PhongMaterial(Color.web("#3337ff")));
        clickablePositiveChargedSphere.setDrawMode(DrawMode.FILL);
        clickableNegativeChargedSphere.setMaterial(new PhongMaterial(Color.web("#ff3232")));
        clickableNegativeChargedSphere.setDrawMode(DrawMode.FILL);

        anchorPane.toFront();
        executorService = Executors.newFixedThreadPool(4);
        initializeMainTimer();

        //majorChargedSphere.setOnMouseDragged(this::dragMajorChargedSphere);
    }

    /**
     * Method that initialize the center sphere
     */
    public void initializeMajorSphere(){
        double h = ChargesSettings.DEFAULT_HEIGHT;
        double w = ChargesSettings.DEFAULT_WIDTH;
        majorChargedSphere = new MajorChargedSphere("MajorChargedSphere", 30, new PhongMaterial(Color.web("#3337ff")), mainField.getRadius());
        root.getChildren().add(majorChargedSphere);
        majorChargedSphere.setCenter(new PVector(w/2, h/2));
        majorChargedSphere.setOnMouseClicked(e -> handleChangedSphereProperty());
        majorChargedSphere.setCursor(Cursor.HAND);
    }

    /**
     * Set the background of the electric field
     */
    void initializeMainField(){
        mainField = new Circle(265.0);

        RadialGradient paint = new RadialGradient(
                0, 0.0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, new Color(0.3428, 0.35, 0.322, 1.0)),
                new Stop(0.0067, new Color(0.3428, 0.35, 0.322, 1.0)),
                new Stop(1.0, new Color(1.0, 1.0, 1.0, 0.0)));

        mainField.setFill(paint);
        root.getChildren().add(mainField);
        mainField.toBack();
        mainField.setTranslateX(ChargesSettings.DEFAULT_WIDTH/2);
        mainField.setTranslateY(ChargesSettings.DEFAULT_HEIGHT/2);
    }

    /**
     * The timer that will be used to update every element in the screen simulating the movement
     */
    void initializeMainTimer() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastTime = 0;
            @Override
            public void handle(long currentTime) {
                if(lastTime == 0){
                    lastTime = currentTime;
                    return;
                }
                double dt = (currentTime - lastTime) / ChargesSettings.NANOCONVERTER;
                mainLoop(dt);
                lastTime = currentTime;
            }
        };
        timer.start();
    }

    /**
     * Method that handle all the computation
     * @param dt time
     */
    void mainLoop(double dt) {
        chargedSpheresDeque.forEach(ChargedSphere::update);
        //compute the displacement of each charge
        handleFieldComputations(dt);
        //check if any sphere exits from the interface
        checkSphereBounds();
        updatePositions();
    }

    /**
     * Insert a new Charged Sphere in the field (onMousePressed)
     * @param event mouse click
     */
    void initializeChargedSphere(MouseEvent event) {
        chargedSphere = null;
        for(ChargedSphere cs : chargedSpheresDeque){
            if(PVector.distance(cs.getLocation(), new PVector(event.getX(),event.getY())) < cs.getRadius()){
                chargedSphere = cs;
            }
        }

        forceText = new ForceText();
        forceLine = new ForceLine(event.getX(), event.getY(), event.getX(), event.getY());
        root.getChildren().addAll(forceLine, forceText);
        forceLine.setVisible(true);
        forceLine.toBack();

        if(chargedSphere == null){
            if(isPositiveCharge)
                chargedSphere = new ChargedSphere("ChargedSphere", 20, ChargesSettings.ASSUMED_MINOR_CHARGE, new PhongMaterial(Color.web("#3337ff")));
            else
                chargedSphere = new ChargedSphere("ChargedSphere", 20, -ChargesSettings.ASSUMED_MINOR_CHARGE, new PhongMaterial(Color.web("#ff3232")));

            chargedSphere.setCenter(new PVector(event.getX(), event.getY()));
            root.getChildren().add(chargedSphere);
        }
        else{
            chargedSpheresDeque.remove(chargedSphere);
            chargedSphere.showDetails();
            chargedSphere.setVelocity(new PVector(0,0));
        }
    }

    /**
     * Let the user choose the force to apply to the charged sphere
     * @param event mouse drag
     */
    void dragForceLine(MouseEvent event) {
        forceText.setOwner(new PVector(event.getX(), event.getY()));
        forceLine.setEnd(new PVector(event.getX(), event.getY()));


        forceText.setText(String.format("%.1fµN", ForceLine.getHypotenuse(forceLine)/ChargesSettings.CONVERTER));
        if(chargedSphere.getCharge() > 0)
            forceLine.setStroke(Color.BLUE);
        else
            forceLine.setStroke(Color.RED);
    }

    /**
     * hides the forceLine and the forceText that guides the user
     * adds the charged sphere  to the list of charged spheres
     * @param event mouse release
     */
    void releaseChargedSphere(MouseEvent event) {
        chargedSphere.hideDetails();
        chargedSpheresDeque.add(chargedSphere);

        chargedSphere.applyImpulseForce(new PVector((forceLine.getStartX() - forceLine.getEndX())/ChargesSettings.CONVERTER,
                (forceLine.getStartY() - forceLine.getEndY())/ChargesSettings.CONVERTER));

        root.getChildren().removeAll(forceText, forceLine);
        forceText = null;
        forceLine = null;
    }

    /**
     * the main function that provides the methods to check the mouse click and inserting new elements
     * @param event the mouse moved over the root Pane
     */
    @FXML
    void handleNewChargedSphere(MouseEvent event) {
        if(creationChargeEnabled || (isPositiveCharge ^ whichSphereIsClicked(event))){
            isPositiveCharge = whichSphereIsClicked(event);

            root.setOnMousePressed(this::initializeChargedSphere);
            root.setOnMouseDragged(this::dragForceLine);
            root.setOnMouseReleased(this::releaseChargedSphere);

            SphereLabel.setText("Click to stop creating Charged Spheres -->");
            creationChargeEnabled = false;
        } else {
            root.setOnMousePressed(null);
            root.setOnMouseDragged(null);
            root.setOnMouseReleased(null);
            SphereLabel.setText("Click to create a new Charged Sphere -->");
            creationChargeEnabled = true;
            //majorChargedSphere.setOnMouseDragged(this::dragMajorChargedSphere);
        }
    }

    /**
     * returns true if the Positive Sphere has been clicked
     * false if the Negative Sphere has been clicked
     * @param event mouse click
     * @return boolean value
     */
    boolean whichSphereIsClicked(MouseEvent event){
        return ((Sphere) event.getTarget()).getId().equals("clickablePositiveChargedSphere");
    }

    /**
     * handles the clear button to remove every charged sphere in the interface
     * @param event the mouse click on the button
     */
    @FXML
    void handleClearField(MouseEvent event){
        try {
            root.getChildren().removeAll(chargedSpheresDeque);
            chargedSpheresDeque.clear();
        }catch (NoSuchElementException e) {
            new Alert(Alert.AlertType.WARNING, "No charge to delete").showAndWait();
        }catch (RuntimeException ignore){
            new Alert(Alert.AlertType.WARNING, "Problems on deleting charges, duplicate children on the root Pane").showAndWait();
        }
    }

    /**
     * DELETE each chargedSphere that goes out of the field :)
     * haha ovviamente non fa niente di quello che c'è scritto
     */
    void checkSphereBounds() {
        double h = ChargesSettings.DEFAULT_HEIGHT;
        double w = ChargesSettings.DEFAULT_WIDTH;

        double topLimit     = root.localToScene(0, 0).getY();
        double bottomLimit  = root.localToScene(0, h).getY();
        double leftLimit    = root.localToScene(0, 0).getX();
        double rightLimit   = root.localToScene(w, 0).getX();

        for(Iterator<ChargedSphere> iter = chargedSpheresDeque.iterator(); iter.hasNext();) {
            ChargedSphere cs = iter.next();
            double radius = cs.getRadius();
            if(cs.getLocation().y + radius >= (bottomLimit)){
                removeChargedSphere(cs);
            }
            if(cs.getLocation().x + radius >= rightLimit){
                removeChargedSphere(cs);
            }
            if(cs.getLocation().y - radius <= (topLimit-52)){
                removeChargedSphere(cs);
            }
            if(cs.getLocation().x - radius <= leftLimit){
                removeChargedSphere(cs);
            }
        }
    }

    /**
     * perform a series of computations that allows the spheres to moved like they're attracted by the main sphere
     * @param dt the time fraction used in the law of motion to find the new position
     */
    void handleFieldComputations(double dt){
        chargedSpheresDeque.forEach(cs -> executorService.submit(() -> {
            PVector force;
            //other charges, only if the filter is enabled
            if(realityFilter)
                for(ChargedSphere cs1 : chargedSpheresDeque.stream().filter(cs2 -> !cs2.equals(cs)).toList()){
                    force = cs1.computeElectricForce(cs);
                    cs.computeVelocity(force, dt);
                }
            //main charge
            if(majorChargedSphere.isOnField(cs)){
                force = majorChargedSphere.computeElectricForce(cs);
                cs.computeVelocity(force, dt);
            }
        }));

        //updates the radius and the charge of the main sphere
        for(Iterator<ChargedSphere> iter = chargedSpheresDeque.iterator(); iter.hasNext();) {
            ChargedSphere cs = iter.next();
            if(majorChargedSphere.isOverlaid(cs)){
                majorChargedSphere.updateSphere(cs);
                removeChargedSphere(cs);
            }
        }
    }

    /**
     * Updates the location of the major sphere in case his position has changed without changing the location of his vector,
     * and the location of the main field.
     */
    private void updatePositions() {
        majorChargedSphere.updateLocation();
        mainField.setTranslateX(majorChargedSphere.getTranslateX());
        mainField.setTranslateY(majorChargedSphere.getTranslateY());
    }

    /**
     * tries to remove the charged sphere
     * @param cs charged sphere to remove
     */
    public void removeChargedSphere(ChargedSphere cs) {
        try{
            // removes the overlapping spheres from the list
            chargedSpheresDeque.remove(cs);
            // removes the overlapping spheres from the root
            root.getChildren().remove(cs);
        }
        catch (NoSuchElementException e){
            new Alert(Alert.AlertType.WARNING, "No charge to delete").showAndWait();
        }
    }

    /**
     * tries to stop the charged sphere
     * @param cs charged sphere to remove
     */
    public void stopChargedSphere(ChargedSphere cs) {
        try{
            cs.setVelocity(new PVector(0,0));
            // removes the sphere from the list
            chargedSpheresDeque.remove(cs);
        }
        catch (NoSuchElementException e){
            new Alert(Alert.AlertType.WARNING, "No charge to stop").showAndWait();
        }
    }

    /**
     * handle the new screen that appears when the user clicks on the main sphere
     */
    @FXML
    public void handleChangedSphereProperty() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("mainSphere-edit-view.fxml"));
            DialogPane view = loader.load();
            ChangingMainSphereController controller = loader.getController();

            // Set the sphere into the controller.
            controller.setMajorChargedSphere(majorChargedSphere);

            // Create the dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Change main Sphere's parameters");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(view);

            // Show the dialog and wait until the user closes it
            Optional<ButtonType> clickedButton = dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleRealityFilter() {
        if (realityFilter) {
            realityFilter = false;
            realityFilterButton.setText("RealityFilterDisabled");
        } else {
            realityFilter = true;
            realityFilterButton.setText("RealityFilterEnabled");
        }
    }
}