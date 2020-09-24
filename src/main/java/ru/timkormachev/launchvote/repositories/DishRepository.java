package ru.timkormachev.launchvote.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.timkormachev.launchvote.model.Dish;
import ru.timkormachev.launchvote.model.Restaurant;

import java.util.List;

@Transactional(readOnly = true)
public interface DishRepository extends JpaRepository<Dish, Integer> {

    List<Dish> findAllByRestaurantOrderByDescription(Restaurant restaurant);
}
