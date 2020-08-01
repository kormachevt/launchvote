package ru.timkormachev.launchvote.repositories;


import org.springframework.data.repository.CrudRepository;
import ru.timkormachev.launchvote.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {
}
