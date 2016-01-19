package travel.snapshot.qa.manager.tomcat.api.response;

/**
 * Represents a status of a response from a Tomcat container. This is is the very first string in the first line of the
 * response message.
 */
public enum TomcatResponseStatus {
    OK,
    FAIL;
}
