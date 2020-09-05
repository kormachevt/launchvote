package ru.timkormachev.launchvote.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import ru.timkormachev.launchvote.model.Role;
import ru.timkormachev.launchvote.model.User;
import ru.timkormachev.launchvote.to.UserTo;

import java.util.Set;

public class UserUtils {
    public static User createNewFromTo(UserTo userTo, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setLogin(userTo.getLogin());
        user.setPassword(userTo.getPassword());
        encodePassword(user, passwordEncoder);
        user.setEmail(userTo.getEmail().toLowerCase());
        user.setRoles(Set.of(Role.USER));
        return user;
    }

    public static User updateFromTo(User user, UserTo userTo, PasswordEncoder passwordEncoder) {
        user.setLogin(userTo.getLogin());
        user.setEmail(userTo.getEmail().toLowerCase());
        user.setPassword(userTo.getPassword());
        encodePassword(user, passwordEncoder);
        return user;
    }

    private static void encodePassword(User user, PasswordEncoder passwordEncoder) {
        String password = user.getPassword();
        user.setPassword(StringUtils.hasText(password) ? passwordEncoder.encode(password) : password);
    }

    public static UserTo asTo(User user) {
        UserTo userTo = new UserTo();
        userTo.setLogin(user.getLogin());
        userTo.setPassword(user.getPassword());
        userTo.setEmail(user.getEmail());
        userTo.setId(user.getId());
        return userTo;
    }
}
