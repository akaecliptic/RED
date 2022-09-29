package dev.akaecliptic;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dev.akaecliptic.auxiliaries.Pool;
import dev.akaecliptic.core.Callback;
import dev.akaecliptic.core.Request;
import dev.akaecliptic.core.RequestException;
import dev.akaecliptic.core.Response;
import dev.akaecliptic.enums.ContentType;
import dev.akaecliptic.enums.MethodType;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
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
 * This hyper focused on the Cinephile use-case. As of 09/2022, the 3 criteria are:
 * > GET/POST/PUT calls
 * > Async calls
 * > Pool calls
 *
 */
public class RED {
    private static final Logger LOGGER = Logger.getLogger(RED.class.getName());

    private static final String HEADER_LOCATION = "location";
    private static final Set<Integer> REDIRECTS;

    static {
        REDIRECTS = new HashSet<>();
        REDIRECTS.add(300);
        REDIRECTS.add(301);
        REDIRECTS.add(302);
        REDIRECTS.add(307);
        REDIRECTS.add(308);
    }

    private static RED instance;
    private final HttpClient client;
    private static final int DEFAULT_TIMEOUT = 30;

    private RED() {
        this.client = HttpClient.newBuilder()
                .version(Version.HTTP_2)
                .followRedirects(Redirect.NORMAL)
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
        Request request = new Request(url, ContentType.JSON, MethodType.GET);
        String response = call(request);
        return JsonParser.parseString(response);
    }

    public String get(String url) throws RequestException {
        Request request = new Request(url);
        return call(request);
    }

    public String post(String url, String body) throws RequestException {
        Request request = new Request(url, ContentType.TEXT, MethodType.POST);
        return call(request, BodyPublishers.ofString(body));
    }

    public void async(String url, Callback<String> callback) throws RequestException {
        Request request = new Request(url);
        async(request, callback);
    }

    public Pool collect(Callback<String> callback, String... urls) throws RequestException{
        LinkedList<CompletableFuture<Void>> pool = new LinkedList<>();
        for (String url : urls) {
            Request request = new Request(url);
            pool.add(async(request, callback));
        }
        return new Pool(pool);
    }


    //UNDER THE HOOD

    private String call(Request request) throws RequestException {
        try {
            HttpResponse<String> response = client.send(request.make(), BodyHandlers.ofString());
            int status = response.statusCode();

            if(status > 399){
                String message = String.format("Exception caused when making request to URL: '%s'", request.getURL());
                LOGGER.log(Level.WARNING, message);
                throw new RequestException(message);
            }

            if(REDIRECTS.contains(status)) {
                Optional<String> location = response.headers().firstValue(HEADER_LOCATION);

                if (location.isPresent()) {
                    request.setURL(location.get());
                    response = client.send(request.make(), BodyHandlers.ofString());
                }
            }

            return response.body();
        } catch ( Exception e ) {
            String message = String.format("Exception caused when making request to URL: '%s'", request.getURL());
            LOGGER.log(Level.WARNING, message);
            throw new RequestException(message, e);
        }
    }

    private String call(Request request, BodyPublisher bodyPublisher) throws RequestException {
        try {
            HttpResponse<String> response = client.send(request.make(bodyPublisher), BodyHandlers.ofString());
            int status = response.statusCode();

            if(status > 399){
                String message = String.format("Exception caused when making request to URL: '%s'", request.getURL());
                LOGGER.log(Level.WARNING, message);
                throw new RequestException(message);
            }

            if(REDIRECTS.contains(status)) {
                Optional<String> location = response.headers().firstValue(HEADER_LOCATION);

                if (location.isPresent()) {
                    request.setURL(location.get());
                    response = client.send(request.make(), BodyHandlers.ofString());
                }
            }

            return response.body();
        } catch ( Exception e ) {
            String message = String.format("Exception caused when making request to URL: '%s'", request.getURL());
            LOGGER.log(Level.WARNING, message);
            throw new RequestException(message, e);
        }
    }

    private CompletableFuture<Void> async(Request request, Callback<String> callback) throws RequestException {
        try {
            return client.sendAsync(request.make(), BodyHandlers.ofString())
                    .thenApply(RED::toResponse)
                    .thenAccept(callback);
        } catch ( Exception e ) {
            String message = String.format("Exception caused when making async request to URL: '%s'", request.getURL());
            LOGGER.log(Level.WARNING, message);
            throw new RequestException(message, e);
        }
    }

    private static <T> Response<T> toResponse(HttpResponse<T> response) {
        return new Response<>(response);
    }
}
