package com.example.ar_reshare;

public enum Category {
    CLOTHING(0,"models/pawn.obj"),        // RED
    SHOES(300,"models/pawn.obj"),         // PINK
    ELECTRONICS(180,"models/pawn.obj"),   // CYAN BLUE
    BOOKS(240,"models/pawn.obj"),         // NAVY
    FOOD(120,"models/pawn.obj"),          // GREEN
    OTHER(35,"models/pawn.obj");          // ORANGE

    private float hueColour;
    private String modelLocation;

    private Category(float colour, String modelLocation) {
        this.hueColour = colour;
        this.modelLocation = modelLocation;
    }

    public float getCategoryColour() {
        return this.hueColour;
    }
}
