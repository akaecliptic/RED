package dev.akaecliptic.models;

public class Configuration {

    private final String imageURL;
    private final String secureImageURL;

    private final String[] backdrops;
    private final String[] posters;

    public Configuration(String imageURL, String secureImageURL, String[] backdrops, String[] posters) {
        this.imageURL = imageURL;
        this.secureImageURL = secureImageURL;
        this.backdrops = backdrops;
        this.posters = posters;
    }

    public String image(String size, String url) {
        return String.format("%s%s/%s", this.secureImageURL, size, url);
    }

    public String image(boolean secure, String size, String url) {
        String base = (secure) ? this.secureImageURL : this.imageURL;
        return String.format("%s%s/%s", base, size, url);
    }

    // GETTERS
    public String base() {
        return this.imageURL;
    }

    public String secure() {
        return this.secureImageURL;
    }

    public String[] backdrops() {
        return this.backdrops;
    }

    public String[] posters() {
        return this.posters;
    }
}
