package com.dreamzite.dzanalytics.model;

import java.util.ArrayList;

public abstract class TwitterUtils {

    public static long getAllLikes(ArrayList<TweetInfo> tweetInfo) {
        long likesCount = 0;

        for (TweetInfo t : tweetInfo) {
            ArrayList<Tweet> tweets = t.getTweets();
            for (Tweet tw : tweets) {
                likesCount = likesCount + tw.getLikes();
            }
        }

        return likesCount;
    }

    public static long getAllRetweets(ArrayList<TweetInfo> tweetInfo) {
        long retweetsCount = 0;

        for (TweetInfo t : tweetInfo) {
            ArrayList<Tweet> tweets = t.getTweets();
            for (Tweet tw : tweets) {
                retweetsCount = retweetsCount + tw.getRewteets();
            }
        }

        return retweetsCount;
    }
}
