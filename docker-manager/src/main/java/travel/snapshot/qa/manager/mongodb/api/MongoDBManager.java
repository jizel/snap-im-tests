package travel.snapshot.qa.manager.mongodb.api;

import com.mongodb.MongoClient;
import travel.snapshot.qa.manager.api.ServiceManager;
import travel.snapshot.qa.manager.mongodb.configuration.MongoDBManagerConfiguration;

/**
 * Mongo operations interface. Gathers methods which simplifies and enables you to interact with Mongo.
 */
public interface MongoDBManager extends ServiceManager {

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
     * @throws MongoDBManagerException if it is not possible to close MongoDB client successfully.
     */
    void closeClient() throws MongoDBManagerException;
}
