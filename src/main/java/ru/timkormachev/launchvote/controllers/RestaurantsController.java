package ru.timkormachev.launchvote.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.timkormachev.launchvote.model.Dish;
import ru.timkormachev.launchvote.model.Restaurant;
import ru.timkormachev.launchvote.repositories.DishRepository;
import ru.timkormachev.launchvote.repositories.RestaurantRepository;
import ru.timkormachev.launchvote.util.json.View;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static ru.timkormachev.launchvote.controllers.RestaurantsController.REST_URL;
import static ru.timkormachev.launchvote.util.ValidationUtil.*;

@RestController
@RequestMapping(REST_URL)
public class RestaurantsController {
    static final String REST_URL = "/restaurants";

    private final RestaurantRepository restaurantRepository;

    private final DishRepository dishRepository;

    public RestaurantsController(RestaurantRepository restaurantRepository, DishRepository dishRepository) {
        this.restaurantRepository = restaurantRepository;
        this.dishRepository = dishRepository;
    }

    @GetMapping
    @JsonView(value = {View.Restaurants.class})
    public List<Restaurant> getAll() {
        return restaurantRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @GetMapping("/with")
    @JsonView(value = {View.Restaurants.WithDishes.class})
    public List<Restaurant> getAllWithDishes(@RequestParam boolean dishes) {
        if (dishes) {
            return restaurantRepository.findRestaurantsWithDishesByOrderByName();
        }
        return getAll();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> add(@Valid @RequestBody Restaurant restaurant) {
        checkNew(restaurant);
        Restaurant created = restaurantRepository.save(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @GetMapping("/{id}")
    public Restaurant get(@PathVariable int id) {
        return restaurantRepository.findById(id).orElseThrow();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody Restaurant restaurant, @PathVariable int id) {
        assureIdConsistent(restaurant, id);
        checkModificationAllowed(id);
        restaurant.getDishes().forEach(dish -> dish.setRestaurant(restaurant));
        restaurantRepository.save(restaurant);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        checkModificationAllowed(id);
        restaurantRepository.deleteById(id);
    }

    @GetMapping("/{id}/dishes")
    public List<Dish> getDishes(@PathVariable int id) {
        return dishRepository.findAllByRestaurantOrderByDescription(restaurantRepository.getOne(id));
    }

    //  https://stackoverflow.com/a/5587892
    @PutMapping(value = "/{id}/dishes", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void createDishes(@Valid @RequestBody List<Dish> dishes, @PathVariable int id) throws ChangeSetPersister.NotFoundException {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        List<Dish> currentDishes = restaurant.getDishes();
        currentDishes.clear();
        restaurant.addToDishes(dishes);
        restaurantRepository.save(restaurant);
    }
}
