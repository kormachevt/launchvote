package ru.timkormachev.launchvote.repositories;


import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.timkormachev.launchvote.exception.NotFoundException;
import ru.timkormachev.launchvote.model.Restaurant;

import java.util.List;

@Transactional(readOnly = true)
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

    @EntityGraph(value = "Restaurant.dishes")
    @Cacheable("restaurantsWithDishes")
    List<Restaurant> findRestaurantsWithDishesByOrderByName();

    default Restaurant findOrThrowById(Integer id) {
        return findById(id).orElseThrow(() -> new NotFoundException("id=" + id));
    }
}
