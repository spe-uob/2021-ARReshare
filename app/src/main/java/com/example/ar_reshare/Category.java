package com.example.ar_reshare;

import android.graphics.Color;

public enum Category {
    CLOTHING(Color.YELLOW,"models/hat.obj","models/purple.png"),        // YELLOW
    ACCESSORIES(Color.MAGENTA, "models/pawn.obj","models/pink.png"), // PINK
    ELECTRONICS(Color.CYAN,"models/phone.obj","models/grey.png"),   // CYAN
    BOOKS(Color.RED,"models/pawn.obj","models/pink.png"),         // RED
    FOOD(Color.GREEN,"models/burger.obj","models/burger.png"),          // GREEN
    OTHER(Color.rgb(255, 165, 0),"models/cup.obj","models/pink.png");          // ORANGE

    private final int hueColour;
    private String modelLocation;
    private String colorLocation;

    Category(int colour, String modelLocation, String colorLocation) {
        this.hueColour = colour;
        this.modelLocation = modelLocation;
        this.colorLocation = colorLocation;
    }

    public int getCategoryColour() {
        return this.hueColour;
    }
}
