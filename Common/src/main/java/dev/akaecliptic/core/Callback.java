package dev.akaecliptic.core;

import java.util.function.Consumer;

public interface Callback<T> extends Consumer<Response<T>> {
    void accept(Response<T> response);
}
