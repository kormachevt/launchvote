package ru.timkormachev.launchvote.util;


import java.time.LocalTime;

public class VotesUtil {
    public static boolean isRevoteAllowed(LocalTime stopVoteTime, LocalTime currentTime) {
        return currentTime.isBefore(stopVoteTime);
    }
}
