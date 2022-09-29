package dev.akaecliptic.core;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TMDBContext {
    private static final Logger LOGGER = Logger.getLogger(TMDBContext.class.getName());

    private String apiKey;

    private final String language;
    private final String region;

    public TMDBContext() {
        String file = "/keys.properties";
        try {
            InputStream stream = TMDBContext.class.getResourceAsStream(file);

            Properties properties = new Properties();
            properties.load(stream);

            this.apiKey = properties.getProperty("TMDB.AuthV3");
        } catch (Exception e) {
            String message = "Exception found trying to read properties file {0}.\n" +
                    "Please try manually setting setting api key or creating a valid" + file + " file.\n" +
                    "Falling back to null...";
            LOGGER.log(Level.WARNING, message, file);

            this.apiKey = null;
        }
        this.language = "&language=en-GB";
        this.region = "&region=GB";
    }

    public TMDBContext(String apiKey) {
        this.apiKey = apiKey;
        this.language = "&language=en-GB";
        this.region = "&region=GB";
    }

    public TMDBContext(String apiKey, String language, String region) {
        this.apiKey = apiKey;
        this.language = "&language=" + language;
        this.region = "&region=" + language;
    }

    public String requireApiKey() throws ApiKeyException {
        if(this.apiKey == null) throw new ApiKeyException("Invalid api key. Please check and set api key.");
        return "?api_key=" + this.apiKey;
    }

    public String getAPIKey() {
        return this.apiKey;
    }

    public void setAPIKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getLanguage() {
        return language;
    }

    public String getRegion() {
        return region;
    }

    public String baseURL(QueryType type) {
        return String.format("https://api.themoviedb.org/3/%s", type);
    }

    public String movieURL(int id) {
        return String.format("https://api.themoviedb.org/3/%s/%s", QueryType.MOVIE, id);
    }

    public String image(String size, String url) {
        return String.format("https://image.tmdb.org/t/p/%s/%s", size, url);
    }

    public String query(String query) {
        return "&query=" + query;
    }

    public String page(int page) {
        return "&page=" + page;
    }

    public enum QueryType {
        MOVIE("movie"),
        SEARCH("search/movie"),

        CONFIG("configuration"),
        GENRE("genre/movie/list"),

        RATED("movie/top_rated"),
        POPULAR("movie/popular"),
        UPCOMING("movie/upcoming"),
        PLAYING("movie/now_playing");

        private final String type;

        QueryType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return this.type;
        }
    }
}
