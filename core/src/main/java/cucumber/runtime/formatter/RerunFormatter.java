package cucumber.runtime.formatter;

import cucumber.api.TestCase;
import cucumber.api.event.EventHandler;
import cucumber.api.event.EventPublisher;
import cucumber.api.event.TestCaseFinished;
import cucumber.api.event.TestRunFinished;
import cucumber.api.formatter.Formatter;
import cucumber.api.formatter.NiceAppendable;
import cucumber.api.formatter.StrictAware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Formatter for reporting all failed features and print their locations
 * Failed means: (failed, undefined, pending) test result
 */
class RerunFormatter implements Formatter, StrictAware {
    private final NiceAppendable out;
    private Map<String, ArrayList<Integer>> featureAndFailedLinesMapping = new HashMap<String, ArrayList<Integer>>();
    private boolean isStrict = false;

    private EventHandler<TestCaseFinished> testCaseFinishedHandler = new EventHandler<TestCaseFinished>() {

        @Override
        public void receive(TestCaseFinished event) {
            if (!event.result.isOk(isStrict)) {
                recordTestFailed(event.testCase);
            }
        }
    };
    private EventHandler<TestRunFinished> runFinishHandler = new EventHandler<TestRunFinished>() {

        @Override
        public void receive(TestRunFinished event) {
            reportFailedTestCases();
            out.close();
        }
    };

    public RerunFormatter(Appendable out) {
        this.out = new NiceAppendable(out);
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseFinished.class, testCaseFinishedHandler);
        publisher.registerHandlerFor(TestRunFinished.class, runFinishHandler);
    }

    private void reportFailedTestCases() {
        Set<Map.Entry<String, ArrayList<Integer>>> entries = featureAndFailedLinesMapping.entrySet();
        boolean firstFeature = true;
        for (Map.Entry<String, ArrayList<Integer>> entry : entries) {
            if (!entry.getValue().isEmpty()) {
                if (!firstFeature) {
                    out.append(" ");
                }
                out.append(entry.getKey());
                firstFeature = false;
                for (Integer line : entry.getValue()) {
                    out.append(":").append(line.toString());
                }
            }
        }
    }

    private void recordTestFailed(TestCase testCase) {
        String path = testCase.getPath();
        ArrayList<Integer> failedTestCases = this.featureAndFailedLinesMapping.get(path);
        if (failedTestCases == null) {
            failedTestCases = new ArrayList<Integer>();
            this.featureAndFailedLinesMapping.put(path, failedTestCases);
        }

        failedTestCases.add(testCase.getLine());
    }

    @Override
    public void setStrict(boolean strict) {
        isStrict = strict;
    }
}
