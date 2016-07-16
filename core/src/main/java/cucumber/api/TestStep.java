package cucumber.api;

import cucumber.api.event.TestStepFinished;
import cucumber.api.event.TestStepStarted;
import cucumber.runner.EventBus;
import cucumber.runtime.DefinitionMatch;
import cucumber.runtime.StopWatch;
import cucumber.runtime.UndefinedStepDefinitionException;
import gherkin.GherkinDialect;
import gherkin.pickles.PickleStep;

import java.util.Arrays;

public class TestStep {
    private static final String[] PENDING_EXCEPTIONS = {
            "org.junit.AssumptionViolatedException",
            "org.junit.internal.AssumptionViolatedException"
    };
    static {
        Arrays.sort(PENDING_EXCEPTIONS);
    }
    private static final Object DUMMY_ARG = new Object();
    private final StopWatch stopWatch;
    protected final DefinitionMatch definitionMatch;

    public TestStep(DefinitionMatch definitionMatch, StopWatch stopWatch) {
        this.definitionMatch = definitionMatch;
        this.stopWatch = stopWatch;
    }

    public boolean isHook() {
        return definitionMatch.isHook();
    }

    public String getStepText() {
        return definitionMatch.getStepText();
    }
    public PickleStep getPickleStep() {
        return definitionMatch.getStep();
    }

    public String getPattern() {
        return definitionMatch.getPattern();
    }

    public String getLocation() {
        return definitionMatch.getSourceLocation();
    }

    public Result run(EventBus bus, GherkinDialect i18n, Scenario scenario, boolean skipSteps) {
        bus.send(new TestStepStarted(this));
        String status;
        Throwable error = null;
        stopWatch.start();
        try {
            status = executeStep(i18n, scenario, skipSteps);
        } catch (Throwable t) {
            error = t;
            status = mapThrowableToStatus(t);
        }
        long duration = stopWatch.stop();
        Result result = mapStatusToResult(status, error, duration);
        bus.send(new TestStepFinished(this, result));
        return result;
    }

    protected String nonExceptionStatus(boolean skipSteps) {
        return skipSteps ? Result.SKIPPED.getStatus() : Result.PASSED;
    }

    protected String executeStep(GherkinDialect i18n, Scenario scenario, boolean skipSteps) throws Throwable {
        if (!skipSteps) {
            definitionMatch.runStep(i18n, scenario);
            return Result.PASSED;
        } else {
            definitionMatch.dryRunStep(i18n, scenario);
            return Result.SKIPPED.getStatus();
        }
    }

    private String mapThrowableToStatus(Throwable t) {
        if (t.getClass().isAnnotationPresent(Pending.class) || Arrays.binarySearch(PENDING_EXCEPTIONS, t.getClass().getName()) >= 0) {
            return "pending";
        }
        if (t.getClass() == UndefinedStepDefinitionException.class) {
            return Result.UNDEFINED.getStatus();
        }
        return Result.FAILED;
    }

    private Result mapStatusToResult(String status, Throwable error, long duration) {
        Long resultDuration = duration;
        Throwable resultError = error;
        if (status == Result.SKIPPED.getStatus()) {
            return Result.SKIPPED;
        }
        if (status == Result.UNDEFINED.getStatus()) {
            return Result.UNDEFINED;
        }
        return new Result(status, resultDuration, resultError, DUMMY_ARG);
    }
}
