package ru.timkormachev.launchvote.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.timkormachev.launchvote.exception.NotFoundException;
import ru.timkormachev.launchvote.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    User getByEmail(String email);

    default User findOrThrowById(Integer id) {
        return findById(id).orElseThrow(() -> new NotFoundException("id=" + id));
    }
}
