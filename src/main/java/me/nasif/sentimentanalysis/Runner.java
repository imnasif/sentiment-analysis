package me.nasif.sentimentanalysis;

import com.likethecolor.alchemy.api.Client;
import com.likethecolor.alchemy.api.call.AbstractCall;
import com.likethecolor.alchemy.api.call.SentimentCall;
import com.likethecolor.alchemy.api.call.type.CallTypeText;
import com.likethecolor.alchemy.api.entity.SentimentAlchemyEntity;
import java.io.IOException;

import twitter4j.JSONException;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.builder.ToStringStyle;

public class Runner {

    private static final String TWEETER_CONSUMER_KEY = "";
    private static final String TWEETER_CONSUMER_SECRET = "";
    private static final String TWEETER_ACCESS_TOKEN = "";
    private static final String TWEETER_ACCESS_TOKEN_SECRET = "";

    private static final String ALCHEMY_API_KEY = "";
    private static final Client alchemyCLlient = new Client();

    private static ConfigurationBuilder configurationBuilder;
    private static Twitter twitter;

    public static void main(String[] args) throws JSONException, IOException {

        initTweeter();
        initAlchemy();

        try {
            Query query = new Query("Nexium");
            //query.setCount(100);
            QueryResult result;
            result = twitter.search(query);
            List<twitter4j.Status> tweets = result.getTweets();
            System.err.println(tweets.size());
            tweets
                    .stream()
                    .filter((tweet) -> (tweet.getLang().matches("en")))
                    .forEach((tweet) -> {
                        System.out.println("Screen Name : @" + tweet.getUser().getScreenName() + "\nTweet : " + tweet.getText() + "\n");

                        final AbstractCall<SentimentAlchemyEntity> sentimentCall = new SentimentCall(new CallTypeText(tweet.getText()));
                        try {
                            System.out.println(alchemyCLlient.call(sentimentCall).toString(ToStringStyle.MULTI_LINE_STYLE));
                        } catch (IOException ex) {
                            Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    });
            System.exit(0);

        } catch (TwitterException te) {
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
    }

    private static void initTweeter() {
        configurationBuilder = new ConfigurationBuilder();

        configurationBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey(TWEETER_CONSUMER_KEY)
                .setOAuthConsumerSecret(TWEETER_CONSUMER_SECRET)
                .setOAuthAccessToken(TWEETER_ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(TWEETER_ACCESS_TOKEN_SECRET);

        twitter = new TwitterFactory(configurationBuilder.build()).getInstance();

    }

    private static void initAlchemy() throws IOException, JSONException {

        alchemyCLlient.setAPIKey(ALCHEMY_API_KEY);

    }
}
