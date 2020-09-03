package ru.timkormachev.launchvote.repositories;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.timkormachev.launchvote.model.Restaurant;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

    @EntityGraph(value = "Restaurant.dishes")
    List<Restaurant> findRestaurantsWithDishesByOrderByName();
}
