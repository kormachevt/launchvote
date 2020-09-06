package ru.timkormachev.launchvote;

import ru.timkormachev.launchvote.model.Role;
import ru.timkormachev.launchvote.model.User;

import java.util.Set;

import static ru.timkormachev.launchvote.model.AbstractBaseEntity.START_SEQ;
import static ru.timkormachev.launchvote.web.json.JsonUtil.writeAdditionProps;

public class TestData {
    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;
    public static final int NOT_FOUND_ID = 10;
    public static final User USER = new User(USER_ID)
            .setLogin("User")
            .setPassword("password")
            .setEmail("user@gmail.com")
            .setRoles(Set.of(Role.USER));
    public static final User ADMIN = new User(ADMIN_ID)
            .setLogin("Admin")
            .setPassword("admin")
            .setEmail("admin@gmail.com")
            .setRoles(Set.of(Role.USER, Role.ADMIN));
    public static TestMatcher<User> USER_MATCHER = TestMatcher.usingFieldsWithIgnoringAssertions(User.class, "password");

    public static User getNew() {
        return new User()
                .setLogin("New")
                .setPassword("newPass")
                .setEmail("new@gmail.com")
                .setEnabled(false)
                .setRoles(Set.of(Role.USER));
    }

    public static User getUpdated() {
        User updated = new User(USER);
        updated.setLogin("UpdatedLogin");
        updated.setRoles(Set.of(Role.ADMIN));
        return updated;
    }

    public static String jsonWithPassword(User user, String passw) {
        return writeAdditionProps(user, "password", passw);
    }
}
