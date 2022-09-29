package dev.akaecliptic.models;

import java.io.Serializable;
import java.time.LocalDate;

public abstract class Media implements Serializable {

    protected int id;
    protected boolean seen;
    protected String title;
    protected String description;
    protected int nativeRating;
    protected int userRating;
    protected LocalDate release;

    public Media(int id, boolean seen, String title, String description, int nativeRating, int userRating, LocalDate release) {
        this.id = id;
        this.seen = seen;
        this.title = title;
        this.description = description;
        this.nativeRating = nativeRating;
        this.userRating = userRating;
        this.release = release;
    }

    public Media(boolean seen, String title, String description, int nativeRating, int userRating, LocalDate release) {
        this.seen = seen;
        this.title = title;
        this.description = description;
        this.nativeRating = nativeRating;
        this.userRating = userRating;
        this.release = release;
    }

    @Override
    public String toString() {
        return "Media { " +
                    "id: " + this.id +
                    ", seen: " + this.seen +
                    ", title: '" + this.title + '\'' +
                    ", description: '" + this.description + '\'' +
                    ", nativeRating: " + this.nativeRating +
                    ", userRating: " + this.userRating +
                    ", release: " + this.release +
                " }";
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSeen() {
        return this.seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNativeRating() {
        return this.nativeRating;
    }

    public void setNativeRating(int nativeRating) {
        this.nativeRating = nativeRating;
    }

    public int getUserRating() {
        return this.userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public LocalDate getRelease() {
        return this.release;
    }

    public void setRelease(LocalDate release) {
        this.release = release;
    }
}
