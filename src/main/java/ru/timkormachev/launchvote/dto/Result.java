package ru.timkormachev.launchvote.dto;

import lombok.Data;

@Data
public class Result {
    private final String restaurant;
    private final int percentage;
}
