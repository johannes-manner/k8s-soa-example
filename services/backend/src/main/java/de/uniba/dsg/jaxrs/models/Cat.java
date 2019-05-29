package de.uniba.dsg.jaxrs.models;

public class Cat {
    public int id;
    public String name;
    public String imageUrl;
    public String movie;

    public Cat() {}

    public Cat(int id, String name, String imageUrl, String movie) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.movie = movie;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
