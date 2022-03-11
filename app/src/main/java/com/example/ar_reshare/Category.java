package com.example.ar_reshare;

import android.graphics.Color;

public enum Category {
    CLOTHING(Color.rgb(255, 165, 0),"models/hat.obj","models/purple.png",
            R.drawable.ic_baseline_clothing_24),        // ORANGE
    ACCESSORIES(Color.MAGENTA, "models/pawn.obj","models/pink.png",
            R.drawable.ic_baseline_watch_24), // PINK
    ELECTRONICS(Color.CYAN,"models/phone.obj","models/grey.png",
            R.drawable.ic_baseline_computer_24),   // CYAN
    BOOKS(Color.RED,"models/pawn.obj","models/pink.png",
            R.drawable.ic_baseline_menu_book_24),         // RED
    FOOD(Color.GREEN,"models/burger.obj","models/burger.png",
            R.drawable.ic_baseline_fastfood_24),          // GREEN
    OTHER(Color.rgb(30, 19, 34),"models/cup.obj","models/pink.png",
            R.drawable.ic_baseline_handyman_24);          // PURPLE

    private final int hueColour;
    private String modelLocation;
    private String colorLocation;
    private int categoryIcon;

    Category(int colour, String modelLocation, String colorLocation, int categoryIcon) {
        this.hueColour = colour;
        this.modelLocation = modelLocation;
        this.colorLocation = colorLocation;
        this.categoryIcon = categoryIcon;
    }

    public int getCategoryColour() {
        return this.hueColour;
    }
    public int getCategoryIcon() {
        return this.categoryIcon;
    }
}
