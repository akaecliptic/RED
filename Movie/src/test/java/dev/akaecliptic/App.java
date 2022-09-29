package dev.akaecliptic;

import dev.akaecliptic.core.MovieFactory;
import dev.akaecliptic.core.RequestException;
import dev.akaecliptic.core.TMDBContext;
import dev.akaecliptic.models.Movie;

public class App {

    public static void main(String[] args) throws RequestException {
        var c = new TMDBCaller(new TMDBContext());
        var j = c.search("Logan", 1);
        var j2 = c.movie(263115);
        var g = c.genre();
        var g1 = c.popular(1);
        var g2 = c.upcoming(1);
        var g3 = c.rated(1);

        System.out.println(MovieFactory.createGenreMap(g));

        var m = MovieFactory.createList(j.getAsJsonObject().get("results"));
        var l = MovieFactory.create(j2);

        System.out.println(l.toStringPretty());

        for (Movie movie : m) {
            System.out.println(movie.toStringPretty());
        }
    }
}
