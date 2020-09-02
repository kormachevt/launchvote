package ru.timkormachev.launchvote.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.timkormachev.launchvote.model.User;
import ru.timkormachev.launchvote.model.Vote;

import java.time.LocalDate;

public interface VoteRepository extends JpaRepository<Vote, Integer> {

    Vote findByUserAndDate(User user, LocalDate date);
}
