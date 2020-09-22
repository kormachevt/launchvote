package ru.timkormachev.launchvote.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.timkormachev.launchvote.util.json.View;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "restaurants")
@NamedEntityGraph(name = "Restaurant.dishes",
        attributeNodes = @NamedAttributeNode("dishes")
)
@Getter
@Setter
@ToString
@Accessors(chain = true)

public class Restaurant extends AbstractBaseEntity {

    @JsonView(value = {View.Restaurants.WithDishes.class})
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "restaurant", orphanRemoval = true)
    @OrderBy("description ASC")
    private List<Dish> dishes;

    public Restaurant(Integer id) {
        super(id);
    }

    public Restaurant() {
    }

    @Column(name = "name", nullable = false)
    @NotBlank
    @Size(min = 1, max = 255)
    @JsonView(value = {View.Restaurants.class})
    private String name;

    public Restaurant(Restaurant restaurant) {
        this.name = restaurant.getName();
        this.dishes = restaurant.getDishes();
    }

    //  https://stackoverflow.com/a/3937867
    public void addToDishes(List<Dish> dishes) {
        dishes.forEach(dish -> {
            dish.setRestaurant(this);
            this.dishes.add(dish);
        });
    }
}
