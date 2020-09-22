package ru.timkormachev.launchvote.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Table(name = "vote")
@Entity
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Vote extends AbstractBaseEntity {

    @Column(name = "vote_date")
    LocalDate date;

    @Column(name = "vote_time")
    LocalTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}
