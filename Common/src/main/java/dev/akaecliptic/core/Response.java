package dev.akaecliptic.core;

import java.net.http.HttpResponse;

@Deprecated
public class Response<T> {

    private final HttpResponse<T> response;
    private final boolean error;
    private final T body;
    private final int status;

    public Response( HttpResponse<T> response ){
        this.response = response;
        this.status = response.statusCode();
        this.error = (this.status > 399);
        this.body = response.body();
    }

    public HttpResponse<T> get() {
        return this.response;
    }

    public boolean isError() {
        return this.error;
    }

    public T getBody() {
        return this.body;
    }

    public int getStatus() {
        return this.status;
    }
}
