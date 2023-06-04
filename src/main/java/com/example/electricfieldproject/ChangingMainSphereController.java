package com.example.electricfieldproject;

import com.example.electricfieldproject.electricharges.ChargesSettings;
import com.example.electricfieldproject.electricharges.MajorChargedSphere;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class ChangingMainSphereController {
    @FXML private ComboBox<String> signComboBox;
    @FXML private Slider chargeSlider;
    @FXML private Slider effectiveRatioSlider;
    @FXML private Slider sphereRadiusSlider;
    @FXML private Label chargeLabel;
    @FXML private Label effectiveRatioLabel;
    @FXML private Label sphereRadiusLabel;
    @FXML private Label signLabel;
    MajorChargedSphere majorChargedSphere;

    /**
     * Aggangio dei listener in ogni dato
     */
    @FXML
    public void initialize() {
        effectiveRatioSlider.setMin(50);
        effectiveRatioSlider.setMax(350);
        effectiveRatioSlider.setMajorTickUnit((effectiveRatioSlider.getMax() - effectiveRatioSlider.getMin())/5);

        signComboBox.setItems(FXCollections.observableArrayList("Positive", "Negative"));
        sphereRadiusSlider.setMin(ChargesSettings.MAIN_SPHERE_RADIUS_MINLIMIT);
        sphereRadiusSlider.setMax(ChargesSettings.MAIN_SPHERE_RADIUS_MAXLIMIT);
        sphereRadiusSlider.setMajorTickUnit((sphereRadiusSlider.getMax() - sphereRadiusSlider.getMin())/5);

        chargeSlider.setMin(ChargesSettings.MAIN_SPHERE_CHARGE_MINLIMIT*(10E07));
        chargeSlider.setMax(ChargesSettings.MAIN_SPHERE_CHARGE_MAXLIMIT*(10E07));
        chargeSlider.setMajorTickUnit((chargeSlider.getMax() - chargeSlider.getMin())/5);
    }

    public void setListener(){
        effectiveRatioSlider.setValue(majorChargedSphere.getEffectiveRadius());
        sphereRadiusSlider.setValue(majorChargedSphere.getRadius());
        chargeSlider.setValue(majorChargedSphere.getCharge());
        signComboBox.setValue(signToString(majorChargedSphere.getSign()));

        sphereRadiusSlider.valueProperty().addListener((observable, oldValue, newValue) -> majorChargedSphere.setRadius((Double) newValue));
        chargeSlider.valueProperty().addListener((observable, oldValue, newValue) -> majorChargedSphere.setCharge((Double) newValue*(10E-07)));
        effectiveRatioSlider.valueProperty().addListener((observable, oldValue, newValue) -> majorChargedSphere.setEffectiveRadius((Double) newValue));

        signComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            majorChargedSphere.setCharge(signStringToDouble(newValue)*majorChargedSphere.getCharge());
        });
    }

    /**
     * @param sign must be equals to Negative if is a negative charge
     * @return Returns -1 if sign is equals to Negative, otherwise +1
     */
    double signStringToDouble(String sign){
        if(sign == null)
            return majorChargedSphere.getSign();

        if(sign.equals("Negative"))
            return -1;
        else
            return 1;
    }

    /**
     * @param sign integer positive or negative
     * @return Returns Positive if the sign is positive, Negative otherwise
     */
    String signToString(double sign){
        if(sign > 0)
            return "Positive";
        else
            return "Negative";
    }

    void update() {
        chargeLabel.textProperty().set(String.valueOf(majorChargedSphere.getCharge()));
        effectiveRatioLabel.textProperty().set(String.valueOf(majorChargedSphere.getEffectiveRadius()));
        signLabel.textProperty().set(signToString(majorChargedSphere.getSign()));
        sphereRadiusLabel.textProperty().set(String.valueOf(majorChargedSphere.getRadius()));
    }

    public void setMajorChargedSphere(MajorChargedSphere majorChargedSphere){
        this.majorChargedSphere = majorChargedSphere;
        System.out.println(majorChargedSphere);
        update();
        setListener();
    }
}