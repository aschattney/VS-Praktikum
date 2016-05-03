package de.schattney.app;

import de.hochschuledarmstadt.model.RequiredMaterial;

public class Material {

    public static final String COLOR_RED = "red";
    public static final String COLOR_BLUE = "blue";
    public static final String COLOR_YELLOW = "yellow";

    private int amountRed;
    private int amountBlue;
    private int amountYellow;

    private int maximumRed;
    private int maximumBlue;
    private int maximumYellow;

    private static final Object LOCK_OBJ = new Object();

    public Material(int amountRed, int amountBlue, int amountYellow){

        this.maximumRed = amountRed;
        this.maximumBlue = amountBlue;
        this.maximumYellow = amountYellow;

        this.amountRed = amountRed;
        this.amountBlue = amountBlue;
        this.amountYellow = amountYellow;

    }

    public void use(String color){
        synchronized (LOCK_OBJ) {
            boolean found = false;
            if (color.equals(COLOR_RED)) {
                amountRed--;
                found = true;
            } else if (color.equals(COLOR_BLUE)) {
                amountBlue--;
                found = true;
            } else if (color.equals(COLOR_YELLOW)) {
                amountYellow--;
                found = true;
            }
            if (!found)
                throw new IllegalArgumentException(String.format("unknown color: %s"));
        }
    }

    public boolean isEnoughColorAvailable(RequiredMaterial requiredMaterial){
        synchronized (LOCK_OBJ) {
            if (amountRed - requiredMaterial.getRed() < 0)
                return false;
            if (amountBlue - requiredMaterial.getBlue() < 0)
                return false;
            if (amountYellow - requiredMaterial.getYellow() < 0)
                return false;
            return true;
        }
    }

    public void refill() {
        synchronized (LOCK_OBJ) {
            amountRed = maximumRed;
            amountYellow = maximumYellow;
            amountBlue = maximumBlue;
        }
    }

    public RequiredMaterial getColorFillLevel() {
        synchronized (LOCK_OBJ) {
            return new RequiredMaterial(amountRed, amountBlue, amountYellow);
        }
    }
}
