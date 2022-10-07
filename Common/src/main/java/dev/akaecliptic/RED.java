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
 * Hello, RED is part of the REDc package.
 *
 * The overall goal of this product is to server as the main HTTP API for Cinephile
 * and any other future projects.
 *
 * The goal is not to reinvent the wheel, or create a brand-new library.
 * Just a fun mini project, to replace the HTTP library currently being used,
 * which is not particularly fun to use.
 *
 * This hyper focused on the Cinephile use-case. As of 09/2022, the 2 criteria are:
 * > GET/POST calls
 * > Async calls
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

    //USE THESE

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

    //UNDER THE HOOD

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
