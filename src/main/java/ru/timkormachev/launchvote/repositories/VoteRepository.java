package ru.timkormachev.launchvote.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.timkormachev.launchvote.model.User;
import ru.timkormachev.launchvote.model.Vote;
import ru.timkormachev.launchvote.to.ResultTo;

import java.time.LocalDate;
import java.util.List;

@Transactional(readOnly = true)
public interface VoteRepository extends JpaRepository<Vote, Integer> {

    Vote getByUserAndDate(User user, LocalDate date);

    @Query(value = "SELECT r.name as restaurant , (COUNT(DISTINCT v.user_id) * 100 / (SELECT COUNT(*) FROM vote v WHERE v.vote_date = ?1)) as percentage FROM Restaurant r LEFT OUTER JOIN Vote v ON r.id = v.restaurant_id AND v.vote_date = ?1 GROUP BY r.name ORDER BY percentage DESC, restaurant", nativeQuery = true)
    List<ResultTo> findResultsByDate(LocalDate date);
}
