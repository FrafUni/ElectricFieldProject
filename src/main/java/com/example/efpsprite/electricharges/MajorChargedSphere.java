package com.example.efpsprite.electricharges;

import com.example.efpsprite.commons.PVector;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class MajorChargedSphere extends ChargedSphere{
    private double fieldRadius;

    public MajorChargedSphere(String name, double radius, int sign, PhongMaterial phongMaterial, double effectiveRadius){
        super(name, radius);
        this.fieldRadius = effectiveRadius;
        setCharge(ChargesSettings.ASSUMED_MAJOR_CHARGE);
        setPhongMaterialSphere(phongMaterial);
    }

    public double getEffectiveRadius() {
        return fieldRadius;
    }

    public void setEffectiveRadius(double effectiveRadius) {
        if(effectiveRadius >= 0 || effectiveRadius <= ChargesSettings.MAX_FIELD_RADIUS)
            this.fieldRadius = effectiveRadius;
    }

    /**
     * @param cs the charged sphere to check
     * @return true if the cs sphere is on the main sphere
     */
    public boolean isOverlaid (ChargedSphere cs){
        return PVector.distance(new PVector(cs.getTranslateX(), cs.getTranslateY()), getLocation()) < getRadius();
    }

    /**
     * @param chargedSphere is the charged sphere analyzed
     * @return true if the chargedSphere is in the electric field
     */
    public boolean isOnField (ChargedSphere chargedSphere){
        return (PVector.distance(getLocation(), chargedSphere.getLocation())) < fieldRadius;
    }

    /**
     * updates the radius and the charge of the main sphere every time that a charged sphere is absorbed
     * @param cs the charged sphere used to calculate the new charge and radius
     */
    public void updateMainSphere (ChargedSphere cs){
        this.setCharge(this.getCharge() + cs.getCharge());
        this.setRadius(this.getRadius() + cs.getSign() * this.getSign());
    }

    /**
     * Compute the electric field for a specific charged sphere and modifies the speed of the sphere as the result of the applied force
     * @param cs the charged sphere whose speed has to be changed
     * @param dt the time interval between this and the last computation on the sphere
     * @return a new ForceVector that is the new velocity vector of the sphere
     */
    public PVector computeElectricForce (ChargedSphere cs,double dt){
        if (!isOnField(cs)) {
            return PVector.ZERO;
        }

        double r = PVector.distance(this.getLocation(), cs.getLocation());
        double rX = this.getLocation().x - cs.getTranslateX();
        double rY = this.getLocation().y - cs.getTranslateY();
        double EX = ChargesSettings.COULOMB_CONSTANT * this.getCharge() * (rX) / Math.pow(r, 3);
        double EY = ChargesSettings.COULOMB_CONSTANT * this.getCharge() * (rY) / Math.pow(r, 3);

        // set to negative if the spheres have the same charge
        double FX = cs.getCharge() * EX * (-1);
        double FY = cs.getCharge() * EY * (-1);
        return new PVector(FX, FY);
    }

    public PVector calculateOrbitVector(ChargedSphere cs){
        // Calculate the angle between the x-axis and the line connecting charges Q and q
        double theta = Math.atan2(cs.getTranslateY() - this.getTranslateY(),
                                 cs.getTranslateX() - this.getTranslateX());
     //   PVector.angleBetween(this.getLocation(), cs.getLocation());
        // distance between the charge
        double d = PVector.distance(this.getLocation(), cs.getLocation());
        // magnitude of the electric force using Coulomb's law
        double F = ChargesSettings.COULOMB_CONSTANT * Math.abs( this.getCharge() * cs.getCharge()) / Math.pow(d, 3);

        System.out.println("theta: " + theta + " d: " + d + " F:" + F);

        return new PVector((cs.getLocation().x
                - F*Math.cos(theta))/ChargesSettings.CONVERTER,
                        (cs.getLocation().y - F*Math.sin(theta))/ChargesSettings.CONVERTER);
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

