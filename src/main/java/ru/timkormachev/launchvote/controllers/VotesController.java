package ru.timkormachev.launchvote.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.timkormachev.launchvote.dto.Result;
import ru.timkormachev.launchvote.model.Restaurant;
import ru.timkormachev.launchvote.model.User;
import ru.timkormachev.launchvote.model.Vote;
import ru.timkormachev.launchvote.repositories.RestaurantRepository;
import ru.timkormachev.launchvote.repositories.UserRepository;
import ru.timkormachev.launchvote.repositories.VoteRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.timkormachev.launchvote.controllers.VotesController.REST_URL;
import static ru.timkormachev.launchvote.util.SecurityUtil.authUserId;
import static ru.timkormachev.launchvote.util.VotesUtil.calcPercentage;
import static ru.timkormachev.launchvote.util.VotesUtil.checkVoteTime;

@RestController
@RequestMapping(REST_URL)
public class VotesController {
    static final String REST_URL = "/votes";

    private final VoteRepository voteRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final LocalTime stopVoteTime;
    private static final Comparator<Result> BY_PERCENT_THEN_NAME = Comparator.comparing(Result::getPercentage).reversed()
            .thenComparing(Result::getRestaurant);


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
        checkVoteTime(stopVoteTime);
        Restaurant restaurant = restaurantRepository.getOne(restaurantId);
        User user = userRepository.getOne(authUserId());
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

//      https://stackoverflow.com/questions/33873260/using-jpa-hibernate-and-entity-update-column-on-unique-contraint-violation
        Vote oldVote = voteRepository.getByUserAndDate(user, date);
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
    public List<Result> getResults() {
        List<Vote> votes = voteRepository.findVotesByDate(LocalDate.now());
        Map<Restaurant, Long> votesMap = votes.stream()
                .collect(Collectors.groupingBy(Vote::getRestaurant, Collectors.counting()));

        int totalVotes = votes.size();
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return restaurants.stream()
                .map(r -> new Result(r.getName(), calcPercentage(Optional.ofNullable(votesMap.get(r)).orElse(0L),
                                                                 totalVotes)))
                .sorted(BY_PERCENT_THEN_NAME)
                .collect(Collectors.toList());
    }
}
