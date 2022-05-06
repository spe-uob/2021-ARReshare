package com.example.ar_reshare;
import android.graphics.Color;
import java.util.Arrays;
import java.util.List;

public enum Category {
    CLOTHING(Color.rgb(255, 165, 0),"models/hat.obj","models/purple.png",
            R.drawable.clothing_icon),        // ORANGE
    ACCESSORIES(Color.MAGENTA, "models/pawn.obj","models/pink.png",
            R.drawable.accessories_icon), // PINK
    ELECTRONICS(Color.rgb(0, 0, 139),"models/phone.obj","models/grey.png",
            R.drawable.electronics_icon),   // CYAN
    BOOKS(Color.RED,"models/pawn.obj","models/pink.png",
            R.drawable.books_icon),         // RED
    HOUSEHOLD(Color.GREEN,"models/burger.obj","models/burger.png",
            R.drawable.household_icon),          // GREEN
    OTHER(Color.rgb(204, 136, 128),"models/cup.obj","models/pink.png",
            R.drawable.other_icon);          // PURPLE

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

    public static List<Category> getCategories() {
        return Arrays.asList(Category.values());
    }

    public int getCategoryIcon() {
        return this.categoryIcon;
    }

    // Given a category id returns the corresponding category
    public static Category getCategoryById(int id) {
        switch (id) {
            case 1: return Category.OTHER;
            case 2: return Category.CLOTHING;
            case 3: return Category.ACCESSORIES;
            case 4: return Category.ELECTRONICS;
            case 5: return Category.BOOKS;
            case 6: return Category.HOUSEHOLD;
            default: return Category.OTHER;
        }
    }
}
