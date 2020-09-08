package ru.timkormachev.launchvote.util;


import ru.timkormachev.launchvote.exception.InvalidVotingTimeException;

import java.time.LocalTime;

public class VotesUtil {
    public static int calcPercentage(long thisVotes, int totalVotes) {
        return (int) Math.round(thisVotes * 100.0 / totalVotes);
    }

    public static void checkVoteTime(LocalTime stopVoteTime, LocalTime currentTime) {
        if (currentTime.isAfter(stopVoteTime)) {
            throw new InvalidVotingTimeException();
        }
    }
}
