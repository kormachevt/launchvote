package ru.timkormachev.launchvote.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.timkormachev.launchvote.AbstractControllerTest;
import ru.timkormachev.launchvote.exception.NotFoundException;
import ru.timkormachev.launchvote.model.User;
import ru.timkormachev.launchvote.repositories.UserRepository;
import ru.timkormachev.launchvote.to.UserTo;
import ru.timkormachev.launchvote.web.json.JsonUtil;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.timkormachev.launchvote.TestData.*;
import static ru.timkormachev.launchvote.TestUtil.readFromJson;
import static ru.timkormachev.launchvote.TestUtil.userHttpBasic;
import static ru.timkormachev.launchvote.exception.ErrorType.VALIDATION_ERROR;
import static ru.timkormachev.launchvote.exception.ExceptionInfoHandler.EXCEPTION_DUPLICATE_EMAIL;
import static ru.timkormachev.launchvote.util.UserUtils.createNewFromTo;
import static ru.timkormachev.launchvote.util.UserUtils.updateFromTo;
import static ru.timkormachev.launchvote.web.ProfileController.REST_URL;

class ProfileControllerTest extends AbstractControllerTest {

    private final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    @Autowired
    private UserRepository repository;

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                        .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(USER));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register() throws Exception {
        UserTo newTo = new UserTo()
                .setLogin("newLogin")
                .setPassword("newPassword")
                .setEmail("newemail@gmail.com");
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL + "/register")
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(JsonUtil.writeValue(newTo)))
                .andDo(print())
                .andExpect(status().isCreated());

        User created = readFromJson(action, User.class);
        int newId = created.getId();
        User newUser = createNewFromTo(newTo, encoder);
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
    }

    @Test
    void update() throws Exception {
        UserTo updatedTo = new UserTo()
                .setLogin("newLogin")
                .setPassword("newPassword")
                .setEmail("newemail@gmail.com");
        perform(MockMvcRequestBuilders.put(REST_URL).contentType(MediaType.APPLICATION_JSON)
                        .with(userHttpBasic(USER))
                        .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isNoContent());


        User user = repository.findById(USER_ID).orElseThrow(() -> new NotFoundException("id=" + USER_ID));
        USER_MATCHER.assertMatch(user, updateFromTo(new User(USER), updatedTo, encoder));
    }

    @Test
    void updateInvalid() throws Exception {
        UserTo updatedTo = new UserTo()
                .setPassword("newPassword");
        perform(MockMvcRequestBuilders.put(REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(userHttpBasic(USER))
                        .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void updateDuplicate() throws Exception {
        UserTo updatedTo = new UserTo()
                .setLogin("newLogin")
                .setPassword("newPassword")
                .setEmail("admin@gmail.com");
        perform(MockMvcRequestBuilders.put(REST_URL).contentType(MediaType.APPLICATION_JSON)
                        .with(userHttpBasic(USER))
                        .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_EMAIL));
    }
}