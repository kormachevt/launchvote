package ru.timkormachev.launchvote.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Table(name = "votes")
@Entity
@EqualsAndHashCode(callSuper = true)
public class Vote extends AbstractBaseEntity {

    @Column(name = "date")
    LocalDate date;

    @Column(name = "time")
    LocalTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}
