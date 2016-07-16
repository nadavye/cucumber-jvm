package cucumber.runner;

import cucumber.api.Result;
import cucumber.api.Scenario;
import cucumber.api.TestStep;
import cucumber.runtime.DefinitionMatch;
import cucumber.runtime.StopWatch;
import gherkin.GherkinDialect;;

public class UnskipableStep extends TestStep {

    public UnskipableStep(DefinitionMatch definitionMatch, StopWatch stopWatch) {
        super(definitionMatch, stopWatch);
    }

    protected String executeStep(GherkinDialect i18n, Scenario scenario, boolean skipSteps) throws Throwable {
        definitionMatch.runStep(i18n, scenario);
        return Result.PASSED;
    }

}
