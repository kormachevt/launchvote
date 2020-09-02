package ru.timkormachev.launchvote.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.timkormachev.launchvote.model.User;
import ru.timkormachev.launchvote.repositories.UserRepository;

import javax.validation.Valid;

import static ru.timkormachev.launchvote.util.SecurityUtil.authUserId;
import static ru.timkormachev.launchvote.util.ValidationUtil.assureIdConsistent;

@RestController
@RequestMapping(UserController.REST_URL)
public class UserController {
    static final String REST_URL = "/user";

    private final UserRepository repository;

    public UserController(UserRepository userRepository) {
        this.repository = userRepository;
    }

    @GetMapping
    public User get() {
        return repository.findById(authUserId()).orElseThrow();
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public User update(@RequestBody @Valid User newUser) {
        assureIdConsistent(newUser, authUserId());
        return repository.findById(authUserId())
                .map(user -> {
                    user.setLogin(newUser.getLogin());
                    user.setPassword(newUser.getPassword());
                    user.setEmail((newUser.getEmail()));
                    return repository.save(user);
                }).orElseThrow();
    }
}
