package ru.timkormachev.launchvote.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.timkormachev.launchvote.model.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
}
