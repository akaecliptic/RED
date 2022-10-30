package dev.akaecliptic.auxiliary;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Hello, this class is used to delegate queries. RED API calls will be intercepted with Mockito,
 * calling methods from here instead.
 *
 * The files read are local resource files, stored from previous API calls to TMDB.
 */
public class MockRED {

    private static final String MOVIE_FILE = "/movie.json";
    private static final String MOVIE_LIST_FILE = "/movies.json";
    private static MockRED instance;

    private String movieFile;
    private String movieListFile;

    private MockRED() {
        var m = this.getClass().getResource(MOVIE_FILE);
        var l = this.getClass().getResource(MOVIE_LIST_FILE);

        if (m == null || l == null) return;

        movieFile = m.getFile();
        movieListFile = l.getFile();
    }

    public static MockRED instance() {
        if(instance == null) {
            instance = new MockRED();
        }
        return instance;
    }

    public JsonElement getMovie() {
        String out;

        try {
            out = Files.readString(new File(movieFile).toPath());
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Error reading movie file at location '" + movieFile + "'.", e);
        }

        return JsonParser.parseString((out == null) ? "" : out);
    }

    public JsonElement getMovieList() {
        String out;

        try {
            out = Files.readString(new File(movieListFile).toPath());
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Error reading movie file at location '" + movieListFile + "'.", e);
        }

        return JsonParser.parseString((out == null) ? "" : out);
    }
}
