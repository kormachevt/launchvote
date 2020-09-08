package ru.timkormachev.launchvote.to;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Data
public class ResultTo {

    @NotBlank
    private String restaurant;
    @Range(max = 100)
    private Integer percentage;

    public ResultTo() {

    }

    public ResultTo(String restaurant, Integer percentage) {
        this.restaurant = restaurant;
        this.percentage = percentage;
    }
}
