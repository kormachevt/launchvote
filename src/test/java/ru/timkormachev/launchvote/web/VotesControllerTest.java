package ru.timkormachev.launchvote.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.timkormachev.launchvote.AbstractControllerTest;
import ru.timkormachev.launchvote.model.User;
import ru.timkormachev.launchvote.model.Vote;
import ru.timkormachev.launchvote.repositories.VoteRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.timkormachev.launchvote.RestaurantTestData.*;
import static ru.timkormachev.launchvote.TestUtil.*;
import static ru.timkormachev.launchvote.UserTestData.*;
import static ru.timkormachev.launchvote.VotesTestData.*;
import static ru.timkormachev.launchvote.web.VotesController.REST_URL;

class VotesControllerTest extends AbstractControllerTest {

    @Autowired
    VoteRepository repository;

    @Autowired
    Environment environment;

    @MockBean
    private Clock clock;

    private Clock revoteIsEnabledClock;

    private Clock revoteIsNotEnabledClock;

    @BeforeEach
    public void initMocks() {
        String freezeVoteTime = environment.getProperty("app.voteFreezeTime");
        assert freezeVoteTime != null;
        LocalDate date = LocalDate.now();
        LocalTime revoteIsEnabledTime = LocalTime.parse(freezeVoteTime).minusSeconds(1);
        LocalTime revoteIsNotEnabledTime = LocalTime.parse(freezeVoteTime).plusSeconds(1);
        revoteIsEnabledClock = toClock(revoteIsEnabledTime, date);
        revoteIsNotEnabledClock = toClock(revoteIsNotEnabledTime, date);
    }


    @Test
    @Transactional
    @DirtiesContext
    void registerNewVoteBeforeVoteFreezeTime() throws Exception {
        doReturn(revoteIsEnabledClock.instant()).when(clock).instant();
        doReturn(revoteIsEnabledClock.getZone()).when(clock).getZone();

        User registered = registerNewUser(getNew());

        perform(MockMvcRequestBuilders.post(REST_URL + "?restaurantId=" + BETA_ID)
                        .with(userHttpBasic(getNew())))
                .andDo(print())
                .andExpect(status().isCreated());
        Vote newVote = repository.getByUserAndDate(registered, LocalDate.now());
        assertThat(newVote.getRestaurant()).isEqualTo(BETA);
    }

    @Test
    @Transactional
    @DirtiesContext
    void registerNewVoteAfterVoteFreezeTime() throws Exception {
        doReturn(revoteIsNotEnabledClock.instant()).when(clock).instant();
        doReturn(revoteIsNotEnabledClock.getZone()).when(clock).getZone();

        User registered = registerNewUser(getNew());

        perform(MockMvcRequestBuilders.post(REST_URL + "?restaurantId=" + BETA_ID)
                        .with(userHttpBasic(getNew())))
                .andDo(print())
                .andExpect(status().isCreated());
        Vote newVote = repository.getByUserAndDate(registered, LocalDate.now());
        assertThat(newVote.getRestaurant()).isEqualTo(BETA);
    }

    @Test
    @Transactional
    @DirtiesContext
    void revoteBeforeVoteFreezeTime() throws Exception {
        doReturn(revoteIsEnabledClock.instant()).when(clock).instant();
        doReturn(revoteIsEnabledClock.getZone()).when(clock).getZone();

        perform(MockMvcRequestBuilders.post(REST_URL + "?restaurantId=" + OMEGA_ID)
                        .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isCreated());
        Vote newVote = repository.getByUserAndDate(USER, LocalDate.now());
        assertThat(newVote.getRestaurant()).isEqualTo(OMEGA);
    }

    @Test
    @Transactional
    @DirtiesContext
    void revoteAfterVoteFreezeTime() throws Exception {
        doReturn(revoteIsNotEnabledClock.instant()).when(clock).instant();
        doReturn(revoteIsNotEnabledClock.getZone()).when(clock).getZone();

        perform(MockMvcRequestBuilders.post(REST_URL + "?restaurantId=" + OMEGA_ID)
                        .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isOk());
        Vote newVote = repository.getByUserAndDate(USER, LocalDate.now());
        assertThat(newVote.getRestaurant()).isEqualTo(ALFA);
    }

    @Test
    void getResults() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                        .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESULTS_MATCHER.contentJson(RESULT_1, RESULT_2, RESULT_3));
    }

    private User registerNewUser(User user) throws Exception {
        ResultActions action = perform(MockMvcRequestBuilders.post(UsersController.REST_URL)
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .with(userHttpBasic(ADMIN))
                                               .content(jsonWithPassword(user, "newPass")))
                .andDo(print())
                .andExpect(status().isCreated());

        return readFromJson(action, User.class);
    }
}