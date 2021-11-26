package com.example.ar_reshare;

public enum Category {
    CLOTHING(0),        // RED
    SHOES(300),         // PINK
    ELECTRONICS(180),   // CYAN BLUE
    BOOKS(240),         // NAVY
    FOOD(120),          // GREEN
    OTHER(60);          // YELLOW

    private float hueColour;

    private Category(float colour) {
        this.hueColour = colour;
    }

    public float getCategoryColour() {
        return this.hueColour;
    }
}
