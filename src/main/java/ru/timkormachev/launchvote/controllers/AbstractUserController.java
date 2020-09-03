package ru.timkormachev.launchvote.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import ru.timkormachev.launchvote.model.HasId;
import ru.timkormachev.launchvote.util.UniqueMailValidator;

import static ru.timkormachev.launchvote.util.ValidationUtil.assureIdConsistent;
import static ru.timkormachev.launchvote.util.ValidationUtil.checkModificationAllowed;

public abstract class AbstractUserController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final UniqueMailValidator emailValidator;

    // Validate manually cause UniqueMailValidator doesn't work for update with user.id==null
    private WebDataBinder binder;

    protected AbstractUserController(UniqueMailValidator emailValidator) {
        this.emailValidator = emailValidator;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null && emailValidator.supports(binder.getTarget().getClass())) {
            binder.addValidators(emailValidator);
            this.binder = binder;
        }
    }

    protected void checkAndValidateForUpdate(HasId user, int id) throws BindException {
        log.info("update {} with id={}", user, id);
        assureIdConsistent(user, id);
        checkModificationAllowed(id);
        binder.validate();
        if (binder.getBindingResult().hasErrors()) {
            throw new BindException(binder.getBindingResult());
        }
    }


}
