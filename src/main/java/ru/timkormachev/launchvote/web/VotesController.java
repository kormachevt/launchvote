package ru.timkormachev.launchvote.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.timkormachev.launchvote.model.AuthorizedUser;
import ru.timkormachev.launchvote.model.Restaurant;
import ru.timkormachev.launchvote.model.User;
import ru.timkormachev.launchvote.model.Vote;
import ru.timkormachev.launchvote.repositories.RestaurantRepository;
import ru.timkormachev.launchvote.repositories.UserRepository;
import ru.timkormachev.launchvote.repositories.VoteRepository;
import ru.timkormachev.launchvote.to.ResultTo;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.timkormachev.launchvote.util.VotesUtil.calcPercentage;
import static ru.timkormachev.launchvote.util.VotesUtil.checkVoteTime;
import static ru.timkormachev.launchvote.web.VotesController.REST_URL;

@RestController
@RequestMapping(REST_URL)
public class VotesController {
    static final String REST_URL = "/votes";

    private final VoteRepository voteRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final LocalTime stopVoteTime;
    private static final Comparator<ResultTo> BY_PERCENT_THEN_NAME = Comparator.comparing(ResultTo::getPercentage).reversed()
            .thenComparing(ResultTo::getRestaurant);

    @Autowired
    private final Clock clock;


    public VotesController(VoteRepository repository,
                           RestaurantRepository restaurantRepository,
                           UserRepository userRepository,
                           @Value("${app.stopVoteTime}") String stopVoteTimeProperty, Clock clock) {
        this.voteRepository = repository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.stopVoteTime = LocalTime.parse(stopVoteTimeProperty);
        this.clock = clock;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void registerVote(@RequestParam("restaurantId") int restaurantId, @AuthenticationPrincipal AuthorizedUser authorizedUser) {
        LocalTime now = LocalTime.now(clock);
        checkVoteTime(stopVoteTime, now);
        Restaurant restaurant = restaurantRepository.getOne(restaurantId);
        User user = userRepository.getOne(authorizedUser.getId());
        LocalDate date = LocalDate.now();
        Vote oldVote = voteRepository.getByUserAndDate(user, date);
        Vote vote = oldVote == null ? new Vote() : oldVote;
        vote.setUser(user);
        vote.setRestaurant(restaurant);
        vote.setDate(date);
        vote.setTime(now);
        voteRepository.save(vote);
    }

    @GetMapping
    public List<ResultTo> getResults() {
        List<Vote> votes = voteRepository.findVotesByDate(LocalDate.now());
        Map<Restaurant, Long> votesMap = votes.stream()
                .collect(Collectors.groupingBy(Vote::getRestaurant, Collectors.counting()));

        int totalVotes = votes.size();
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return restaurants.stream()
                .map(r -> new ResultTo(r.getName(), calcPercentage(Optional.ofNullable(votesMap.get(r)).orElse(0L),
                                                                   totalVotes)))
                .sorted(BY_PERCENT_THEN_NAME)
                .collect(Collectors.toList());
    }
}
