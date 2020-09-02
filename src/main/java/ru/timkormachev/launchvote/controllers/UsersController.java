package ru.timkormachev.launchvote.controllers;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.timkormachev.launchvote.model.User;
import ru.timkormachev.launchvote.repositories.UserRepository;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static ru.timkormachev.launchvote.util.ValidationUtil.assureIdConsistent;

@RestController
@RequestMapping(UsersController.REST_URL)
public class UsersController {
    static final String REST_URL = "/users";

    private final UserRepository repository;

    public UsersController(UserRepository userRepository) {
        this.repository = userRepository;
    }

    @GetMapping
    public List<User> getAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "login", "email"));
    }

    @GetMapping("/{id}")
    public User get(@PathVariable int id) {
        return repository.findById(id).orElseThrow();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createWithLocation(@Valid @RequestBody User user) {
        User created = repository.save(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody User newUser, @PathVariable int id) {
        assureIdConsistent(newUser, id);
        repository.save(newUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        repository.deleteById(id);
    }
}
