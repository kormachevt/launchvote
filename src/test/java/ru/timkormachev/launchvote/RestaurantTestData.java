package ru.timkormachev.launchvote;

import ru.timkormachev.launchvote.model.Dish;
import ru.timkormachev.launchvote.model.Restaurant;

import java.util.List;

import static ru.timkormachev.launchvote.TestUtil.generateRandom;
import static ru.timkormachev.launchvote.TestUtil.generateRandomLong;
import static ru.timkormachev.launchvote.model.AbstractBaseEntity.START_SEQ;

public class RestaurantTestData {
    public static final int ALFA_ID = START_SEQ + 2;
    public static final int OMEGA_ID = START_SEQ + 3;
    public static final int ALFA_1_ID = START_SEQ + 4;
    public static final int ALFA_2_ID = START_SEQ + 5;
    public static final int OMEGA_1_ID = START_SEQ + 6;
    public static final int OMEGA_2_ID = START_SEQ + 7;


    public static final Restaurant ALFA = new Restaurant(ALFA_ID).setName("Alfa");
    public static final Restaurant OMEGA = new Restaurant(OMEGA_ID).setName("Omega");
    public static final Dish ALFA_1 = new Dish(ALFA_1_ID).setDescription("Alfa_1").setPrice(201L).setRestaurant(ALFA);
    public static final Dish ALFA_2 = new Dish(ALFA_2_ID).setDescription("Alfa_2").setPrice(202L).setRestaurant(ALFA);
    public static final Dish OMEGA_1 = new Dish(OMEGA_1_ID).setDescription("Omega_1").setPrice(301L).setRestaurant(OMEGA);
    public static final Dish OMEGA_2 = new Dish(OMEGA_2_ID).setDescription("Omega_2").setPrice(302L).setRestaurant(OMEGA);

    public static TestMatcher<Restaurant> RESTAURANT_MATCHER = TestMatcher.usingFieldsWithIgnoringAssertions(Restaurant.class, "dishes");
    public static TestMatcher<Restaurant> RESTAURANTS_WITH_DISHES_MATCHER = TestMatcher.usingFieldsWithIgnoringAssertionsRecursive(Restaurant.class, "dishes.restaurant");
    public static TestMatcher<Dish> DISH_MATCHER = TestMatcher.usingFieldsWithIgnoringAssertions(Dish.class, "restaurant");
    public static TestMatcher<Dish> DISHES_MATCHER = TestMatcher.usingFieldsWithIgnoringAssertions(Dish.class, "restaurant", "id");

    static {
        ALFA.setDishes(List.of(ALFA_1, ALFA_2));
        OMEGA.setDishes(List.of(OMEGA_1, OMEGA_2));
    }

    public static Dish getUpdated() {
        return new Dish()
                .setDescription(generateRandom(10))
                .setPrice(generateRandomLong(1000));
    }

}
