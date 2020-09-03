package ru.timkormachev.launchvote.util;

import lombok.experimental.UtilityClass;
import ru.timkormachev.launchvote.util.exception.InvalidVoteTimeException;

import java.time.LocalTime;

@UtilityClass
public class VotesUtil {
    public int calcPercentage(long thisVotes, int totalVotes) {
        return (int) Math.round(thisVotes * 100.0 / totalVotes);
    }

    public void checkVoteTime(LocalTime stopVoteTime) {
        if (LocalTime.now().isAfter(stopVoteTime)) {
            throw new InvalidVoteTimeException();
        }
    }
}
