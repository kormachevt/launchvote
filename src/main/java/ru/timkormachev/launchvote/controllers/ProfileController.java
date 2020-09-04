package ru.timkormachev.launchvote.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.timkormachev.launchvote.model.AuthorizedUser;
import ru.timkormachev.launchvote.model.Role;
import ru.timkormachev.launchvote.model.User;
import ru.timkormachev.launchvote.repositories.UserRepository;
import ru.timkormachev.launchvote.util.UniqueMailValidator;

import javax.validation.Valid;
import java.net.URI;
import java.util.Set;

import static ru.timkormachev.launchvote.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(ProfileController.REST_URL)
public class ProfileController extends AbstractUserController {
    static final String REST_URL = "/profile";

    private final UserRepository repository;

    private final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public ProfileController(UserRepository userRepository, UniqueMailValidator uniqueMailValidator) {
        super(uniqueMailValidator);
        this.repository = userRepository;
    }

    @GetMapping
    public User get(@AuthenticationPrincipal AuthorizedUser authorizedUser) {
        return repository.findById(authorizedUser.getId()).orElseThrow();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> register(@Valid @RequestBody User registeringUser) {
        checkNew(registeringUser);
        User user = new User();
        user.setLogin(registeringUser.getLogin());
        user.setPassword(encoder.encode(registeringUser.getPassword()));
        user.setEmail(registeringUser.getEmail());
        Set<Role> roles = Set.of(Role.USER);
        user.setRoles(roles);
        User created = repository.save(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public User update(@RequestBody @Valid User newUser, @AuthenticationPrincipal AuthorizedUser authorizedUser) throws BindException {
        int authUserId = authorizedUser.getId();
        checkAndValidateForUpdate(newUser, authUserId);
        return repository.findById(authUserId)
                .map(user -> {
                    user.setLogin(newUser.getLogin());
                    user.setPassword(encoder.encode(newUser.getPassword()));
                    user.setEmail((newUser.getEmail()));
                    return repository.save(user);
                }).orElseThrow();
    }
}
