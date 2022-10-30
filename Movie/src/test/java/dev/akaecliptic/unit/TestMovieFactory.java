package dev.akaecliptic.unit;

import dev.akaecliptic.RED;
import dev.akaecliptic.TMDBCaller;
import dev.akaecliptic.auxiliary.MockRED;
import dev.akaecliptic.core.MovieFactory;
import dev.akaecliptic.core.RequestException;
import dev.akaecliptic.core.TMDBContext;
import dev.akaecliptic.models.Information;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestMovieFactory {

    private static final String LOGAN_DESCRIPTION = "In the near future, a weary Logan cares for an ailing Professor X " +
            "in a hideout on the Mexican border. But Logan's attempts to hide from the world and his legacy are upended " +
            "when a young mutant arrives, pursued by dark forces.";

    private TMDBCaller caller;
    private RED red;
    private MockRED delegate;

    @Before
    public void init() {
        red = mock(RED.class);
        setMock(red);

        caller = new TMDBCaller(new TMDBContext());
        delegate = MockRED.instance();
    }

    private void setMock(RED mock) {
        try {
            Field instance = RED.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException("Error found mocking RED", e);
        }
    }

    @After
    public void clearMock() {
        try {
            Field instance = RED.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException("Error found mocking RED", e);
        }
    }

    private void mockMovie() throws RequestException {
        when(red.json(contains("https"))).thenReturn(delegate.getMovie());
    }

    private void mockMovieList() throws RequestException {
        when(red.json(contains("https"))).thenReturn(delegate.getMovieList());
    }

    @Test
    public void test_createMovie() throws RequestException {
        mockMovie();
        var json = caller.movie(263115);

        var movie = MovieFactory.create(json);

        assertEquals(263115, movie.getId());
        assertEquals("Logan", movie.getTitle());
        assertEquals(LOGAN_DESCRIPTION, movie.getDescription());

        assertFalse(movie.isSeen());

        assertEquals(78, movie.getNativeRating());
        assertEquals(0, movie.getUserRating());
        assertEquals(LocalDate.parse("2017-02-28"), movie.getRelease());

        Information information = movie.getInfo();

        assertEquals("/fnbjcRDYn6YviCcePDnGdyAkYsB.jpg", information.getPoster());
        assertEquals("/9X7YweCJw3q8Mcf6GadxReFEksM.jpg", information.getBackdrop());
        assertEquals(137, information.getRuntime());
        assertEquals("His time has come.", information.getTagline());
        assertEquals(List.of(28, 18, 878), information.getGenres());
    }

    @Test
    public void test_createMovieList() throws RequestException {
        mockMovieList();
        var json = caller.search("Spider-Man", 1);
        var results = json.getAsJsonObject().getAsJsonArray("results");

        var list = MovieFactory.createList(results);

        var movie1 = list[0];
        var movie2 = list[1];
        var movie3 = list[2];

        assertEquals(634649, movie1.getId());
        assertEquals("Spider-Man: No Way Home", movie1.getTitle());
        assertEquals(LocalDate.parse("2021-12-15"), movie1.getRelease());

        assertEquals(List.of(28, 12, 878), movie1.getInfo().getGenres());


        assertEquals(557, movie2.getId());
        assertEquals("Spider-Man", movie2.getTitle());
        assertEquals(LocalDate.parse("2002-06-14"), movie2.getRelease());

        assertEquals(List.of(14, 28), movie2.getInfo().getGenres());


        assertEquals(315635, movie3.getId());
        assertEquals("Spider-Man: Homecoming", movie3.getTitle());
        assertEquals(LocalDate.parse("2017-07-05"), movie3.getRelease());

        assertEquals(List.of(28, 12, 878, 18), movie3.getInfo().getGenres());
    }
}
