package com.example.electricfieldproject.forceelement;

import com.example.electricfieldproject.commons.PVector;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ForceText extends Text{
    public ForceText() {
        super("");
        this.setFont(new Font(20));
        this.setFill(Color.BLACK);
    }

    public ForceText(String text) {
        super(text);
        this.setFont(new Font(20));
        this.setFill(Color.BLACK);
    }

    /**
     * anchors the text to the specified ForceVector owner
     * @param fv the owner
     */
    public void setOwner(PVector fv){
        this.setX(fv.x);
        this.setY(fv.y);
    }
}