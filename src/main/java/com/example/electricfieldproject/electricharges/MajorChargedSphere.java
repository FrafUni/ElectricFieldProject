package com.example.electricfieldproject.electricharges;

import com.example.electricfieldproject.commons.PVector;
import javafx.scene.paint.PhongMaterial;

public class MajorChargedSphere extends ChargedSphere{
    private double fieldRadius;

    public MajorChargedSphere(String name, double radius, PhongMaterial phongMaterial, double effectiveRadius){
        super(name, radius);
        this.fieldRadius = effectiveRadius;
        setCharge(ChargesSettings.ASSUMED_MAJOR_CHARGE);
        setPhongMaterialSphere(phongMaterial);
    }

    public double getFieldRadius() {
        return fieldRadius;
    }

    public void setFieldRadius(double effectiveRadius) {
        if(effectiveRadius >= 0 || effectiveRadius <= ChargesSettings.MAX_FIELD_RADIUS)
            this.fieldRadius = effectiveRadius;
    }

    /**
     * @param cs the charged sphere to check
     * @return true if the cs sphere is on the main sphere
     */
    public boolean isOverlaid (ChargedSphere cs){
        return PVector.distance(cs.getLocation(), getLocation()) < getRadius();
    }

    /**
     * @param cs is the charged sphere analyzed
     * @return true if the chargedSphere is in the electric field
     */
    public boolean isOnField (ChargedSphere cs){
        return (PVector.distance(getLocation(), cs.getLocation())) < fieldRadius;
    }

    /**
     * updates the radius and the charge of the sphere every time that a charged sphere collides with this
     * @param cs the charged sphere used to calculate the new charge and radius
     */
    public <T extends ChargedSphere> void updateSphere (T cs){
        this.setCharge(this.getCharge() + cs.getCharge());
        this.setRadius(this.getRadius() + cs.getSign() * this.getSign());
    }

    @Override
    public String toString() {
        return "MajorChargedSphere{" +
                "fieldRadius=" + fieldRadius +
                '}' + "\n\tChargedSphere{" +
                "charge=" + getCharge() +
                '}' + "\n\tSprite{" +
                "location=" + getLocation() +
                ", velocity=" + getVelocity() +
                ", acceleration=" + getAcceleration() +
                ", mass=" + getMass() +
                '}';
    }
}

