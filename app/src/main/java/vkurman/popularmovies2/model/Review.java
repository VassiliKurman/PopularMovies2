package vkurman.popularmovies2.model;

/**
 * Review class for Movie.
 * Created by Vassili Kurman on 22/03/2018.
 * Version 1.0
 */

public class Review {
    private long id;
    private String author;
    private String content;
    private String url;

    public Review(long id, String author, String content, String url) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}