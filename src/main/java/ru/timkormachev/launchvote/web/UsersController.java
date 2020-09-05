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
import ru.timkormachev.launchvote.exception.NotFoundException;
import ru.timkormachev.launchvote.model.User;
import ru.timkormachev.launchvote.repositories.UserRepository;
import ru.timkormachev.launchvote.util.UniqueMailValidator;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static ru.timkormachev.launchvote.util.ValidationUtil.*;

@RestController
@RequestMapping(UsersController.REST_URL)
public class UsersController extends AbstractUserController {
    static final String REST_URL = "/users";

    private final UserRepository repository;

    private final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UsersController(UserRepository userRepository, UniqueMailValidator emailValidator) {
        super(emailValidator);
        this.repository = userRepository;
    }

    @GetMapping
    public List<User> getAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "login", "email"));
    }

    @GetMapping("/{id}")
    public User get(@PathVariable int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("id=" + id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createWithLocation(@Valid @RequestBody User user) {
        checkNew(user);
        user.setPassword(encoder.encode(user.getPassword()));
        User created = repository.save(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody User newUser, @PathVariable int id) throws BindException {
        checkAndValidateForUpdate(newUser, id);
        assureIdConsistent(newUser, id);
        repository.save(newUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        checkModificationAllowed(id);
        repository.deleteById(id);
    }
}
