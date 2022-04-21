package com.example.ar_reshare;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExampleData {

    private static boolean initialised = false;
    private static List<User> users = null;
    private static List<Product> products = null;
    private static List<Chat> chats = null;

    private ExampleData() {
        users = new ArrayList<>();
        products = new ArrayList<>();
        chats = new ArrayList<>();



        User user0 = new User("John","", 0);
        users.add(user0);
        products.add(new Product("Fancy Cup", "This is a really nice fancy cup. Feel free to message me to arrange a pickup.", user0, Category.OTHER,51.45120306024447, -2.5869936269149303));
        user0.setProfileIcon(R.drawable.jon);

//
//        User user1 = new User("Artur","", 1);
//        user1.setProfileIcon(R.drawable.artur_profile_icon);
//        users.add(user1);
//        products.add(new Product("Matrix Trenchcoat", "I dodge bugs like the Matrix, now you can to!", user1, Category.CLOTHING, 51.456070226943865, -2.602992299931959));
//        products.get(1).addImages(R.drawable.coat);
//
//        User user2 = new User("Lingtao","", 1);
//        users.add(user2);
//        products.add(new Product("Pink Umbrella", "Who wouldn't want this fashionable pink umbrella?", user2, Category.ACCESSORIES, 51.45416805430673, -2.591828561043675));
//        user2.setProfileIcon(R.drawable.lingtao_profile_icon);
//        products.get(2).addImages(R.drawable.umbrella2);
//
//
//        User user3 = new User("Hellin","", 1);
//        users.add(user3);
//        products.add(new Product("Apple Pencil", "Try out this Apple pencil on your new iPad now!", user3, Category.ELECTRONICS,51.45864853294286, -2.5853638594577193));
//        user3.setProfileIcon(R.drawable.user);
//        products.get(3).addImages(R.drawable.applepencil1);
//        products.get(3).addImages(R.drawable.applepencil2);
//
//        User user4 = new User("Ziqian","", 1);
//        users.add(user4);
//        products.add(new Product("Meat", "Leftovers from my lit bbq party last night!", user4, Category.FOOD, 51.45692540090406, -2.6081114869801714));
//        user4.setProfileIcon(R.drawable.ziqian);
//        products.get(4).addImages(R.drawable.meal1);
//        products.get(4).addImages(R.drawable.meal2);
//
//        User user5 = new User("Arafat","",1);
//        user5.setProfileIcon(R.drawable.arfi_profile_icon);
//        users.add(user5);
//        products.add(new Product("PCs For Dummies", "Helped me", user5, Category.BOOKS, 51.459040571152514, -2.6022736036387366));
//        products.get(5).addImages(R.drawable.book);

//        User user0 = new User("John","", 0);
//        users.add(user0);
//        products.add(new Product("Fancy Cup", "This is a really nice fancy cup. Feel free to message me to arrange a pickup.", user0, Category.OTHER,51.45120306024447, -2.5869936269149303));
//        user0.setProfileIcon(R.drawable.jon);
//        products.get(0).addImages(R.drawable.cup);
//        products.get(0).addImages(R.drawable.cup2);
//
//        User user1 = new User("Artur","", 1);
//        user1.setProfileIcon(R.drawable.artur_profile_icon);
//        users.add(user1);
//        products.add(new Product("Matrix Trenchcoat", "I dodge bugs like the Matrix, now you can to!", user1, Category.CLOTHING, 51.456070226943865, -2.602992299931959));
//        products.get(1).addImages(R.drawable.coat);
//
//        User user2 = new User("Lingtao","", 1);
//        users.add(user2);
//        products.add(new Product("Pink Umbrella", "Who wouldn't want this fashionable pink umbrella?", user2, Category.ACCESSORIES, 51.45416805430673, -2.591828561043675));
//        user2.setProfileIcon(R.drawable.lingtao_profile_icon);
//        products.get(2).addImages(R.drawable.umbrella2);
//
//
//        User user3 = new User("Hellin","", 1);
//        users.add(user3);
//        products.add(new Product("Apple Pencil", "Try out this Apple pencil on your new iPad now!", user3, Category.ELECTRONICS,51.45864853294286, -2.5853638594577193));
//        user3.setProfileIcon(R.drawable.user);
//        products.get(3).addImages(R.drawable.applepencil1);
//        products.get(3).addImages(R.drawable.applepencil2);
//
//        User user4 = new User("Ziqian","", 1);
//        users.add(user4);
//        products.add(new Product("Meat", "Leftovers from my lit bbq party last night!", user4, Category.FOOD, 51.45692540090406, -2.6081114869801714));
//        user4.setProfileIcon(R.drawable.ziqian);
//        products.get(4).addImages(R.drawable.meal1);
//        products.get(4).addImages(R.drawable.meal2);
//
//        User user5 = new User("Arafat","",1);
//        user5.setProfileIcon(R.drawable.arfi_profile_icon);
//        users.add(user5);
//        products.add(new Product("PCs For Dummies", "Helped me", user5, Category.BOOKS, 51.459040571152514, -2.6022736036387366));
//        products.get(5).addImages(R.drawable.book);
//

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
//        List<Message> messages1 = new ArrayList<>();
//        Message message1 = new Message("hello!!!", user1, simpleDateFormat.format(new Date()));
//        messages1.add(message1);
//        Message message2 = new Message("hi!!!", user0, simpleDateFormat.format(new Date()));
//        messages1.add(message2);
//        Chat chat1 = new Chat(user0,user1,messages1, products.get(1));
//        chats.add(chat1);
//        List<Message> messages2 = new ArrayList<>();
//        Message message3 = new Message("how are you", user2, simpleDateFormat.format(new Date()));
//        messages2.add(message3);
//        Message message4 = new Message("I am fine thank you!", user0, simpleDateFormat.format(new Date()));
//        messages2.add(message4);
//        Chat chat2 = new Chat(user0,user2,messages2,products.get(2));
//        chats.add(chat2);

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

    public static List<Chat> getChats() {
        if (!initialised) {
            // Create dummy products
            new ExampleData();
        }
        return chats;
    }
}
