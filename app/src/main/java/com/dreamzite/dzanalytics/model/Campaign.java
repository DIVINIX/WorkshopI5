package com.dreamzite.dzanalytics.model;
import java.util.ArrayList;

public class Campaign {

    private String status;
    private int id;
    private String name;
    private String hashtag;
    private String user;
    String startAt;
    boolean isActive;
    ArrayList<TweetCount> tweetCounts;
    ArrayList<TweetInfo> tweetInfo;

    public Campaign(String status, int id, String name, String hashtag, String user, String startAt, boolean isActive, ArrayList<TweetCount> tweetCounts, ArrayList<TweetInfo> tweetInfo) {
        this.status = status;
        this.id = id;
        this.name = name;
        this.hashtag = hashtag;
        this.user = user;
        this.startAt = startAt;
        this.isActive = isActive;
        this.tweetCounts = tweetCounts;
        this.tweetInfo = tweetInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public ArrayList<TweetCount> getTweetCounts() {
        return tweetCounts;
    }

    public void setTweetCounts(ArrayList<TweetCount> tweetCounts) {
        this.tweetCounts = tweetCounts;
    }

    public ArrayList<TweetInfo> getTweetInfo() {
        return tweetInfo;
    }

    public void setTweetInfo(ArrayList<TweetInfo> tweetInfo) {
        this.tweetInfo = tweetInfo;
    }
}
