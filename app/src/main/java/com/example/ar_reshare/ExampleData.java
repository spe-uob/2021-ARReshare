package com.example.ar_reshare;

import java.util.ArrayList;
import java.util.List;

public class ExampleData {

    private static boolean initialised = false;
    private static List<User> users = null;
    private static List<Product> products = null;

    private ExampleData() {
        users = new ArrayList<>();
        products = new ArrayList<>();

        User user0 = new User("John","",1);
        users.add(user0);
        products.add(new Product("Fancy Cup", "This is a really nice fancy cup. Feel free to message me to arrange a pickup.", user0, Category.OTHER,51.45120306024447, -2.5869936269149303));
        User user1 = new User("Artur","",1);
        users.add(user1);
        products.add(new Product("Java for Beginners Book", "This is a book extremely helpful for your scotland yard coursework. Feel free to message me to arrange a pickup.", user1, Category.BOOKS, 51.45599668866024, -2.6030781306216135));
        User user2 = new User("Lingtao","",1);
        users.add(user2);
        products.add(new Product("Pink Umbrella", "This is a very pink umb.", user2, Category.CLOTHING, 51.45416805430673, -2.591828561043675));
        User user3 = new User("Hellin","",1);
        users.add(user3);
        products.add(new Product("Apple Pencil", "This is a product.", user3, Category.ELECTRONICS,51.45864853294286, -2.5853638594577193));
        User user4 = new User("Ziqian","",1);
        users.add(user4);
        products.add(new Product("Meat", "This is a product.", user4, Category.FOOD, 51.45692540090406, -2.6081114869801714));
        User user5 = new User("Arafat","",1);
        users.add(user5);
        products.add(new Product("Pink Headphones", "This is a product.", user5, Category.ELECTRONICS, 51.459040571152514, -2.6022736036387366));
    }

    public static List<User> getUsers() {
        if (!initialised) {
            // Create dummy products
            new ExampleData();
        }
        return users;
    }

    public static List<Product> getProducts() {
        if (!initialised) {
            // Create dummy products
            new ExampleData();
        }
        return products;
    }


}
