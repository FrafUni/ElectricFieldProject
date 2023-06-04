package com.example.electricfieldproject.forceelement;

import com.example.electricfieldproject.commons.PVector;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class ForceLine extends Line {
    public ForceLine() {
        this.setStroke(Color.RED);
        this.setStrokeWidth(2);
        this.setVisible(false);
    }

    public ForceLine(double v, double v1, double v2, double v3) {
        super(v, v1, v2, v3);
        this.setStroke(Color.RED);
        this.setStrokeWidth(2);
        this.setVisible(false);
    }

    /**
     * sets the start and the end of the line with two ForceVector
     * @param start the starting point of the line
     */
    public void setStart(PVector start){
        this.setStartX(start.x);
        this.setStartY(start.y);
    }

    /**
     * sets the end of the line with two ForceVector
     * @param end the ending point of the line
     */
    public void setEnd(PVector end){
        this.setEndX(end.x);
        this.setEndY(end.y);
    }

    /**
     * sets the start and the end of the line with two ForceVector
     * @param start the starting point of the line
     * @param end the ending point of the line
     */
    public void setLink(PVector start, PVector end){
        this.setStartX(start.x);
        this.setStartY(start.y);
        this.setEndX(end.x);
        this.setEndY(end.y);
    }

    /**
     * @param forceLine the line to get the hypotenuse
     * @return the length of the line
     */
    public static double getHypotenuse(Line forceLine){
        return PVector.distance(new PVector(forceLine.getStartX(), forceLine.getStartY()),
                                    new PVector(forceLine.getEndX(), forceLine.getEndY()));
    }
}