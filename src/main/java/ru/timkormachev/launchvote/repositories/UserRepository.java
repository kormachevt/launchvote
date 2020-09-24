package ru.timkormachev.launchvote.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.timkormachev.launchvote.exception.NotFoundException;
import ru.timkormachev.launchvote.model.User;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Integer> {

    User getByEmail(String email);

    default User findOrThrowById(Integer id) {
        return findById(id).orElseThrow(() -> new NotFoundException("id=" + id));
    }
}
