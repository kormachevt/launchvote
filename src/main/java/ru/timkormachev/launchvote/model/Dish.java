package ru.timkormachev.launchvote.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
    private String description;

    @Column(name = "price", nullable = false)
    @Range(min = 0L, max = 1_000_000L)
    private Long price;

    @ManyToOne
    private Restaurant restaurant;
}
