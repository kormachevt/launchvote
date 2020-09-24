package ru.timkormachev.launchvote;

import ru.timkormachev.launchvote.to.TestResultTo;

public class VotesTestData {
    public static TestResultTo RESULT_1 = new TestResultTo("Alfa", 50L);
    public static TestResultTo RESULT_2 = new TestResultTo("Omega", 50L);
    public static TestResultTo RESULT_3 = new TestResultTo("Beta", 0L);
    public static TestMatcher<TestResultTo> RESULTS_MATCHER = TestMatcher.usingEqualsAssertions(TestResultTo.class);
}
