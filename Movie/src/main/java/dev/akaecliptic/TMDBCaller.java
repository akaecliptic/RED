package dev.akaecliptic;

import com.google.gson.JsonElement;
import dev.akaecliptic.core.RequestException;
import dev.akaecliptic.core.TMDBContext;
import dev.akaecliptic.core.TMDBContext.QueryType;

public class TMDBCaller {
    private final RED red;
    private final TMDBContext context;

    public TMDBCaller(TMDBContext context) {
        this.context = context;
        this.red = RED.instance();
    }

    public JsonElement movie(int id) throws RequestException {
        String query = context.movieURL(id) +
                        context.requireApiKey() +
                        context.getLanguage() +
                        context.getRegion();
        return red.json(query);
    }

    public JsonElement upcoming(int page) throws RequestException {
        String query = context.baseURL(QueryType.UPCOMING) +
                        context.requireApiKey() +
                        context.getRegion() +
                        context.page(page);
        return red.json(query);
    }

    public JsonElement rated(int page) throws RequestException {
        String query = context.baseURL(QueryType.RATED) +
                        context.requireApiKey() +
                        context.getRegion() +
                        context.page(page);
        return red.json(query);
    }

    public JsonElement popular(int page) throws RequestException {
        String query = context.baseURL(QueryType.POPULAR) +
                        context.requireApiKey() +
                        context.getRegion() +
                        context.page(page);
        return red.json(query);
    }

    public JsonElement playing(int page) throws RequestException {
        String query = context.baseURL(QueryType.PLAYING) +
                        context.requireApiKey() +
                        context.getRegion() +
                        context.page(page);
        return red.json(query);
    }

    public JsonElement search(String param, int page) throws RequestException {
        String query = context.baseURL(QueryType.SEARCH) +
                        context.requireApiKey() +
                        context.getLanguage() +
                        context.query(param) +
                        context.getRegion() +
                        context.page(page);
        return red.json(query);
    }

    public JsonElement genre() throws RequestException {
        String query = context.baseURL(QueryType.GENRE) +
                        context.requireApiKey() +
                        context.getLanguage();
        return red.json(query);
    }

    public JsonElement config() throws RequestException {
        String query = context.baseURL(QueryType.CONFIG) +
                        context.requireApiKey();
        return red.json(query);
    }
}
