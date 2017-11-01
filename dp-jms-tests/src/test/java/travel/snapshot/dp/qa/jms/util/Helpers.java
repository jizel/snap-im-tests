package travel.snapshot.dp.qa.jms.util;

/**
 * Helper methods for dp jms tests
 */
public class Helpers {

    public static void wrapException(Callable method) {
        try {
            method.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
