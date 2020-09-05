package ru.timkormachev.launchvote.to;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ResultTo {

    @NotBlank
    private final String restaurant;

    private final int percentage;
}
