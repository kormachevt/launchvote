package ru.timkormachev.launchvote.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.timkormachev.launchvote.AbstractControllerTest;
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
import static ru.timkormachev.launchvote.RestaurantTestData.OMEGA;
import static ru.timkormachev.launchvote.RestaurantTestData.OMEGA_ID;
import static ru.timkormachev.launchvote.TestUtil.toClock;
import static ru.timkormachev.launchvote.TestUtil.userHttpBasic;
import static ru.timkormachev.launchvote.UserTestData.USER;
import static ru.timkormachev.launchvote.VotesTestData.*;
import static ru.timkormachev.launchvote.exception.ErrorType.VALIDATION_ERROR;
import static ru.timkormachev.launchvote.web.VotesController.REST_URL;

class VotesControllerTest extends AbstractControllerTest {

    @Autowired
    VoteRepository repository;

    @Autowired
    Environment environment;

    @MockBean
    private Clock clock;

    private Clock fixedValidClock;

    private Clock fixedInvalidClock;

    @BeforeEach
    public void initMocks() {
        String stopVoteTimeProperty = environment.getProperty("app.stopVoteTime");
        assert stopVoteTimeProperty != null;
        LocalDate date = LocalDate.now();
        LocalTime validTime = LocalTime.parse(stopVoteTimeProperty).minusSeconds(1);
        LocalTime invalidTime = LocalTime.parse(stopVoteTimeProperty).plusSeconds(1);
        fixedValidClock = toClock(validTime, date);
        fixedInvalidClock = toClock(invalidTime, date);
    }


    @Test
    @Transactional
    @DirtiesContext
    void registerVote() throws Exception {
        doReturn(fixedValidClock.instant()).when(clock).instant();
        doReturn(fixedValidClock.getZone()).when(clock).getZone();

        perform(MockMvcRequestBuilders.post(REST_URL + "?restaurantId=" + OMEGA_ID)
                        .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isCreated());
        Vote newVote = repository.getByUserAndDate(USER, LocalDate.now());
        assertThat(newVote.getRestaurant()).isEqualTo(OMEGA);
    }

    @Test
    void registerVoteInvalidTime() throws Exception {
        doReturn(fixedInvalidClock.instant()).when(clock).instant();
        doReturn(fixedInvalidClock.getZone()).when(clock).getZone();

        perform(MockMvcRequestBuilders.post(REST_URL + "?restaurantId=" + OMEGA_ID)
                        .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void getResults() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                        .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESULTS_MATCHER.contentJson(RESULT_1, RESULT_2));
    }
}