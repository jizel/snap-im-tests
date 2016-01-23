package travel.snapshot.qa.manager.api;

import org.flywaydb.core.Flyway;

/**
 * Interface to be implemented in case we need to add migration capability to some database manager.
 */
public interface FlywayMigration {

    /**
     * Returns Flyway object which is prepared for executing migrations. There is data source already set with username
     * and password found in configuration and its database is used as well. There will be baseline method called on the
     * flyway instance before it is returned to caller of this method.
     *
     * @param migrationLocations locations of migration scripts to execute against the database
     * @return flyway instance configured to execute migrations
     */
    Flyway flyway(String... migrationLocations);

    /**
     * Returns Flyway object which is prepared for executing migrations. There is data source already set with username
     * and password found in configuration and specified {@code database} used as well. There will be baseline method
     * called on the flyway instance before it is returned to caller of this method.
     *
     * @param database           database to set for migration
     * @param migrationLocations locations of migration scripts to execute against the database
     * @return Flyway instance configured to execute migrations
     */
    Flyway flyway(String database, String... migrationLocations);

    /**
     * Returns Flyway object which is prepared for executing migrations against specified {@code database}. There is
     * data source already set with username and password found in configuration. This method overrides database set in
     * configuration by the specified one.
     *
     * @param database           database to set for migration
     * @param baseline           sets baseline flag, respective flyway's baseline method will be called automatically
     *                           when set to true
     * @param migrationLocations locations of migration scripts to execute agains the
     * @return Flyway instance configured to execute migrations
     */
    Flyway flyway(String database, boolean baseline, String... migrationLocations);
}
