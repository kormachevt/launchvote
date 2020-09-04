package ru.timkormachev.launchvote.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.timkormachev.launchvote.util.json.View;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "restaurants")
@Data
@EqualsAndHashCode(callSuper = true)
@NamedEntityGraph(name = "Restaurant.dishes",
                  attributeNodes = @NamedAttributeNode("dishes")
)
public class Restaurant extends AbstractBaseEntity {

    @Column(name = "name", nullable = false)
    @NotBlank
    @Size(min = 1, max = 255)
    @JsonView(value = {View.Restaurants.class})
    private String name;

    @JsonView(value = {View.Restaurants.WithDishes.class})
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "restaurant", orphanRemoval = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Dish> dishes;

    //  https://stackoverflow.com/a/3937867
    public void addToDishes(List<Dish> dishes) {
        dishes.forEach(dish -> {
            dish.setRestaurant(this);
            this.dishes.add(dish);
        });
    }
}
