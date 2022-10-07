package dev.akaecliptic.core;

import dev.akaecliptic.enums.ContentType;
import dev.akaecliptic.enums.MethodType;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpRequest.BodyPublisher;
import java.time.Duration;

@Deprecated
public class Request {
    public static final int DEFAULT_TIMEOUT = 15;
    public static final int DEFAULT_RETRY = 3;
    public static final ContentType DEFAULT_CONTENT_TYPE = ContentType.TEXT;
    public static final MethodType DEFAULT_METHOD_TYPE = MethodType.GET;

    private String url;
    private ContentType contentType;
    private MethodType methodType;
    private int timeout;
    private int retry;

    private Request() { }

    public Request(String url, ContentType contentType, MethodType methodType, int timeout, int retry) {
        this.url = url;
        this.contentType = contentType;
        this.methodType = methodType;
        this.timeout = timeout;
        this.retry = retry;
    }

    public Request(String url, ContentType contentType, MethodType methodType, int timeout) {
        this(url, contentType, methodType, timeout, DEFAULT_RETRY);
    }

    public Request(String url, ContentType contentType, MethodType methodType) {
        this(url, contentType, methodType, DEFAULT_TIMEOUT, DEFAULT_RETRY);
    }

    public Request(String url) {
        this(url, DEFAULT_CONTENT_TYPE, DEFAULT_METHOD_TYPE, DEFAULT_TIMEOUT, DEFAULT_RETRY);
    }

    public HttpRequest make() throws Exception {
        if (methodType.equals(MethodType.GET)){
            return HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(timeout))
                    .header("Content-Type", contentType.value())
                    .build();
        }

        String message = String.format("Exception found when calling 'make()' with MethodType: '%s'", methodType);
        throw new Exception(message);
    }

    public HttpRequest make(BodyPublisher bodyPublisher) throws Exception {
        if(methodType.equals(MethodType.PUT) || methodType.equals(MethodType.POST)){
            Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(timeout))
                    .header("Content-Type", contentType.value());

            setHttpMethod(builder, bodyPublisher);

            return builder.build();
        }

        String message = String.format("Exception found when calling 'make(BodyPublisher)' with MethodType: '%s'", methodType);
        throw new Exception(message);
    }

    public String getURL() {
        return this.url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public int getRetry() {
        return this.retry;
    }

    private void setHttpMethod(Builder builder, BodyPublisher bodyPublisher) {
        switch (methodType){
            case PUT:
                builder.PUT(bodyPublisher);
                break;
            case POST:
                builder.POST(bodyPublisher);
                break;
            default:
        }
    }
}
