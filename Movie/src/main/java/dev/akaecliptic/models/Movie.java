package dev.akaecliptic.models;

import java.time.LocalDate;

public class Movie extends Media {

    public static final LocalDate INDEFINITE_DATE = LocalDate.of(1888, 1, 1);

    private Information information;

    public Movie(int id, String title, boolean seen, String description, int nativeRating, int userRating, LocalDate release, Information information) {
        super(id, title, seen, description, nativeRating, userRating, release);
        this.information = information;
    }

    public Movie(int id, String title, boolean seen, String description, int nativeRating, int userRating, LocalDate release) {
        super(id, title, seen, description, nativeRating, userRating, release);
        this.information = new Information();
    }

    public Movie(String title, boolean seen, String description, int nativeRating, int userRating, LocalDate release) {
        super(title, seen, description, nativeRating, userRating, release);
        this.information = new Information();
    }

    public Information getInfo() {
        return this.information;
    }

    public void setInfo(Information information) {
        this.information = information;
    }

    @Override
    public String toString() {
        return "Movie { " +
                    "id: " + this.id +
                    ", seen: " + this.seen +
                    ", title: '" + this.title + '\'' +
                    ", description: '" + this.description + '\'' +
                    ", nativeRating: " + this.nativeRating +
                    ", userRating: " + this.userRating +
                    ", release: " + this.release +
                    ", " + this.information.toString() +
                " }";
    }

    public String toStringPretty() {
        return "Movie { \n\t" +
                    "id: " + this.id + ",\n\t" +
                    "seen: " + this.seen + ",\n\t" +
                    "title: '" + this.title + "',\n\t" +
                    "description: '" + this.description + "',\n\t" +
                    "nativeRating: " + this.nativeRating + ",\n\t" +
                    "userRating: " + this.userRating + ",\n\t" +
                    "release: " + this.release + ",\n\t" +
                    this.information.toStringPretty() + "\n" +
                "}";
    }
}
