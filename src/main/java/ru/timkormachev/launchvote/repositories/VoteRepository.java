package ru.timkormachev.launchvote.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.timkormachev.launchvote.model.User;
import ru.timkormachev.launchvote.model.Vote;

import java.time.LocalDate;
import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Integer> {

    Vote getByUserAndDate(User user, LocalDate date);

    List<Vote> findVotesByDate(LocalDate date);
}
