package cucumber.runtime.formatter;

import cucumber.api.event.EventPublisher;
import cucumber.api.formatter.Formatter;

import java.util.List;

class NullFormatter implements Formatter {
    public NullFormatter() {
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
    }
}
