package ru.timkormachev.launchvote.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;
import ru.timkormachev.launchvote.util.json.View;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "dishes")
@Data
@EqualsAndHashCode(callSuper = true)
public class Dish extends AbstractBaseEntity {

    @Column(name = "description", nullable = false)
    @NotBlank
    @Size(min = 1, max = 255)
    @JsonView(value = {View.Restaurants.WithDishes.class})
    private String description;

    @Column(name = "price", nullable = false)
    @Range(min = 0L, max = 1_000_000L)
    @JsonView(value = {View.Restaurants.WithDishes.class})
    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    @JsonIgnore
    private Restaurant restaurant;
}
