package travel.snapshot.qa.manager.tomcat.api.response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class TomcatResponseBody {

    private List<String> body = new ArrayList<>();

    public TomcatResponseBody() {
    }

    public TomcatResponseBody(List<String> body) {
        addLines(body);
    }

    public TomcatResponseBody setBody(List<String> body) {
        this.body = body;
        return this;
    }

    public TomcatResponseBody addLine(String line) {
        this.body.add(line);
        return this;
    }

    public TomcatResponseBody addLines(List<String> lines) {
        this.body.addAll(lines);
        return this;
    }

    public List<String> getBody() {
        return Collections.unmodifiableList(body);
    }

    @Override
    public String toString() {
        final StringJoiner lineJoiner = new StringJoiner("\n");
        body.forEach(lineJoiner::add);
        return lineJoiner.toString();
    }
}
