package com.dreamzite.dzanalytics.model;

public class Tweet {
    private long id;
    private int rewteets;
    private int likes;

    public Tweet(long id, int rewteets, int likes) {
        this.id = id;
        this.rewteets = rewteets;
        this.likes = likes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRewteets() {
        return rewteets;
    }

    public void setRewteets(int rewteets) {
        this.rewteets = rewteets;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
