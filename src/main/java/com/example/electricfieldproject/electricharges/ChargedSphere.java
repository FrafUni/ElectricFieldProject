package com.example.electricfieldproject.electricharges;

import com.example.electricfieldproject.commons.PVector;
import com.example.electricfieldproject.commons.Sprite;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

import java.util.Optional;

/**
 * This class limits the use of Sprite
 * in fact in the node list of the children must exist one Sphere
 * However, to avoid runtime errors, each method
 * takes precautions in case there are no (or more) spheres in the view.
 */
public class ChargedSphere extends Sprite {
    private double charge;

    public ChargedSphere(String name, double radius) {
        super(name, new Sphere(radius));
    }

    public ChargedSphere(String name, double radius, double charge, PhongMaterial phongMaterial) {
        super(name, new Sphere(radius));
        setCharge(charge);
        setPhongMaterialSphere(phongMaterial);
    }

    public ChargedSphere(String name, double radius, double charge, PVector center, PhongMaterial phongMaterial) {
        super(name, new Sphere(radius));
        setCharge(charge);
        setCenter(center);
        setPhongMaterialSphere(phongMaterial);
    }

    /**
     * Method reserved for Sphere, set the radius of the sphere in the first child
     *
     * @param radius new radius
     */
    public void setRadius(double radius) {
        Sphere sphere = getSphere();
        if (sphere != null && radius >= ChargesSettings.MAIN_SPHERE_RADIUS_MINLIMIT && radius <= ChargesSettings.MAIN_SPHERE_RADIUS_MAXLIMIT) {
            sphere.setRadius(radius);
        }
    }

    /**
     * Returns the radius of the first sphere in the children list
     *
     * @return radius IF THE SPHERE IS NOT IN THE LIST RETURNS 0
     */
    public double getRadius() {
        Optional<Sphere> sphere = Optional.ofNullable(getSphere());
        return sphere.map(Sphere::getRadius).orElse(0.0);
    }

    public void setCharge(double charge) {
        if (charge >= ChargesSettings.MAIN_SPHERE_CHARGE_MINLIMIT && charge <= ChargesSettings.MAIN_SPHERE_CHARGE_MAXLIMIT){
            if(charge < 0)
                setPhongMaterialSphere(new PhongMaterial(Color.web("#ff3232")));
            else
                setPhongMaterialSphere(new PhongMaterial(Color.web("#3337ff")));

            this.charge = charge;
        }
    }

    public double getCharge() {
        return charge;
    }

    public double getSign(){
        return Math.signum(getCharge());
    }

    /**
     * Replace the first Sphere in the View to the new Sphere
     * if the radius is between 5 and 50
     * @param sphere to insert
     */
    public void setSphere(Sphere sphere) {
        if (sphere.getRadius() >= 5 && sphere.getRadius() <= 50) {
            getChildren().remove(getSphere());
            getChildren().add(sphere);
        }
    }

    /**
     * Returns the first sphere in the children if there is
     * @return Sphere or Null
     */
    public Sphere getSphere() {
        return (Sphere) getChildren().get(0);
    }

    public void setCenter (PVector center){
        if (center.x < ChargesSettings.DEFAULT_WIDTH && center.x > 0 && center.y < ChargesSettings.DEFAULT_WIDTH && center.y > 0) {
            setLocation(center);
            update();
        }
    }

    public void setPhongMaterialSphere (PhongMaterial phongMaterial){
        Sphere sphere = getSphere();
        if (sphere != null) {
            sphere.setMaterial(phongMaterial);
        }
    }

    @Override
    public String toString() {
        return "ChargedSphere{" +
                "charge=" + charge +
                '}' + "\n\tSprite{" +
                "location=" + getLocation() +
                ", velocity=" + getVelocity() +
                ", acceleration=" + getAcceleration() +
                ", mass=" + getMass() +
                '}';
    }
}
