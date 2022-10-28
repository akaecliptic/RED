package dev.akaecliptic.models;

import java.util.List;

public class Page {

    private final int total;
    private final int number;
    private final List<Movie> results;

    public Page(int number, int total, List<Movie> results) {
        this.total = total;
        this.number = number;
        this.results = results;
    }

    public boolean paginate() {
        return this.number < this.total;
    }

    public List<Movie> results() {
        return results;
    }

    public int number() {
        return number;
    }

    public int total() {
        return total;
    }
}
