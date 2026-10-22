package com.testappian.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads config.properties and credentials.properties from the classpath and
 * exposes them as typed lookups. Credentials resolve from environment
 * variables first (APPIAN_USERNAME / APPIAN_PASSWORD) so CI pipelines never
 * need a committed secrets file; local runs fall back to
 * credentials.properties (gitignored, seeded from credentials.properties.example).
 */
public final class ConfigReader {

    private static final Properties CONFIG = load("config.properties");
    private static final Properties CREDENTIALS = load("credentials.properties");

    private ConfigReader() {
    }

    private static Properties load(String resourceName) {
        Properties props = new Properties();
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new IllegalStateException("Could not find " + resourceName + " on the classpath. "
                        + "If this is credentials.properties, copy credentials.properties.example to "
                        + "credentials.properties and fill in real values.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + resourceName, e);
        }
        return props;
    }

    public static String get(String key) {
        String value = CONFIG.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing config.properties key: " + key);
        }
        return value;
    }

    public static String siteUrl() {
        return get("appian.site.url");
    }

    public static String appianVersion() {
        return get("appian.version");
    }

    public static String appianLocale() {
        return get("appian.locale");
    }

    public static int timeoutSeconds() {
        return Integer.parseInt(get("appian.timeout.seconds"));
    }

    public static String browser() {
        return get("browser");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless"));
    }

    public static String username() {
        String env = System.getenv("APPIAN_USERNAME");
        return env != null && !env.isBlank() ? env : CREDENTIALS.getProperty("appian.username");
    }

    public static String password() {
        String env = System.getenv("APPIAN_PASSWORD");
        return env != null && !env.isBlank() ? env : CREDENTIALS.getProperty("appian.password");
    }
}
