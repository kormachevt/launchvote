package ru.timkormachev.launchvote.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.timkormachev.launchvote.model.AuthorizedUser;
import ru.timkormachev.launchvote.model.User;
import ru.timkormachev.launchvote.repositories.UserRepository;
import ru.timkormachev.launchvote.to.UserTo;
import ru.timkormachev.launchvote.util.UniqueMailValidator;

import javax.validation.Valid;
import java.net.URI;

import static ru.timkormachev.launchvote.util.UserUtils.createNewFromTo;
import static ru.timkormachev.launchvote.util.UserUtils.updateFromTo;

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
        log.info("get user with id={}", authorizedUser.getId());
        return repository.findOrThrowById(authorizedUser.getId());
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> register(@RequestBody @Valid UserTo userTo) {
        log.info("create new user with userTo {}", userTo);
        User user = createNewFromTo(userTo, encoder);
        User created = repository.save(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public User update(@Valid @RequestBody UserTo userTo, @AuthenticationPrincipal AuthorizedUser authorizedUser) throws BindException {
        log.info("update user {} with userTo {}", authorizedUser.getId(), userTo);
        int authUserId = authorizedUser.getId();
        checkAndValidateForUpdate(userTo, authUserId);
        User user = repository.getOne(authUserId);
        updateFromTo(user, userTo, encoder);
        return repository.save(user);
    }
}
