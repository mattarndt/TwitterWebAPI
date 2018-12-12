import java.io.PrintWriter;
import java.util.Map;

import twitter4j.*;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

//import java.util.Map;

public class TweetSearch {

	//	Set this to your actual CONSUMER KEY and SECRET for your application as given to you by dev.twitter.com
	private static final String CONSUMER_KEY		= "007O7s4OE8orZIDFcGT3IlpcZ";
	private static final String CONSUMER_SECRET 	= "d6V4WPyp5xHH4B70uIHJaVcK3vqWgUlWnwEJSXRb0GfuXujD4H";
	
	private static PrintWriter file;
	
	//	How many tweets to retrieve in every call to Twitter. 100 is the maximum allowed in the API
	private static final int TWEETS_PER_QUERY		= 10;

	//	This controls how many queries, maximum, we will make of Twitter before cutting off the results.
	//	You will retrieve up to MAX_QUERIES*TWEETS_PER_QUERY tweets.
	//
	//  If you set MAX_QUERIES high enough (e.g., over 450), you will undoubtedly hit your rate limits
	//  and you an see the program sleep until the rate limits reset
	private static final int MAX_QUERIES			= 100;

	//	What we want to search for in this program.  Justin Bieber always returns as many results as you could
	//	ever want, so it's safe to assume we'll get multiple pages back...
	private static final String SEARCH_TERM			= "eagles"; //Change this to change what you want to search


	/**
	 * Replace newlines and tabs in text with escaped versions to making printing cleaner
	 *
	 * @param text	The text of a tweet, sometimes with embedded newlines and tabs
	 * @return		The text passed in, but with the newlines and tabs replaced
	 */
	public static String cleanText(String text)
	{
		text = text.replace("\n", "\\n");
		text = text.replace("\t", "\\t");

		return text;
	}


	/**
	 * Retrieve the "bearer" token from Twitter in order to make application-authenticated calls.
	 *
	 * This is the first step in doing application authentication, as described in Twitter's documentation at
	 * https://dev.twitter.com/docs/auth/application-only-auth
	 *
	 * Note that if there's an error in this process, we just print a message and quit.  That's a pretty
	 * dramatic side effect, and a better implementation would pass an error back up the line...
	 *
	 * @return	The oAuth2 bearer token
	 */
	public static OAuth2Token getOAuth2Token()
	{
		OAuth2Token token = null;
		ConfigurationBuilder cb;

		cb = new ConfigurationBuilder();
		cb.setApplicationOnlyAuthEnabled(true);

		cb.setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET);

		try
		{
			token = new TwitterFactory(cb.build()).getInstance().getOAuth2Token();
		}
		catch (Exception e)
		{
			System.out.println("Could not get OAuth2 token");
			e.printStackTrace();
			System.exit(0);
		}

		return token;
	}

	/**
	 * Get a fully application-authenticated Twitter object useful for making subsequent calls.
	 *
	 * @return	Twitter4J Twitter object that's ready for API calls
	 */
	public static Twitter getTwitter()
	{
		OAuth2Token token;

		//	First step, get a "bearer" token that can be used for our requests
		token = getOAuth2Token();

		//	Now, configure our new Twitter object to use application authentication and provide it with
		//	our CONSUMER key and secret and the bearer token we got back from Twitter
		ConfigurationBuilder cb = new ConfigurationBuilder();

		cb.setApplicationOnlyAuthEnabled(true);

		cb.setOAuthConsumerKey(CONSUMER_KEY);
		cb.setOAuthConsumerSecret(CONSUMER_SECRET);

		cb.setOAuth2TokenType(token.getTokenType());
		cb.setOAuth2AccessToken(token.getAccessToken());

		//	And create the Twitter object!
		return new TwitterFactory(cb.build()).getInstance();

	}
	public static void searchAndWriteFile(){
		int	totalTweets = 0;
		long maxID = -1;

		Twitter twitter = TweetSearch.getTwitter();
		int count = 0;
		try
		{
			
			Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus("search");
			RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");
			System.out.printf("You have %d calls remaining out of %d, Limit resets in %d seconds\n",
							  searchTweetsRateLimit.getRemaining(),
							  searchTweetsRateLimit.getLimit(),
							  searchTweetsRateLimit.getSecondsUntilReset());

			file = new PrintWriter("SearchResults.txt");
			
			for (int queryNumber=0;queryNumber < MAX_QUERIES; queryNumber++)
			{
				System.out.printf("\n\n!!! Starting loop %d\n\n", queryNumber);

				//	Do we need to delay because we've already hit our rate limits?
				if (searchTweetsRateLimit.getRemaining() == 0)
				{
					//	Yes we do, unfortunately ...
					System.out.printf("!!! Sleeping for %d seconds due to rate limits\n", searchTweetsRateLimit.getSecondsUntilReset());

					Thread.sleep((searchTweetsRateLimit.getSecondsUntilReset()+2) * 1000l);
				}
				
				Query q = new Query(SEARCH_TERM);			// Search for tweets that contains this term
				q.setCount(TWEETS_PER_QUERY);				// How many tweets, max, to retrieve
				q.setLang("en");							// English language tweets, please

				if (maxID != -1)
				{
					q.setMaxId(maxID - 1);
				}

				//	This actually does the search on Twitter and makes the call across the network
				QueryResult r = twitter.search(q);

				
				if (r.getTweets().size() == 0)
				{
					break;			// Nothing? We must be done
				}

				
				//	loop through all the tweets and process them.  In this sample program, we just print them
				//	out, but in a real application you might save them to a database, a CSV file, do some
				//	analysis on them, whatever...
				for (Status s: r.getTweets())				// Loop through all the tweets...
				{
					//	Increment our count of tweets retrieved
					totalTweets++;

					//	Keep track of the lowest tweet ID.  If you do not do this, you cannot retrieve multiple
					//	blocks of tweets...
					if (maxID == -1 || s.getId() < maxID)
					{
						maxID = s.getId();
					}
					count = count + 1;
					//	Do something with the tweet....
					System.out.printf(count + " At %s, @%-20s said:  %s\n",
									  s.getCreatedAt().toString(),
									  s.getUser().getScreenName(),
									  TweetSearch.cleanText(s.getText()));
					

					String tweets = TweetSearch.cleanText(s.getText());
					
					tweets = tweets.replace("\\n", " ");
					tweets = tweets.replaceAll("[\\p{Punct}&&[^#]]+", "");
					//tweets = count + " " + tweets;
					//System.out.println("NEW TWEETS = " + tweets);
					file.write(tweets);
					file.write("\n");
					
				}
				
				//	As part of what gets returned from Twitter when we make the search API call, we get an updated
				//	status on rate limits.  We save this now so at the top of the loop we can decide whether we need
				//	to sleep or not before making the next call.
				searchTweetsRateLimit = r.getRateLimitStatus();
			}
			
		}
		catch (Exception e)
		{
			//	Catch all -- you're going to read the stack trace and figure out what needs to be done to fix it
			System.out.println("That didn't work well...wonder why?");

			e.printStackTrace();

		}
		System.out.printf("\n\nA total of %d tweets retrieved\n", totalTweets);

	}

}
