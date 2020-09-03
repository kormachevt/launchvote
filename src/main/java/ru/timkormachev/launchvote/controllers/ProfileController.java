package ru.timkormachev.launchvote.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import ru.timkormachev.launchvote.model.User;
import ru.timkormachev.launchvote.repositories.UserRepository;
import ru.timkormachev.launchvote.util.UniqueMailValidator;

import javax.validation.Valid;

import static ru.timkormachev.launchvote.util.SecurityUtil.authUserId;

@RestController
@RequestMapping(ProfileController.REST_URL)
public class ProfileController extends AbstractUserController {
    static final String REST_URL = "/profile";

    private final UserRepository repository;

    public ProfileController(UserRepository userRepository, UniqueMailValidator uniqueMailValidator) {
        super(uniqueMailValidator);
        this.repository = userRepository;
    }

    @GetMapping
    public User get() {
        return repository.findById(authUserId()).orElseThrow();
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public User update(@RequestBody @Valid User newUser) throws BindException {
        checkAndValidateForUpdate(newUser, authUserId());
        return repository.findById(authUserId())
                .map(user -> {
                    user.setLogin(newUser.getLogin());
                    user.setPassword(newUser.getPassword());
                    user.setEmail((newUser.getEmail()));
                    return repository.save(user);
                }).orElseThrow();
    }
}
