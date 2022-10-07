package dev.akaecliptic.core;

import java.util.function.Consumer;

public interface Callback extends Consumer<String> {
    void accept(String response);
}
