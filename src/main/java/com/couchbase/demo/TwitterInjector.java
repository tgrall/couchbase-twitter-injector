package com.couchbase.demo;

import com.couchbase.client.CouchbaseClient;
import net.spy.memcached.internal.OperationFuture;
import net.spy.memcached.internal.CheckedOperationTimeoutException;
import net.spy.memcached.ops.OperationStatus;
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
    public final static String COUCHBASE_TWITTER_TRACK = "couchbase.twitter.track";

    private List<URI> couchbaseServerUris = new ArrayList<URI>();
    private String couchbaseBucket = "default";
    private String couchbasePassword = "";
    private String[] track = null;


    public static void main(String[] args) {
        TwitterInjector twitterInjector = new TwitterInjector();
        twitterInjector.setUp(args);
        twitterInjector.injectTweets();
    }

    private void setUp(String[] args) {
        try {


            Properties prop = new Properties();
            InputStream in = TwitterInjector.class.getClassLoader().getResourceAsStream("twitter4j.properties");
            if (in == null) {
                throw new Exception("File twitter4j.properties not found");
            }
            prop.load(in);
            in.close();


            if (args.length > 0) {
                track = args[0].split(",");
            } else {

                if (track == null & prop.contains(COUCHBASE_TWITTER_TRACK) && !prop.getProperty(COUCHBASE_TWITTER_TRACK).isEmpty()  ) {
                    track = prop.getProperty(COUCHBASE_TWITTER_TRACK).split(",");;
                } else {
                    System.out.println("No track : get all feeds...");
                }

            }


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
                        cbClient.add(idStr, 0, twitterMessage);
                        System.out.print(".");
                    } catch (Exception e) {
                        e.printStackTrace();
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
            if (track == null) {
                twitterStream.sample();
            } else {
                long[] follow = {};
                FilterQuery filter = new FilterQuery(0, follow, track );
                twitterStream.filter(filter);
            }


        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }



}

