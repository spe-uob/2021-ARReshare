package com.example.ar_reshare;

public enum Category {
    CLOTHING(0,"models/hat.obj","models/purple.png"),        // RED
    SHOES(300,"models/pawn.obj","models/pink.png"),         // PINK
    ELECTRONICS(180,"models/phone.obj","models/grey.png"),   // CYAN BLUE
    BOOKS(240,"models/pawn.obj","models/pink.png"),         // NAVY
    FOOD(120,"models/burger.obj","models/burger.png"),          // GREEN
    OTHER(35,"models/cup.obj","models/pink.png");          // ORANGE

    private float hueColour;
    private String modelLocation;
    private String colorLocation;

    private Category(float colour, String modelLocation, String colorLocation) {
        this.hueColour = colour;
        this.modelLocation = modelLocation;
        this.colorLocation = colorLocation;
    }

    public float getCategoryColour() {
        return this.hueColour;
    }
}
