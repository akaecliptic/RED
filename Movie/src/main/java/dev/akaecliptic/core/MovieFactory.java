package dev.akaecliptic.core;

import com.google.gson.*;
import dev.akaecliptic.models.Information;
import dev.akaecliptic.models.Movie;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieFactory {
    private static final Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Movie.class, new MovieDeserializer());
        builder.registerTypeAdapter(Movie[].class, new MovieListDeserializer());
        gson = builder.create();
    }

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

    private static int format(float vote_average) {
        return (vote_average == -1) ? -1 : Math.round(vote_average * 10);
    }

    private static Information parseInformation(JsonObject json) {
        String poster = getString(json.get("poster_path"));
        String backdrop = getString(json.get("backdrop_path"));
        Integer runtime = getInt(json.get("runtime"));
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

    private static class MovieDeserializer implements JsonDeserializer<Movie> {
        @Override
        public Movie deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = element.getAsJsonObject();

            int id = getInt(json.get("id"));
            boolean seen = false;
            String title = getString(json.get("title"));
            String description = getString(json.get("overview"));
            int nativeRating = format(getFloat(json.get("vote_average")));
            int userRating = 0;
            String stringDate = getString(json.get("release_date"));
            LocalDate release = (stringDate == null) ? null : LocalDate.parse(stringDate);

            Information information = parseInformation(json);

            return new Movie(id, seen, title, description, nativeRating, userRating, release, information);
        }
    }

    private static class MovieListDeserializer implements JsonDeserializer<Movie[]> {
        @Override
        public Movie[] deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonArray array = element.getAsJsonArray();
            Movie[] movies = new Movie[array.size()];

            for (int i = 0; i < array.size(); i++) {
                JsonObject json = array.get(i).getAsJsonObject();

                int id = getInt(json.get("id"));
                boolean seen = false;
                String title = getString(json.get("title"));
                String description = getString(json.get("overview"));
                int nativeRating = format(getFloat(json.get("vote_average")));
                int userRating = 0;
                String stringDate = getString(json.get("release_date"));
                LocalDate release = (stringDate == null) ? null : LocalDate.parse(stringDate);

                Information information = parseInformation(json);

                movies[i] = new Movie(id, seen, title, description, nativeRating, userRating, release, information);
            }

            return movies;
        }

    }
}
