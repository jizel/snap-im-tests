package travel.snapshot.qa.manager.mongodb.impl;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import travel.snapshot.qa.manager.mongodb.api.MongoDBManager;
import travel.snapshot.qa.manager.mongodb.api.MongoDBManagerException;
import travel.snapshot.qa.manager.mongodb.configuration.MongoDBManagerConfiguration;

/**
 * Manages an instance of MongoDB.
 */
public class MongoDBManagerImpl implements MongoDBManager {

    private final MongoDBManagerConfiguration configuration;

    private MongoClient mongoClient;

    public MongoDBManagerImpl() {
        this(new MongoDBManagerConfiguration.Builder().build());
    }

    public MongoDBManagerImpl(final MongoDBManagerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public MongoDBManagerConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public MongoClient getClient() {

        if (mongoClient == null) {
            mongoClient = new MongoClient(new MongoClientURI("mongodb://" + configuration.getConnectionString()));
        }

        return mongoClient;
    }

    @Override
    public void closeClient() throws MongoDBManagerException {
        try {
            getClient().close();
        } catch (Exception ex) {
            throw new MongoDBManagerException(String.format("Unable to close MongoDB client: %s", ex.getMessage()), ex);
        }
    }
}
