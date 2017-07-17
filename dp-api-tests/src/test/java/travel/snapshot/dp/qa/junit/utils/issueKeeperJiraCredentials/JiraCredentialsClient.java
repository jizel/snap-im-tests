package travel.snapshot.dp.qa.junit.utils.issueKeeperJiraCredentials;

import qa.tools.ikeeper.IssueDetails;
import qa.tools.ikeeper.annotation.Jira;
import qa.tools.ikeeper.client.JiraClient;
import qa.tools.ikeeper.client.connector.IssueTrackingSystemConnector;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Issue keeper Jira client that works with credentials. Unfortunately all the original code cannot be reused and
 * methods getIssues and getIssueConnector had to be copied and overridden using JiraCredentialsConnector instead of
 * JiraConnector.
 *
 * This class can be removed once (if) IssueKeeper (https://github.com/ibek/issue-keeper) supports authentication.
 *
 * This class also defines Jira action states. Alternative approach (config file) must be used if deleted.
 */
public class JiraCredentialsClient extends JiraClient {

    private final IssueTrackingSystemConnector jiraCredentialsConnector;

    public JiraCredentialsClient(String urlDomain) {
        super(urlDomain);
        jiraCredentialsConnector = new JiraCredentialsConnector(urlDomain);
    }

    @Override
    public List<IssueDetails> getIssues(Annotation annotation) {
        Jira jiraAnnotation = (Jira) annotation;

        String[] ids = jiraAnnotation.value();
        List<IssueDetails> detailsList = new ArrayList<IssueDetails>();
        for (String id : ids) {
            IssueDetails details = jiraCredentialsConnector.getIssue(id);
            detailsList.add(details);
        }

        return detailsList;
    }

    @Override
    public IssueTrackingSystemConnector getIssueConnector() {
        return jiraCredentialsConnector;
    }

    @Override
    public List<String> getDefaultActionStates() {
        return Arrays.asList("TO DO", "DEVELOPMENT", "REOPENED");
    }
}
