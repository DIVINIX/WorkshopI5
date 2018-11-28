package com.dreamzite.dzanalytics;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dreamzite.dzanalytics.model.Campaign;
import com.dreamzite.dzanalytics.model.Tweet;
import com.dreamzite.dzanalytics.model.TweetCount;
import com.dreamzite.dzanalytics.model.TweetInfo;

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

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
