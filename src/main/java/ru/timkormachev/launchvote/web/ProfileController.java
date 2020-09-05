package ru.timkormachev.launchvote.web;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.timkormachev.launchvote.exception.NotFoundException;
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
        return repository.findById(authorizedUser.getId()).orElseThrow(() -> new NotFoundException("id=" + authorizedUser.getId()));
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> register(@RequestBody @Valid UserTo userTo) {
        User user = createNewFromTo(userTo, encoder);
        User created = repository.save(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public User update(@Valid @RequestBody UserTo userTo, @AuthenticationPrincipal AuthorizedUser authorizedUser) throws BindException, ChangeSetPersister.NotFoundException {
        int authUserId = authorizedUser.getId();
        checkAndValidateForUpdate(userTo, authUserId);
        User user = repository.getOne(authUserId);
        updateFromTo(user, userTo, encoder);
        return repository.save(user);
    }
}
