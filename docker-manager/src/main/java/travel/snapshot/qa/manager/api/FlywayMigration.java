package travel.snapshot.qa.manager.api;

import org.flywaydb.core.Flyway;

/**
 * Interface to be implemented in case we need to add migration capability to some database manager.
 */
public interface FlywayMigration {

    /**
     * Returns Flyway object which performs migrations against specified {@code scheme}. Data source, user name and
     * password will be constructed from the underlying configuration. Migrations will be picked from the default
     * location.
     *
     * @param scheme scheme to use for migration
     * @return Flyway instance configured to execute migrations
     */
    Flyway flyway(String scheme);

    /**
     * Returns Flyway object which is prepared for executing migrations against specified {@code dataSource} and {@code
     * scheme}. There is data source already set with username and password found in configuration.
     *
     * @param dataSource database to set for migration
     * @param scheme     locations of migration scripts to execute against the database
     * @return Flyway instance configured to execute migrations
     */
    Flyway flyway(String dataSource, String scheme);

    /**
     * Returns Flyway object which is prepared for executing migrations against specified {@code dataSource} and {@code
     * scheme}. There is data source already set with username and password found in configuration.
     *
     * @param dataSource         datasource to set for migration
     * @param scheme             scheme to use for migration
     * @param migrationLocation  location of migration scripts to execute against the datasource and scheme
     * @param migrationLocations addition locations of migration scripts to execute against the datasource and scheme
     * @return Flyway instance configured to execute migrations
     */
    Flyway flyway(String dataSource, String scheme, String migrationLocation, String... migrationLocations);
}
