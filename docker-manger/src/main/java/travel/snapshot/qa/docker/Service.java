package travel.snapshot.qa.docker;

import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.manager.api.Configuration;
import travel.snapshot.qa.manager.api.ServiceManager;

/**
 * Presents a view to any Docker service which is backed by particular service manager implementation and its respective
 * configuration.
 *
 * @param <T> type of service manager implementation this Docker service will handle
 * @param <U> type of the configuration object this Docker service will handle
 */
public interface Service<T extends ServiceManager, U extends Configuration> {

    /**
     * Builds Docker service manager for some particular Docker service using custom configuration and container ID
     * which will be managed afterwards.
     *
     * @param configuration configuration of a service
     * @param containerId   container ID which this service will manage
     * @return concrete Docker service implementation for given service type
     */
    DockerServiceManager<T> init(final U configuration, final String containerId);

    /**
     * Configuration of the service will be the default one.
     *
     * @param containerId container ID which service will manage
     * @return concrete Docker service implementation for given service type
     */
    DockerServiceManager<T> init(final String containerId);

    /**
     * Builds Docker service manager for some particular Docker service.  There will be default container ID used.
     *
     * @param configuration configuration of a service
     * @return concrete Docker service implementation for given service type
     */
    DockerServiceManager<T> init(final U configuration);

    /**
     * Default configuration and default container ID for given service type should be used. This is the most minimal
     * version of build method. In most cases, it is sufficient to use this method. In case you need to do any
     * configuration, you have to call other build methods more suitable for additional service configuration.
     *
     * @return concrete Docker service implementation for given service type
     */
    DockerServiceManager<T> init();
}