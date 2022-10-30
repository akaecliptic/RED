package dev.akaecliptic.core;

import com.google.gson.*;
import dev.akaecliptic.models.Configuration;
import dev.akaecliptic.models.Information;
import dev.akaecliptic.models.Movie;
import dev.akaecliptic.models.Page;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hello, this class is responsible for converting the raw JSON values to their corresponding class abstractions.
 */
public class MovieFactory {
    private static final Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Page.class, new PageDeserializer());
        builder.registerTypeAdapter(Movie.class, new MovieDeserializer());
        builder.registerTypeAdapter(Movie[].class, new MovieListDeserializer());
        builder.registerTypeAdapter(Configuration.class, new ConfigurationDeserializer());
        gson = builder.create();
    }

    /* EXPOSED METHODS */

    public static Movie create(JsonElement json) {
        return gson.fromJson(json, Movie.class);
    }

    public static Movie[] createList(JsonElement json) {
        return gson.fromJson(json, Movie[].class);
    }

    public static Map<Integer, String> createGenreMap(JsonElement element) {
        Map<Integer, String> map = new HashMap<>();

        if(element.isJsonNull() || element.getAsJsonObject().get("genres") == null) return map;

        JsonArray array = element.getAsJsonObject().get("genres").getAsJsonArray();

        for (int i = 0; i < array.size(); i++) {
            JsonObject json = array.get(i).getAsJsonObject();

            Integer key = getInt(json.get("id"));
            String value = getString(json.get("name"));

            map.put(key, value);
        }

        return map;
    }

    public static Configuration createConfig(JsonElement json) {
        return gson.fromJson(json, Configuration.class);
    }

    public static Page createPage(JsonElement json) {
        return gson.fromJson(json, Page.class);
    }

    /* INTERNAL METHODS */

    private static int getInt(JsonElement json) {
        if(json == null) return -1;

        if(json.isJsonNull()) return -1;

        return json.getAsInt();
    }

    private static String getString(JsonElement json) {
        if(json == null) return null;

        if(json.isJsonNull()) return null;

        if(json.getAsString().isBlank()) return null;

        return json.getAsString();
    }

    private static float getFloat(JsonElement json) {
        if(json == null) return -1;

        if(json.isJsonNull()) return -1;

        return json.getAsFloat();
    }

    private static JsonArray getArray(JsonElement json) {
        if(json == null) return null;

        if(json.isJsonNull()) return null;

        return json.getAsJsonArray();
    }

    private static int formatRating(float rating) {
        return (rating == -1) ? -1 : Math.round(rating * 10);
    }

    private static Information parseInformation(JsonObject json) {
        String poster = getString(json.get("poster_path"));
        String backdrop = getString(json.get("backdrop_path"));
        int runtime = getInt(json.get("runtime"));
        String tagline = getString(json.get("tagline"));

        List<Integer> genres = parseGenre(json);

        return new Information(poster, backdrop, runtime, tagline, genres);
    }

    private static List<Integer> parseGenre(JsonObject parent) {
        List<Integer> genres = new ArrayList<>();

        if(parent.has("genres")) {

            JsonArray array = getArray(parent.get("genres"));
            for (JsonElement element : array) {
                genres.add(element.getAsJsonObject().get("id").getAsInt());
            }

        } else if(parent.has("genre_ids")) {

            JsonArray array = getArray(parent.get("genre_ids"));
            for (JsonElement json : array) genres.add(json.getAsInt());

        }

        return genres;
    }

    private static Movie parseMovie(JsonObject json) {
        int id = getInt(json.get("id"));
        boolean seen = false;
        String title = getString(json.get("title"));
        String description = getString(json.get("overview"));
        int nativeRating = formatRating(getFloat(json.get("vote_average")));
        int userRating = 0;
        String stringDate = getString(json.get("release_date"));
        LocalDate release = (stringDate == null) ? Movie.INDEFINITE_DATE : LocalDate.parse(stringDate);

        Information information = parseInformation(json);

        return new Movie(id, title, seen, description, nativeRating, userRating, release, information);
    }

    /* DESERIALIZERS */
    // TODO: 2022-10-29 Revisit this, these seem redundant. Refactor for 0.2.6

    private static class MovieDeserializer implements JsonDeserializer<Movie> {
        @Override
        public Movie deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = element.getAsJsonObject();
            return parseMovie(json);
        }
    }

    private static class MovieListDeserializer implements JsonDeserializer<Movie[]> {
        @Override
        public Movie[] deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonArray array = element.getAsJsonArray();
            Movie[] movies = new Movie[array.size()];

            for (int i = 0; i < array.size(); i++) {
                JsonObject json = array.get(i).getAsJsonObject();
                movies[i] = parseMovie(json);
            }

            return movies;
        }

    }

    private static class PageDeserializer implements JsonDeserializer<Page> {
        @Override
        public Page deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject page = element.getAsJsonObject();
            if(page == null || page.isJsonNull()) return null;

            JsonArray array = page.getAsJsonArray("results");
            List<Movie> movies = new ArrayList<>();

            for (int i = 0; i < array.size(); i++) {
                JsonObject json = array.get(i).getAsJsonObject();
                movies.add(parseMovie(json));
            }

            int number = getInt(page.get("page"));
            int total = getInt(page.get("total_pages"));

            return new Page(number, total, movies);
        }

    }

    private static class ConfigurationDeserializer implements JsonDeserializer<Configuration> {
        @Override
        public Configuration deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject parent = element.getAsJsonObject();
            JsonObject json = parent.getAsJsonObject("images");

            if(json == null || json.isJsonNull()) return null;

            String secure = getString(json.get("secure_base_url"));
            String base = getString(json.get("base_url"));

            JsonArray backdropSizes = getArray(json.get("backdrop_sizes"));
            JsonArray posterSizes = getArray(json.get("poster_sizes"));

            List<String> backdrops = new ArrayList<>();
            List<String> posters = new ArrayList<>();

            backdropSizes.forEach(child -> backdrops.add(getString(child)));
            posterSizes.forEach(child -> posters.add(getString(child)));

            return new Configuration(base, secure, backdrops.toArray(new String[]{}), posters.toArray(new String[]{}));
        }

    }
}
