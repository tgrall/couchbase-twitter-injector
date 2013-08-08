Tweet Injector for Couchbase
==========================

1- Create your Twitter API Keys from https://dev.twitter.com/


2- Download the jar from [here](http://db.tt/pwIYZiF8) or build it using mvn clean package.


3- Create a new twitter4j.properties file with the following content

twitter4j.jsonStoreEnabled=true

oauth.consumerKey=[YOUR CONSUMER KEY]

oauth.consumerSecret=[YOUR CONSUMER SECRET KEY]

oauth.accessToken=[YOUR ACCESS TOKEN]

oauth.accessTokenSecret=[YOUR ACCESS TOKEN SECRET]

couchbase.uri.list=http://127.0.0.1:8091/pools

couchbase.bucket=social

couchbase.password=



4- Run the following command from the folder where twitter4j.properties is saved

java -jar CouchbaseTwitterInjector.jar

This will inject Tweets in the  couchbase.bucket bucket.


5- Kill the process to stop the injector

