package ru.timkormachev.launchvote.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.timkormachev.launchvote.model.Restaurant;
import ru.timkormachev.launchvote.model.User;
import ru.timkormachev.launchvote.model.Vote;
import ru.timkormachev.launchvote.repositories.RestaurantRepository;
import ru.timkormachev.launchvote.repositories.UserRepository;
import ru.timkormachev.launchvote.repositories.VoteRepository;
import ru.timkormachev.launchvote.util.exception.InvalidVoteTimeException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.timkormachev.launchvote.controllers.VotesController.REST_URL;
import static ru.timkormachev.launchvote.util.SecurityUtil.authUserId;

@RestController
@RequestMapping(REST_URL)
public class VotesController {
    static final String REST_URL = "/votes";

    private final VoteRepository voteRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final LocalTime stopVoteTime;

    public VotesController(VoteRepository repository,
                           RestaurantRepository restaurantRepository,
                           UserRepository userRepository,
                           @Value("${app.stopVoteTime}") String stopVoteTimeProperty) {
        this.voteRepository = repository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.stopVoteTime = LocalTime.parse(stopVoteTimeProperty);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void registerVote(@RequestParam("restaurantId") int restaurantId) {
        checkVoteTime();
        Restaurant restaurant = restaurantRepository.getOne(restaurantId);
        User user = userRepository.getOne(authUserId());
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

//      https://stackoverflow.com/questions/33873260/using-jpa-hibernate-and-entity-update-column-on-unique-contraint-violation
        Vote oldVote = voteRepository.findByUserAndDate(user, date);
        if (oldVote == null) {
            Vote vote = new Vote();
            vote.setUser(user);
            vote.setRestaurant(restaurant);
            vote.setDate(date);
            vote.setTime(time);
            voteRepository.save(vote);
        } else {
            oldVote.setRestaurant(restaurant);
            oldVote.setDate(date);
            oldVote.setTime(time);
            voteRepository.save(oldVote);
        }
    }

    @GetMapping
    public List<Vote> getResults() {
        return voteRepository.findAll(Sort.by(Sort.Direction.ASC, "restaurant_id"));
    }

    private void checkVoteTime() {
        if (LocalTime.now().isAfter(stopVoteTime)) {
            throw new InvalidVoteTimeException();
        }
    }
}
