package ru.timkormachev.launchvote.util;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import ru.timkormachev.launchvote.exception.ExceptionInfoHandler;
import ru.timkormachev.launchvote.exception.IllegalRequestDataException;
import ru.timkormachev.launchvote.model.HasIdAndEmail;
import ru.timkormachev.launchvote.model.User;
import ru.timkormachev.launchvote.repositories.UserRepository;


@Component
public class UniqueMailValidator implements org.springframework.validation.Validator {

    private final UserRepository repository;

    public UniqueMailValidator(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return HasIdAndEmail.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        HasIdAndEmail user = ((HasIdAndEmail) target);
        String email = user.getEmail();
        if (email == null) {
            throw new IllegalRequestDataException("email cant'be null");
        }
        User dbUser = repository.getByEmail(email.toLowerCase());
        if (dbUser != null && !dbUser.getId().equals(user.getId())) {
            errors.rejectValue("email", ExceptionInfoHandler.EXCEPTION_DUPLICATE_EMAIL);
        }
    }
}
