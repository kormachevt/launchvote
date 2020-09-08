package ru.timkormachev.launchvote.exception;

public class InvalidVotingTimeException extends ApplicationException {
    public static final String INVALID_VOTE_TIME_EXCEPTION = "exception.votes.invalidVoteTime";

    public InvalidVotingTimeException() {
        super(ErrorType.VALIDATION_ERROR, INVALID_VOTE_TIME_EXCEPTION);
    }
}