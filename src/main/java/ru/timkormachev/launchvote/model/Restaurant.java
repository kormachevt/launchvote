package ru.timkormachev.launchvote.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "restaurants")
@Data
public class Restaurant extends AbstractBaseEntity {

    @Column(name = "name", nullable = false)
    @NotBlank
    @Size(min = 1, max = 255)
    private String name;

    @OneToMany(fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               mappedBy = "restaurant")
    @JsonManagedReference
    private List<Dish> dishes;
}
