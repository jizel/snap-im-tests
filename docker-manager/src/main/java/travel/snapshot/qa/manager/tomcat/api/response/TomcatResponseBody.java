package travel.snapshot.qa.manager.tomcat.api.response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class TomcatResponseBody {

    private List<String> body = new ArrayList<String>();

    public void setBody(List<String> body) {
        this.body = body;
    }

    public void addLine(String line) {
        this.body.add(line);
    }

    public void addLines(List<String> lines) {
        this.body.addAll(lines);
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
