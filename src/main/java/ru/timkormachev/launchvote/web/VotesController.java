package ru.timkormachev.launchvote.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.timkormachev.launchvote.model.AuthorizedUser;
import ru.timkormachev.launchvote.model.User;
import ru.timkormachev.launchvote.model.Vote;
import ru.timkormachev.launchvote.repositories.RestaurantRepository;
import ru.timkormachev.launchvote.repositories.UserRepository;
import ru.timkormachev.launchvote.repositories.VoteRepository;
import ru.timkormachev.launchvote.to.ResultTo;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.timkormachev.launchvote.util.VotesUtil.isRevoteAllowed;
import static ru.timkormachev.launchvote.web.VotesController.REST_URL;

@RestController
@RequestMapping(REST_URL)
public class VotesController {
    static final String REST_URL = "/votes";
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final VoteRepository voteRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final LocalTime freezeVoteTime;

    @Autowired
    private final Clock clock;


    public VotesController(VoteRepository repository,
                           RestaurantRepository restaurantRepository,
                           UserRepository userRepository,
                           @Value("${app.voteFreezeTime}") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime freezeVoteTime,
                           Clock clock) {
        this.voteRepository = repository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.freezeVoteTime = freezeVoteTime;
        this.clock = clock;
    }

    @PostMapping
    @ResponseStatus()
    @Transactional
    @CacheEvict(value = "voteResults", allEntries = true)
    public ResponseEntity<Vote> vote(@RequestParam("restaurantId") int restaurantId, @AuthenticationPrincipal AuthorizedUser authorizedUser) {
        log.info("user with id {} votes for restaurant with id {}", authorizedUser.getId(), restaurantId);
        User user = userRepository.getOne(authorizedUser.getId());
        LocalDate date = LocalDate.now();
        Vote oldVote = voteRepository.getByUserAndDate(user, date);

        boolean isAlreadyVoted = oldVote != null;
        LocalTime now = LocalTime.now(clock);
        if (isAlreadyVoted && !isRevoteAllowed(freezeVoteTime, now)) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        Vote vote = isAlreadyVoted ? oldVote : new Vote();

        vote.setUser(user);
        vote.setRestaurant(restaurantRepository.getOne(restaurantId));
        vote.setDate(date);
        vote.setTime(now);
        voteRepository.save(vote);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping
    @Cacheable("voteResults")
    public List<ResultTo> getResults() {
        log.info("get voting results");
        return voteRepository.findResultsByDate(LocalDate.now());
    }
}
