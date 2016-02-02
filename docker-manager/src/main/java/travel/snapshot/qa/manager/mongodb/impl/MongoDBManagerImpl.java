package travel.snapshot.qa.manager.mongodb.impl;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.CountDownWatch;
import travel.snapshot.qa.manager.api.BasicWaitingCondition;
import travel.snapshot.qa.manager.mongodb.api.MongoDBManager;
import travel.snapshot.qa.manager.mongodb.api.MongoDBManagerException;
import travel.snapshot.qa.manager.mongodb.check.MongoDBStartCheckTask;
import travel.snapshot.qa.manager.mongodb.configuration.MongoDBManagerConfiguration;

import java.util.concurrent.TimeUnit;

public class MongoDBManagerImpl implements MongoDBManager {

    private final MongoDBManagerConfiguration configuration;

    public MongoDBManagerImpl() {
        this(new MongoDBManagerConfiguration.Builder().build());
    }

    public MongoDBManagerImpl(final MongoDBManagerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void waitForConnectivity() {
        Spacelift.task(configuration, MongoDBStartCheckTask.class).execute().until(
                new CountDownWatch(configuration.getStartupTimeoutInSeconds(), TimeUnit.SECONDS.SECONDS),
                new BasicWaitingCondition());
    }

    @Override
    public MongoDBManagerConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public MongoClient getClient() {
        return new MongoClient(new MongoClientURI("mongodb://" + configuration.getConnectionString()));
    }

    @Override
    public void closeClient(final MongoClient mongoClient) {
        try {
            mongoClient.close();
        } catch (Exception ex) {
            throw new MongoDBManagerException(String.format("Unable to close MongoDB client: %s", ex.getMessage()), ex);
        }
    }
}
