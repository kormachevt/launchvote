package ru.timkormachev.launchvote.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.timkormachev.launchvote.AbstractControllerTest;
import ru.timkormachev.launchvote.exception.NotFoundException;
import ru.timkormachev.launchvote.model.Dish;
import ru.timkormachev.launchvote.model.Restaurant;
import ru.timkormachev.launchvote.repositories.DishRepository;
import ru.timkormachev.launchvote.repositories.RestaurantRepository;
import ru.timkormachev.launchvote.web.json.JsonUtil;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.timkormachev.launchvote.RestaurantTestData.*;
import static ru.timkormachev.launchvote.TestUtil.readFromJson;
import static ru.timkormachev.launchvote.TestUtil.userHttpBasic;
import static ru.timkormachev.launchvote.UserTestData.ADMIN;
import static ru.timkormachev.launchvote.UserTestData.USER;
import static ru.timkormachev.launchvote.exception.ErrorType.VALIDATION_ERROR;

class RestaurantsControllerTest extends AbstractControllerTest {

    private static final String USERS_URL = RestaurantsController.USERS_URL + '/';
    private static final String ADMIN_URL = RestaurantsController.ADMIN_URL + '/';

    @Autowired
    RestaurantRepository repository;

    @Autowired
    DishRepository dishRepository;

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(USERS_URL)
                        .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_MATCHER.contentJson(ALFA, OMEGA));
    }

    @Test
    void getAllWithDishes() throws Exception {
        perform(MockMvcRequestBuilders.get(USERS_URL + "/with-dishes")
                        .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANTS_WITH_DISHES_MATCHER.contentJson(ALFA, OMEGA));
    }

    @Test
    @DirtiesContext
    void add() throws Exception {
        Restaurant newRestaurant = new Restaurant().setName("newRestaurant");
        ResultActions action = perform(MockMvcRequestBuilders.post(ADMIN_URL)
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .with(userHttpBasic(ADMIN))
                                               .content(JsonUtil.writeValue(newRestaurant)))
                .andDo(print())
                .andExpect(status().isCreated());
        Restaurant created = readFromJson(action, Restaurant.class);
        int newId = created.id();
        newRestaurant.setId(newId);
        RESTAURANT_MATCHER.assertMatch(created, newRestaurant);
        RESTAURANT_MATCHER.assertMatch(repository.findOrThrowById(newId), newRestaurant);
    }

    @Test
    @DirtiesContext
    void addInvalid() throws Exception {
        Restaurant newRestaurant = new Restaurant().setName("newRestaurant");
        ResultActions action = perform(MockMvcRequestBuilders.post(ADMIN_URL)
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .with(userHttpBasic(ADMIN))
                                               .content(JsonUtil.writeValue(newRestaurant)))
                .andDo(print())
                .andExpect(status().isCreated());
        Restaurant created = readFromJson(action, Restaurant.class);
        int newId = created.id();
        newRestaurant.setId(newId);
        RESTAURANT_MATCHER.assertMatch(created, newRestaurant);
        RESTAURANT_MATCHER.assertMatch(repository.findOrThrowById(newId), newRestaurant);
    }

    @Test
    void addForbidden() throws Exception {
        Restaurant newRestaurant = new Restaurant().setName("");
        perform(MockMvcRequestBuilders.post(ADMIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(userHttpBasic(ADMIN))
                        .content(JsonUtil.writeValue(newRestaurant)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(USERS_URL + ALFA_ID)
                        .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andDo(print())
                // https://jira.spring.io/browse/SPR-14472
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANTS_WITH_DISHES_MATCHER.contentJson(ALFA));
    }

    @Test
    @DirtiesContext
    void update() throws Exception {
        Restaurant updated = new Restaurant(ALFA)
                .setName("newName");
        updated.setId(null);
        perform(MockMvcRequestBuilders.put(ADMIN_URL + ALFA_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(userHttpBasic(ADMIN))
                        .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNoContent());
        updated.setId(ALFA_ID);
        RESTAURANT_MATCHER.assertMatch(repository.findOrThrowById(ALFA_ID), updated);
    }

    @Test
    void updateInvalid() throws Exception {
        Restaurant updated = new Restaurant(ALFA)
                .setName("");
        updated.setId(null);
        perform(MockMvcRequestBuilders.put(ADMIN_URL + ALFA_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeValue(updated))
                        .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void updateForbidden() throws Exception {
        Restaurant updated = new Restaurant(ALFA)
                .setName("newName");
        updated.setId(null);
        perform(MockMvcRequestBuilders.put(ADMIN_URL + ALFA_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(userHttpBasic(USER))
                        .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DirtiesContext
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(ADMIN_URL + ALFA_ID)
                        .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(
                NotFoundException.class, () -> repository.findOrThrowById(ALFA_ID));
    }

    @Test
    void deleteForbidden() throws Exception {
        perform(MockMvcRequestBuilders.delete(ADMIN_URL + ALFA_ID)
                        .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void getDishes() throws Exception {
        perform(MockMvcRequestBuilders.get(USERS_URL + ALFA_ID + "/dishes")
                        .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DISH_MATCHER.contentJson(ALFA_1, ALFA_2));
    }

    @Test
    @DirtiesContext
    void createDishes() throws Exception {
        List<Dish> newDishes = List.of(getUpdated(), getUpdated()).stream()
                .sorted(Comparator.comparing(Dish::getDescription))
                .collect(Collectors.toList());
        perform(MockMvcRequestBuilders.put(ADMIN_URL + ALFA_ID + "/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(userHttpBasic(ADMIN))
                        .content(JsonUtil.writeValue(newDishes)))
                .andDo(print())
                .andExpect(status().isNoContent());
        List<Dish> updatedDishes = dishRepository.findAllByRestaurantOrderByDescription(ALFA);
        DISHES_MATCHER.assertMatch(updatedDishes, newDishes);
    }

    @Test
    void createDishesInvalid() throws Exception {
        List<Dish> newDishes = List.of(getUpdated().setDescription(""), getUpdated().setPrice(null)).stream()
                .sorted(Comparator.comparing(Dish::getDescription))
                .collect(Collectors.toList());
        perform(MockMvcRequestBuilders.put(ADMIN_URL + ALFA_ID + "/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(userHttpBasic(ADMIN))
                        .content(JsonUtil.writeValue(newDishes)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void createDishesForbidden() throws Exception {
        List<Dish> newDishes = List.of(getUpdated(), getUpdated()).stream()
                .sorted(Comparator.comparing(Dish::getDescription))
                .collect(Collectors.toList());
        perform(MockMvcRequestBuilders.put(ADMIN_URL + ALFA_ID + "/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(userHttpBasic(USER))
                        .content(JsonUtil.writeValue(newDishes)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}