package ru.timkormachev.launchvote.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.timkormachev.launchvote.AbstractControllerTest;
import ru.timkormachev.launchvote.exception.NotFoundException;
import ru.timkormachev.launchvote.model.Role;
import ru.timkormachev.launchvote.model.User;
import ru.timkormachev.launchvote.repositories.UserRepository;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.timkormachev.launchvote.TestData.*;
import static ru.timkormachev.launchvote.TestUtil.readFromJson;
import static ru.timkormachev.launchvote.TestUtil.userHttpBasic;
import static ru.timkormachev.launchvote.exception.ErrorType.VALIDATION_ERROR;
import static ru.timkormachev.launchvote.exception.ExceptionInfoHandler.EXCEPTION_DUPLICATE_EMAIL;
import static ru.timkormachev.launchvote.web.json.JsonUtil.writeValue;

class UsersControllerTest extends AbstractControllerTest {

    private static final String REST_URL = UsersController.REST_URL + '/';

    @Autowired
    UserRepository repository;

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + ADMIN_ID)
                        .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                // https://jira.spring.io/browse/SPR-14472
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(ADMIN));
    }

    @Test
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + 1)
                        .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getByEmail() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "by?email=" + ADMIN.getEmail())
                        .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(ADMIN));
    }

    @Test
    @DirtiesContext
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + USER_ID)
                        .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(
                NotFoundException.class, () -> repository.findOrThrowById(USER_ID));
    }

    @Test
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + 1)
                        .with(userHttpBasic(ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                        .with(userHttpBasic(USER)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DirtiesContext
    void update() throws Exception {
        User updated = getUpdated();
        updated.setId(null);
        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(userHttpBasic(ADMIN))
                        .content(jsonWithPassword(updated, "newPass")))
                .andExpect(status().isNoContent());

        USER_MATCHER.assertMatch(repository.findOrThrowById(USER_ID), getUpdated());
    }


    @Test
    @DirtiesContext
    void createWithLocation() throws Exception {
        User newUser = getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .with(userHttpBasic(ADMIN))
                                               .content(jsonWithPassword(newUser, "newPass")))
                .andExpect(status().isCreated());

        User created = readFromJson(action, User.class);
        int newId = created.id();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(repository.findOrThrowById(newId), newUser);
    }

    @Test
    @DirtiesContext
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                        .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(ADMIN, USER));
    }

    @Test
    @DirtiesContext
    void enable() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + USER_ID)
                        .param("enabled", "false")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertFalse(repository.findOrThrowById(USER_ID).isEnabled());
    }

    @Test
    void createInvalid() throws Exception {
        User expected = new User().setPassword("newPass").setRoles(Set.of(Role.USER, Role.ADMIN));
        perform(MockMvcRequestBuilders.post(REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(userHttpBasic(ADMIN))
                        .content(writeValue(expected)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andDo(print());
    }

    @Test
    void updateInvalid() throws Exception {
        User updated = new User(USER);
        updated.setLogin("");
        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(userHttpBasic(ADMIN))
                        .content(writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @DirtiesContext
    void updateDuplicate() throws Exception {
        User updated = new User(USER);
        updated.setEmail("admin@gmail.com");
        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(userHttpBasic(ADMIN))
                        .content(jsonWithPassword(updated, "password")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_EMAIL))
                .andDo(print());
    }

    @Test
    @DirtiesContext
    void createDuplicate() throws Exception {
        User expected = getNew().setEmail("admin@gmail.com");
        perform(MockMvcRequestBuilders.post(REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(userHttpBasic(ADMIN))
                        .content(jsonWithPassword(expected, "newPass")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_EMAIL));
    }
}