package ru.timkormachev.launchvote.to;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Data
public class TestResultTo {

    @NotBlank
    private String restaurant;
    @Range(max = 100)
    private Long percentage;

    public TestResultTo() {
    }

    public TestResultTo(String restaurant, Long percentage) {
        this.restaurant = restaurant;
        this.percentage = percentage;
    }
}
