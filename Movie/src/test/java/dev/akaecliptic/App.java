package dev.akaecliptic;

import dev.akaecliptic.core.MovieFactory;
import dev.akaecliptic.core.RequestException;
import dev.akaecliptic.core.TMDBContext;
import dev.akaecliptic.models.Movie;

public class App {

    public static void main(String[] args) throws RequestException {
        var c = new TMDBCaller(new TMDBContext(args[0]));
        var search = c.search("Hello", 1);
        var movie = c.movie(263115);

        System.out.println(MovieFactory.create(movie));

        var movies = MovieFactory.createPage(search.getAsJsonObject());

        for (Movie m : movies.results()) {
            System.out.println(m.getRelease().getYear());
        }
    }
}
