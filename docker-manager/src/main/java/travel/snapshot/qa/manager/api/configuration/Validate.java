package travel.snapshot.qa.manager.api.configuration;

import travel.snapshot.qa.manager.api.container.ContainerManagerConfigurationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class Validate {

    private Validate() {
    }

    /**
     * Checks that object is not null, throws exception if it is.
     *
     * @param obj     The object to check
     * @param message The exception message
     * @throws IllegalArgumentException Thrown if obj is null
     */
    public static void notNull(final Object obj, final String message) throws IllegalArgumentException {

        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Checks that the specified String is not null or empty, throws exception if it is.
     *
     * @param string  The object to check
     * @param message The exception message
     * @throws IllegalArgumentException Thrown if obj is null
     */
    public static void notNullOrEmpty(final String string, final String message) throws IllegalArgumentException {

        if (string == null || string.length() == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Checks if an object is a null object or an empty String.
     *
     * @param arg argument to check for nullity or emptiness
     * @return true if {@code} arg is a null object or an empty String
     */
    public static boolean isNullOrEmpty(final String arg) {
        return arg == null || arg.isEmpty();
    }

    /**
     * Checks that path represents a valid file
     *
     * @param path    The path to file
     * @param message The exception message
     * @throws IllegalArgumentException Throws if given file does not exist or if it cannot be read
     */
    public static void isValidFile(final String path, final String message) throws IllegalArgumentException {

        notNull(path, message);
        final File file = new File(path);

        if (!file.exists() || !file.canRead()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Checks that string is not null and not empty and it represents a path to a valid directory
     *
     * @param string  The path to check
     * @param message The exception message
     * @throws ContainerManagerConfigurationException Thrown if string is empty, null or it does not represent a path to
     *                                                a valid directory
     */
    public static void directoryExists(final String string, final String message) throws ContainerManagerConfigurationException {

        if (string == null || string.length() == 0 || !new File(string).isDirectory()) {
            throw new ContainerManagerConfigurationException(message);
        }
    }

    /**
     * Checks that string is not null and not empty and it represents a path to a valid directory
     *
     * @param file    The file to check
     * @param message The exception message
     * @throws ContainerManagerConfigurationException Thrown if string is empty, null or it does not represent a path to
     *                                                a valid directory
     */
    public static void directoryExists(final File file, final String message) throws ContainerManagerConfigurationException {
        try {
            directoryExists(file.getCanonicalPath(), message);
        } catch (IOException ex) {
            throw new ContainerManagerConfigurationException(ex);
        }
    }

    /**
     *
     * @param file
     * @throws FileNotFoundException
     * @throws FileNotExecutableException
     */
    public static void isExecutableFile(File file) throws FileNotFoundException, FileNotExecutableException {
        notNull(file, "File to check is a null object.");

        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath() + " does not exist.");
        }

        if (!file.canExecute()) {
            throw new FileNotExecutableException(file + " is not executable.");
        }
    }

    public static class FileNotExecutableException extends Exception {

        private static final long serialVersionUID = -2281993207167380102L;

        public FileNotExecutableException(String message) {
            super(message);
        }
    }
}
