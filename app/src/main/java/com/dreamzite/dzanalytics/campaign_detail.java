package com.dreamzite.dzanalytics;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dreamzite.dzanalytics.model.Campaign;
import com.dreamzite.dzanalytics.model.Tweet;
import com.dreamzite.dzanalytics.model.TweetCount;
import com.dreamzite.dzanalytics.model.TweetInfo;
import com.dreamzite.dzanalytics.model.TwitterUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class campaign_detail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_detail);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        int id = (int) b.get("id");

        new JSONTask().execute("http://api.metweecs.com/campaign/poll/" + id);
    }

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

    public static long getAllLikesFromtweetInfo(TweetInfo tweetInfo) {
        long likesCount = 0;

            ArrayList<Tweet> tweets = tweetInfo.getTweets();
            for (Tweet tw : tweets) {
                likesCount = likesCount + tw.getLikes();
        }

        return likesCount;
    }

    public static long getAllRetweetsFromtweetInfo(TweetInfo tweetInfo) {
        long retweetsCount = 0;

            ArrayList<Tweet> tweets = tweetInfo.getTweets();
            for (Tweet tw : tweets) {
                retweetsCount = retweetsCount + tw.getRewteets();
            }

        return retweetsCount;
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

    public class JSONTask extends AsyncTask<String,String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try{
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                return buffer.toString();
            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try {
                ArrayList<TweetCount> tweetCounts; // Array for the tweetCounts information
                ArrayList<TweetInfo> tweetInfos; // Array for the tweetInfos infortmations, contains all the tweet with their informations

                // Get the global JSON object and get informations
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");

                // Get the "results" JSON object with basic informations
                JSONObject results = jsonObject.getJSONObject("results");
                int id = results.getInt("_id");
                String name = results.getString("name");
                String hashtag = results.getString("hashtag");
                String user = results.getString("user");
                long startAt = results.getLong("startsAt");
                boolean isActive = results.getBoolean("isActive");

                // Get the tweetCounts array from JSON and fill the tweetCounts array for the campaign
                JSONArray tweetCountsArray  = results.getJSONArray("tweetCounts");
                tweetCounts = new ArrayList<>(); // Will be in the campaign object
                for (int i=0; i < tweetCountsArray.length(); i++) {
                    try {
                        // Get the object which is an association with a timestamp and his tweetCounts
                        JSONObject object = tweetCountsArray.getJSONObject(i);
                        String key = object.keys().next();
                        long timestampDate = Long.parseLong(key);
                        String date = new SimpleDateFormat("yyy-MM-dd HH:mm:ss").format(new Date(timestampDate));
                        int counts = object.getInt(key);
                        tweetCounts.add(new TweetCount(date, counts));
                    } catch (JSONException e) {
                        // Oops
                    }
                }

                // Get the teweetInfo array from JSON and fill the tweetInfoArray for the campaign
                JSONArray tweetInfoArray  = results.getJSONArray("tweetInfo");
                tweetInfos = new ArrayList<>();  // Will be in the campaign object
                for (int i=0; i < tweetInfoArray.length(); i++)
                {
                    ArrayList<Tweet> tweets = new ArrayList<>(); // The ArrayList to store all the tweets for each tweetInfos
                    try {
                        // Get the timestamp object with all the tweets
                        JSONObject object = tweetInfoArray.getJSONObject(i);
                        String tweetTimestamp = object.keys().next();
                        long dateLong = Long.parseLong(tweetTimestamp);
                        String tweetTimestampDate = new SimpleDateFormat("yyy-MM-dd HH:mm:ss").format(new Date(dateLong));

                        // Get all tweets object into an array of 1
                        JSONArray tweetObjectArray = object.getJSONArray(tweetTimestamp);
                        // Get all tweets into and object to iterate on him
                        JSONObject tweetObject = tweetObjectArray.getJSONObject(0);
                        Iterator<String> iter = tweetObject.keys();
                        // Iterate on all the key of the tweets object
                        while (iter.hasNext()) {
                            String tweetId = iter.next();
                            // Get each tweets informations and put a new tweet into the array
                            try {
                                JSONObject subTweetObject = tweetObject.getJSONObject(tweetId) ;
                                int retweets = subTweetObject.getInt("retweets");
                                int likes = subTweetObject.getInt("likes");
                                Tweet tweet = new Tweet(Long.parseLong(tweetId), retweets, likes);
                                tweets.add(tweet);
                            } catch (JSONException e) {
                                // Something went wrong!
                            }
                        }
                        tweetInfos.add(new TweetInfo(tweetTimestampDate, tweets));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // Create the campagin object
                String dateStartAt = new SimpleDateFormat("yyy-MM-dd HH:mm:ss").format(new Date(startAt));
                Campaign campaign = new Campaign(status, id, name, hashtag, user, dateStartAt, isActive, tweetCounts, tweetInfos);

                // -----------------------
                // Fill the informations
                // -----------------------
                LinearLayout linearLayout = findViewById(R.id.details);

                // Set the title of the activity
                String title = campaign.getName();
                if (campaign.getHashtag().equals("null"))
                    title = title + " @" + campaign.getUser();
                else
                    title = title + " @" + campaign.getHashtag();

                setTitle(title);

                // Fill the start date
                TextView tvStratDate = findViewById(R.id.startAt);
                tvStratDate.setText(campaign.getStartAt());

                // Fille informations about tweets
                TextView tvLikesCount = new TextView(campaign_detail.this);
                long likesCount = getAllLikes(campaign.getTweetInfo());
                tvLikesCount.setText("Likes : " + likesCount);
                linearLayout.addView(tvLikesCount);

                TextView tvRetweetsCount = new TextView(campaign_detail.this);
                long retweetsCount = getAllRetweets(campaign.getTweetInfo());
                tvRetweetsCount.setText("Retweets : " + retweetsCount);
                linearLayout.addView(tvRetweetsCount);

                // Number of tweets for last timestamp
                ArrayList<TweetCount> tweetCountsArrayForDisplay = campaign.getTweetCounts();
                TweetCount lastTweetCount = tweetCountsArrayForDisplay.get(tweetCountsArrayForDisplay.size() - 1);
                TextView tvTitleLastTweetCount = new TextView(campaign_detail.this);
                tvTitleLastTweetCount.setText("Dernières informations de la campagne");
                tvTitleLastTweetCount.setGravity(Gravity.CENTER);
                linearLayout.addView(tvTitleLastTweetCount);

                TextView tvTweetCountDate = new TextView(campaign_detail.this);
                tvTweetCountDate.setText( "Date : " + lastTweetCount.getDate());
                linearLayout.addView(tvTweetCountDate);

                TextView tvTweetCountLikes = new TextView(campaign_detail.this);
                tvTweetCountLikes.setText( "Nombre de tweet : " + lastTweetCount.getCount());
                linearLayout.addView(tvTweetCountLikes);

                // Number of retweets and like for last timestamp
                ArrayList<TweetInfo> tweetInfoArrayForDisplay = campaign.getTweetInfo();
                TweetInfo lastTweetInfo = tweetInfoArrayForDisplay.get(tweetInfoArrayForDisplay.size() - 1);
                TextView tvTitleLastTweetInfo = new TextView(campaign_detail.this);
                tvTitleLastTweetInfo.setText("Dernières informations des tweets");
                tvTitleLastTweetInfo.setGravity(Gravity.CENTER);
                linearLayout.addView(tvTitleLastTweetInfo);

                TextView tvTweetInfoDate = new TextView(campaign_detail.this);
                tvTweetInfoDate.setText( "Date : " + lastTweetInfo.getDate());
                linearLayout.addView(tvTweetInfoDate);

                TextView tvTweetInfoRetweets = new TextView(campaign_detail.this);
                long tweetInfoRetweets = getAllRetweetsFromtweetInfo(lastTweetInfo);
                tvTweetInfoRetweets.setText( "Nombre de retweets : " + tweetInfoRetweets);
                linearLayout.addView(tvTweetInfoRetweets);

                TextView tvTweetInfoLikes = new TextView(campaign_detail.this);
                long tweetInfoLikes = getAllLikesFromtweetInfo(lastTweetInfo);
                tvTweetInfoLikes.setText( "Nombre de likes : " + tweetInfoLikes);
                linearLayout.addView(tvTweetInfoLikes);

                // Statistics on retweets and likes
                TweetInfo lastTweetInfo2 = tweetInfoArrayForDisplay.get(tweetInfoArrayForDisplay.size() - 2);
                TextView tvTitleStats = new TextView(campaign_detail.this);
                tvTitleStats.setText("Statistiques");
                tvTitleStats.setGravity(Gravity.CENTER);
                linearLayout.addView(tvTitleStats);

                float likesStats1 = getAllLikesFromtweetInfo(lastTweetInfo);
                float retweetsStats1 = getAllRetweetsFromtweetInfo(lastTweetInfo);

                float likesStats2 = getAllLikesFromtweetInfo(lastTweetInfo2);
                float retweetsStats2 = getAllRetweetsFromtweetInfo(lastTweetInfo2);

                float evolutionRetweets = 100 - (retweetsStats2 / retweetsStats1 * 100);
                float evolutionLikes = 100 - (likesStats2 / likesStats1 * 100);

                TextView tvEvolutionRetweets = new TextView(campaign_detail.this);
                tvEvolutionRetweets.setText("Evolution des retweets : " + evolutionRetweets + "%");
                linearLayout.addView(tvEvolutionRetweets);

                TextView tvEvolutionLikes = new TextView(campaign_detail.this);
                tvEvolutionLikes.setText("Evolution des likes : " + evolutionLikes + "%");
                linearLayout.addView(tvEvolutionLikes);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
