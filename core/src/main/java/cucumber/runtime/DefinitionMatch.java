package cucumber.runtime;

import cucumber.api.Scenario;
import gherkin.GherkinDialect;
import gherkin.pickles.PickleStep;;

public interface DefinitionMatch {
    void runStep(GherkinDialect i18n, Scenario scenario) throws Throwable;

    void dryRunStep(GherkinDialect i18n, Scenario scenario) throws Throwable;

    boolean isHook();

    Match getMatch();

    PickleStep getStep();

    String getStepText();

    String getPattern();

    String getSourceLocation();
}
