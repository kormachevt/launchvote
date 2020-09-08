package ru.timkormachev.launchvote;

import ru.timkormachev.launchvote.to.ResultTo;

public class VotesTestData {
    public static ResultTo RESULT_1 = new ResultTo("Alfa", 50);
    public static ResultTo RESULT_2 = new ResultTo("Omega", 50);
    public static TestMatcher<ResultTo> RESULTS_MATCHER = TestMatcher.usingEqualsAssertions(ResultTo.class);
}
