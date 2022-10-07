package dev.akaecliptic.auxiliaries;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

//TODO - Revisit
/*
    Unfortunately, migrating to OkHTTP makes it very clunky and not ideal .
    May revisit if needed later.
 */
@Deprecated
public class Pool implements Runnable {

    private final CompletableFuture<Void> pool;
    private final AtomicBoolean processed = new AtomicBoolean(false);

    public Pool(LinkedList<CompletableFuture<Void>> pool){
        this.pool = CompletableFuture
                        .allOf(pool.toArray(new CompletableFuture[]{}))
                        .thenRun(() -> processed.set(true));
    }

    boolean isProcessed() {
        return this.processed.get();
    }

    @Override
    public void run() {
        this.pool.join();
    }
}
