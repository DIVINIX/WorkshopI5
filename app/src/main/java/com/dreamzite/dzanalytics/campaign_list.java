package com.dreamzite.dzanalytics;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dreamzite.dzanalytics.model.Campaigns;

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
import java.util.ArrayList;

public class campaign_list extends AppCompatActivity {

    ArrayList<Campaigns> campaigns;
    LinearLayout campaignsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_list);

        campaigns = new ArrayList<>();
        campaignsList = findViewById(R.id.campaings);
        new JSONTask().execute("http://api.metweecs.com/campaign/list");
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
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");

                JSONArray jArray = jsonObject.getJSONArray("results");
                for (int i=0; i < jArray.length(); i++)
                {
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        // Pulling items from the array
                        int id = oneObject.getInt("_id");
                        String name = oneObject.getString("name");

                        Campaigns campaign = new Campaigns(id, name);
                        campaigns.add(campaign);
                    } catch (JSONException e) {
                        // Oops
                    }
                }

                for (Campaigns c : campaigns)
                {
                    TextView campaignName = new TextView(campaign_list.this);
                    campaignName.setId(c.getId());
                    campaignName.setText(c.getName());
                    final Button goToCampaign = new Button(campaign_list.this);
                    goToCampaign.setId(c.getId());
                    goToCampaign.setText("Voir les informations");
                    campaignsList.addView(campaignName);
                    campaignsList.addView(goToCampaign);
                    final int id = goToCampaign.getId();
                    goToCampaign.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent campaingDetail = new Intent(getApplicationContext(),campaign_detail.class);
                            campaingDetail.putExtra("id", id);
                            startActivity(campaingDetail);
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
