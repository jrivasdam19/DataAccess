package com.jrivas.FileCreatorApplication.data;

public class Article {
    private String title;
    private String date;
    private String uri;

    public Article() {

    }

    public Article(String title, String date, String uri) {
        this.title = title;
        this.date = date;
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
