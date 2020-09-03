package ru.timkormachev.launchvote.util;


import ru.timkormachev.launchvote.exception.IllegalRequestDataException;

import java.time.LocalTime;

public class VotesUtil {
    public static int calcPercentage(long thisVotes, int totalVotes) {
        return (int) Math.round(thisVotes * 100.0 / totalVotes);
    }

    public static void checkVoteTime(LocalTime stopVoteTime) {
        if (LocalTime.now().isAfter(stopVoteTime)) {
            throw new IllegalRequestDataException("Voting time is over");
        }
    }
}
