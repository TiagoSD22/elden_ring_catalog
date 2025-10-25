package eldenring.poc.config;

/**
 * Centralized API configuration.
 */
public final class ApiConfig {
    private static final String BASE_URL = "https://eldenring.wiki.fextralife.com";

    private ApiConfig() { }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}
