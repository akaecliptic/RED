package dev.akaecliptic.models;

import java.util.ArrayList;
import java.util.List;

public class Information {

    private String poster;
    private String backdrop;

    private Integer runtime;
    private String tagline;

    private List<Integer> genres;


    public Information() {
        this.poster = null;
        this.backdrop = null;

        this.runtime = null;
        this.tagline = null;

        this.genres = new ArrayList<>();
    }

    public Information(String poster, String backdrop) {
        this.poster = poster;
        this.backdrop = backdrop;

        this.runtime = null;
        this.tagline = null;

        this.genres = new ArrayList<>();
    }

    public Information(String poster, String backdrop, int runtime, String tagline, List<Integer> genres) {
        this.poster = poster;
        this.backdrop = backdrop;

        this.runtime = runtime;
        this.tagline = tagline;

        this.genres = genres;
    }

    @Override
    public String toString() {
        return "Information { " +
                    "poster: '" + this.poster +
                    "', backdrop: '" + this.backdrop +
                    "', genres: " + this.genres +
                    ", runtime: " + this.runtime +
                    ", tagline: '" + this.tagline +
                "' }";
    }

    public String toStringPretty() {
        return "Information { \n\t" +
                    "poster: '" + this.poster + "',\n\t" +
                    "backdrop: '" + this.backdrop + "',\n\t" +
                    "genres: " + this.genres + ",\n\t" +
                    "runtime: " + this.runtime + ",\n\t" +
                    "tagline: '" + this.tagline + "'\n\t" +
                "}";
    }

    // GETTERS
    public List<Integer> getGenres() {
        return this.genres;
    }

    public void setGenres(List<Integer> genres) {
        this.genres = genres;
    }

    public int getRuntime() {
        return this.runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public String getPoster() {
        return this.poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getBackdrop() {
        return this.backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public String getTagline() {
        return this.tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }
}
