package travel.snapshot.dp.qa.junit.utils.issueKeeperJiraCredentials;


import static com.jayway.restassured.RestAssured.given;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.tools.ikeeper.IssueDetails;
import qa.tools.ikeeper.client.connector.JiraConnector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Issue keeper Jira connector that works with credentials. Unfortunately all the original code cannot be reused.
 *
 * Private method get from JiraConnector needs to be modified to use credentials. Hence both method (get and getIssues
 * that uses it) bodies were copied and only slightly changed.
 *
 * This class can be removed once (if) IssueKeeper (https://github.com/ibek/issue-keeper) supports authentication.
 */
public class JiraCredentialsConnector extends JiraConnector {

    private static final Logger LOG = LoggerFactory.getLogger(qa.tools.ikeeper.client.connector.JiraConnector.class);

    private static Map<String, IssueDetails> cache = new HashMap<String, IssueDetails>();
    private static boolean active = true;
    private static final String BASE64_JIRA_CREDENTIALS = "dGVzdC5xYTpKZWRub3JvemVjMTIz";

    private String urlDomain;

    JiraCredentialsConnector(String urlDomain) {
        super(urlDomain);
        this.urlDomain = urlDomain;
    }

    @Override
    public IssueDetails getIssue(String id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }
        IssueDetails details = new IssueDetails();
        details.setId(id);

        try {
            String url = urlDomain + "/rest/api/2/issue/" + id + "?fields=summary,fixVersions,status,project";
            String response = getWithCredentials(url);

            JsonObject jsonFields = new JsonParser().parse(response).getAsJsonObject().getAsJsonObject("fields");
            JsonObject jsonStatus = jsonFields.getAsJsonObject("status");
            String statusName = jsonStatus.get("name").getAsString();
            String summary = jsonFields.get("summary").getAsString();
            details.setTitle(summary);
            details.setStatusName(statusName.toUpperCase());
            JsonObject project = jsonFields.getAsJsonObject("project");
            details.setProject("JIRA@" + project.get("key").getAsString());

            Iterator<JsonElement> itelm = jsonFields.get("fixVersions").getAsJsonArray().iterator();
            String fixVersion = null;
            if (itelm.hasNext()) {
                fixVersion = itelm.next().getAsJsonObject().get("name").getAsString();
                details.setTargetVersion(fixVersion);
            }

            cache.put(id, details);
        } catch (Exception ex) {
            LOG.warn(ex.getClass().getName() + " " + ex.getMessage());
        }

        return details;
    }

    private String getWithCredentials(String url) {
        if (!active) {
            return null;
        }
        String result = null;
        BufferedReader in = null;
        try {
            Response response = given()
                    .header("Authorization", "Basic " + BASE64_JIRA_CREDENTIALS)
                    .header("Accept", "application/json")
                    .get(url);

            if (response.getStatusCode() != 200) {
                throw new RuntimeException("Failed to contact Jira on URL:" + url + ", HTTP error code : " + response.getStatusCode());
            }

            in = new BufferedReader(new InputStreamReader(response.asInputStream()));
            String inputLine;
            StringBuilder responseBuffer = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                responseBuffer.append(inputLine);
            }

            result = responseBuffer.toString();
        } catch (UnknownHostException ex) {
            String msg = "Issue Keeper - UnknownHostException: " + ex.getMessage()
                    + ", turning off - all tests will run";
            LOG.warn(msg);
            System.out.println(msg);
            active = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Unable to close reader for the connection to URL:" + url);
                }
            }
        }
        return result;
    }
}
