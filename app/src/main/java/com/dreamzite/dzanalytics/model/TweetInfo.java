package com.dreamzite.dzanalytics.model;

import java.util.ArrayList;

public class TweetInfo {

    private String date;
    private ArrayList<Tweet> tweets;

    public TweetInfo() {
        this.date = "";
        this.tweets = null;
    }

    public TweetInfo(String date, ArrayList<Tweet> tweets) {
        this.date = date;
        this.tweets = tweets;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(ArrayList<Tweet> tweets) {
        this.tweets = tweets;
    }
}
