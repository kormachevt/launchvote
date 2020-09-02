package ru.timkormachev.launchvote.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Voting has already been finished")
public class InvalidVoteTimeException extends RuntimeException {
}
