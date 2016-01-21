package travel.snapshot.qa.manager.mongodb.check;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoSocketReadException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoDatabase;
import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.manager.mongodb.configuration.MongoDBManagerConfiguration;

/**
 * Checks that it is possible to list collections on a given database provided in configuration. This means that Mongo
 * service is trully up and running.
 *
 * @see MongoDBManagerConfiguration
 */
public class MongoDBStartCheckTask extends Task<MongoDBManagerConfiguration, Boolean> {

    private static final int MONGO_CLIENT_CONNECTION_TIMEOUT = 5;

    @Override
    protected Boolean process(MongoDBManagerConfiguration configuration) throws Exception {

        final MongoClientOptions mongoClientOptions = new MongoClientOptions.Builder()
                .connectTimeout(MONGO_CLIENT_CONNECTION_TIMEOUT)
                .build();

        MongoClient mongoClient = null;

        boolean running = false;

        try {
            mongoClient = new MongoClient(configuration.getConnectionString(), mongoClientOptions);
            final MongoDatabase mongoDatabase = mongoClient.getDatabase(configuration.getDatabase());

            for (final String name : mongoDatabase.listCollectionNames()) {
                // This will throw MongoTimeoutException when it is not possible to connect.
                // Plain creation of MongoClient and getting database does not actually reach Mongo server at all.
            }
            running = true;
        } catch (MongoTimeoutException | MongoSocketReadException ex) {
            // intentionally empty
        } finally {
            if (mongoClient != null) {
                try {
                    mongoClient.close();
                } catch (Exception ex) {
                    // intentionally empty
                }
            }
        }

        return running;
    }
}
