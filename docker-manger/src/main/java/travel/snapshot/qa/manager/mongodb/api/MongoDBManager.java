package travel.snapshot.qa.manager.mongodb.api;

import com.mongodb.MongoClient;
import travel.snapshot.qa.manager.api.ServiceManager;
import travel.snapshot.qa.manager.mongodb.configuration.MongoDBManagerConfiguration;

/**
 * Mongo operations interface. Gathers methods which simplifies and enables you to interact with Mongo.
 */
public interface MongoDBManager extends ServiceManager {

    /**
     * Blocks until it is possible to connect to Mongo.
     *
     * @see travel.snapshot.qa.manager.mongodb.check.MongoDBStartCheckTask
     */
    void waitForConnectivity();

    /**
     * Gets configuration of Mongo manager. When not set explicitly, default Mongo configuration should be used.
     *
     * @return client configuration of Mong Manager.
     */
    MongoDBManagerConfiguration getConfiguration();

    /**
     * Return Mongo client from Mongo driver library.
     *
     * @return client to Mongo database
     */
    MongoClient getClient();

    /**
     * Closes Mongo client.
     *
     * @param mongoClient client to close
     */
    void closeClient(MongoClient mongoClient);
}
