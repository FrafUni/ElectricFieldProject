package com.example.electricfieldproject.electricharges;

import com.example.electricfieldproject.commons.PVector;

/**
 * The default settings for the charges
 */
public class ChargesSettings {
    public static double ASSUMED_MASS = 9.11; // *10^-31Kg
    public static double ASSUMED_MINOR_CHARGE = 1.6E-07;// *10^-19C
    public static double ASSUMED_MAJOR_CHARGE = 16E-07;// *10^-19C
    public static double NORMALIZER = 1e5;
    public static double NANOCONVERTER = 1e9;
    public static double COULOMB_CONSTANT = 9*Math.pow(10, 9);
    public static double MAIN_SPHERE_CHARGE_MINLIMIT = -32E-7;
    public static double MAIN_SPHERE_CHARGE_MAXLIMIT = 32E-7;
    public static double MAIN_SPHERE_RADIUS_MINLIMIT = 20;
    public static double MAIN_SPHERE_RADIUS_MAXLIMIT = 40;
    public static int DEFAULT_FIELD_RADIUS = 265;
    public static int CONVERTER = 10;
    public static PVector DEFAULT_POSITION = new PVector(ChargesSettings.DEFAULT_WIDTH/2, ChargesSettings.DEFAULT_WIDTH/2);
    public static double DEFAULT_WIDTH  = 800;
    public static double DEFAULT_HEIGHT  = 494;
    public static double MAX_FIELD_RADIUS = 300;
}
