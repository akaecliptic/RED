package dev.akaecliptic;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dev.akaecliptic.core.Callback;
import dev.akaecliptic.core.RequestException;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello, this is the core of the RED package.
 *
 * The overall goal of this project is to serve as the main HTTP API for Cinephile
 * and any of my future projects.
 *
 * Just a fun mini project to replace the clunky HTTP library that was previously being used.
 * While also making this generic enough to be built on for different projects.
 *
 * This very simple and hyper focused use-case, as of now (09/2022), only has 2 criteria:
 * > Making GET/POST calls
 * > Making Async calls
 *
 */
public class RED {
    private static final Logger LOGGER = Logger.getLogger(RED.class.getName());

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final int DEFAULT_TIMEOUT = 30;
    private static final boolean FOLLOW_REDIRECT = true;

    private static RED instance;
    private final OkHttpClient _client;

    private RED() {
        this._client = new OkHttpClient().newBuilder()
                .followSslRedirects(FOLLOW_REDIRECT)
                .connectTimeout(Duration.ofSeconds(DEFAULT_TIMEOUT))
                .build();
    }

    public static RED instance() {
        if(instance == null) {
            instance = new RED();
        }
        return instance;
    }

    /* EXPOSED METHODS */

    public JsonElement json(String url) throws RequestException, JsonSyntaxException {
        Request request = new Request.Builder().url(url).get().build();
        String response = call(request);
        return JsonParser.parseString((response == null) ? "" : response);
    }

    public String get(String url) throws RequestException {
        Request request = new Request.Builder().url(url).get().build();
        return call(request);
    }

    public String post(String url, String body) throws RequestException {
        RequestBody requestBody = RequestBody.create(body, JSON);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return call(request);
    }

    public void async(String url, Callback callback) throws RequestException {
        Request request = new Request.Builder().url(url).get().build();
        async(request, callback);
    }

    /* INTERNAL METHODS */

    private String call(Request request) throws RequestException {
        try (Response response = _client.newCall(request).execute()) {
            if(!response.isSuccessful()) throw new RequestException("Unexpected code " + response);

            ResponseBody body = response.body();
            return (body == null) ? null : body.string();
        } catch ( Exception e ) {
            String message = String.format("Exception caused when making request to URL: '%s'", request.url());
            LOGGER.log(Level.WARNING, message);
            throw new RequestException(message, e);
        }
    }

    private void async(Request request, Callback callback) throws RequestException {
        try {
            _client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    String message = String.format("Exception caused when making request to URL: '%s'", call.request().url());
                    LOGGER.log(Level.WARNING, message);
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody body = response.body()) {
                        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                        callback.accept((body == null) ? null : body.string());
                    }
                }
            });
        } catch ( Exception e ) {
            String message = String.format("Exception caused when making async request to URL: '%s'", request.url());
            LOGGER.log(Level.WARNING, message);
            throw new RequestException(message, e);
        }
    }
}
