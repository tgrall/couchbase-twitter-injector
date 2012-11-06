package com.couchbase.demo;

import com.couchbase.client.CouchbaseClient;
import org.json.JSONException;
import org.json.JSONObject;
import twitter4j.*;
import twitter4j.json.DataObjectFactory;


import java.io.InputStream;
import java.net.URI;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TwitterInjector {

    public final static String COUCHBASE_URIS = "couchbase.uri.list";
    public final static String COUCHBASE_BUCKET = "couchbase.bucket";
    public final static String COUCHBASE_PASSWORD = "couchbase.password";

    private List<URI> couchbaseServerUris = new ArrayList<URI>();
    private String couchbaseBucket = "default";
    private String couchbasePassword = "";


    public static void main(String[] args) {
        TwitterInjector twitterInjector = new TwitterInjector();
        twitterInjector.setUp();
        twitterInjector.injectTweets();
    }

    private void setUp() {
        try {
            Properties prop = new Properties();
            InputStream in = TwitterInjector.class.getClassLoader().getResourceAsStream("twitter4j.properties");
            if (in == null) {
                throw new Exception("File twitter4j.properties not found");
            }
            prop.load(in);
            in.close();

            if (prop.containsKey(COUCHBASE_URIS)) {
                String[] uriStrings =  prop.getProperty(COUCHBASE_URIS).split(",");
                for (int i=0; i<uriStrings.length; i++) {
                    couchbaseServerUris.add( new URI( uriStrings[i] ) );
                }

            } else {
                couchbaseServerUris.add( new URI("http://127.0.0.1:8091/pools") );
            }

            if (prop.containsKey(COUCHBASE_BUCKET)) {
                couchbaseBucket = prop.getProperty(COUCHBASE_BUCKET);
            }

            if (prop.containsKey(COUCHBASE_PASSWORD)) {
                couchbasePassword = prop.getProperty(COUCHBASE_PASSWORD);

            }

        } catch (Exception e) {
            System.out.println( e.getMessage() );
            System.exit(0);
        }


    }



    private void injectTweets() {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        try {
            final CouchbaseClient cbClient = new CouchbaseClient( couchbaseServerUris , couchbaseBucket , couchbasePassword );
            System.out.println("Send data to : "+  couchbaseServerUris +"/"+ couchbaseBucket );
            StatusListener listener = new StatusListener() {

                @Override
                public void onStatus(Status status) {
                    String twitterMessage = DataObjectFactory.getRawJSON(status);

                    // extract the id_str from the JSON document
                    // see : https://dev.twitter.com/docs/twitter-ids-json-and-snowflake
                    try {
                        JSONObject statusAsJson = new JSONObject(twitterMessage);
                        String idStr = statusAsJson.getString("id_str");
                        cbClient.add( idStr ,0, twitterMessage  );
                        System.out.print(".");
                    } catch (JSONException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

                @Override
                public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                }

                @Override
                public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                }

                @Override
                public void onScrubGeo(long userId, long upToStatusId) {
                }

                @Override
                public void onException(Exception ex) {
                    ex.printStackTrace();
                }
            };

        twitterStream.addListener(listener);
        twitterStream.sample();

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }



}

