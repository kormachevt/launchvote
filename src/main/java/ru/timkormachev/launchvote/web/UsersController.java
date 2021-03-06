package ru.timkormachev.launchvote.web;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.timkormachev.launchvote.model.User;
import ru.timkormachev.launchvote.repositories.UserRepository;
import ru.timkormachev.launchvote.util.UniqueMailValidator;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static ru.timkormachev.launchvote.util.UserUtils.prepareToSave;
import static ru.timkormachev.launchvote.util.ValidationUtil.assureIdConsistent;
import static ru.timkormachev.launchvote.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(UsersController.REST_URL)
public class UsersController extends AbstractUserController {
    static final String REST_URL = "/admin/users";

    private final UserRepository repository;

    private final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UsersController(UserRepository userRepository, UniqueMailValidator emailValidator) {
        super(emailValidator);
        this.repository = userRepository;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("get all users");
        return repository.findAll(Sort.by(Sort.Direction.ASC, "login", "email"));
    }

    @GetMapping("/{id}")
    public User get(@PathVariable int id) {
        log.info("get user with id {}", id);
        return repository.findOrThrowById(id);
    }

    @GetMapping("/by")
    public User getByMail(@RequestParam String email) {
        log.info("get user by email {}", email);
        return repository.getByEmail(email);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createWithLocation(@Valid @RequestBody User user) {
        log.info("create new user {}", user);
        checkNew(user);
        prepareToSave(user, encoder);
        User created = repository.save(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@RequestBody User newUser, @PathVariable int id) throws BindException {
        log.info("update user with id {} to user {}", id, newUser);
        checkAndValidateForUpdate(newUser, id);
        assureIdConsistent(newUser, id);
        prepareToSave(newUser, encoder);
        repository.save(newUser);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void enable(@PathVariable int id, @RequestParam boolean enabled) {
        log.info("turn enabled={} for user with id {}", enabled, id);
        User user = repository.getOne(id);
        user.setEnabled(enabled);
        repository.save(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.info("delete user with id {}", id);
        repository.deleteById(id);
    }
}
