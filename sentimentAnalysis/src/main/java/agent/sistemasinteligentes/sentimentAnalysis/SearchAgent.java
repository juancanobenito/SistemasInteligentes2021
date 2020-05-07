package agent.sistemasinteligentes.sentimentAnalysis;

import agent.launcher.AgentBase;
import agent.launcher.AgentModel;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class SearchAgent extends AgentBase{
	private static final long serialVersionUID = 1L;
	public static final String NICKNAME = "Search";

	static String hashTag;
	static long sinceId = 0;
	static int numberOfTweets = 0;

	protected void setup(){
		super.setup();
		this.type = AgentModel.SEARCH;
		addBehaviour(new Search());
		registerAgentDF();
	}

	private class Search extends CyclicBehaviour{

		@Override
		public void action(){
			ACLMessage input = receive();
			if(input!=null) {
				String [] arguments = input.getContent().split("_");
				hashTag = arguments[0];
				numberOfTweets = Integer.parseInt(arguments[1]);
				String send = TweetRecopiler(hashTag, numberOfTweets).toString();
				ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
				message.setSender(getAID());
				AID id = new AID("Analyzer@192.168.56.1:1200/JADE", AID.ISGUID);
				message.addReceiver(id);
				message.setContent(send);
				send(message);
			}
			block();
		}

	}

	/**
	 * Instancia la consulta y lanza la query
	 * @param hashtag
	 * @param numOfTweets
	 * @return
	 */
	public JsonObject TweetRecopiler(String hashtag, int numOfTweets){
		ConfigurationBuilder cb = new ConfigurationBuilder();

		cb.setDebugEnabled(true)
		.setOAuthConsumerKey(CONSUMER_KEY)
		.setOAuthConsumerSecret(CONSUMER_SECRET)
		.setOAuthAccessToken(ACCESS_TOKEN)
		.setOAuthAccessTokenSecret(ACESS_TOKEN_SECRET);

		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();

		Query queryMax = new Query(hashTag);
		JsonObject output = getTweets(queryMax, twitter, numOfTweets);
		return output;
	}

	/**
	 * Recoge los tweets en base a la información dada
	 * @param query
	 * @param twitter
	 * @param numberOfTweets
	 * @return
	 */
	private JsonObject getTweets(Query query, Twitter twitter, int numberOfTweets) {
		int forCount = 0;
		JsonObject output = new JsonObject();
		JsonArray arrayOfTweets = new JsonArray();
		try {
			QueryResult result = twitter.search(query);
			System.out.println("***********************************************");
			for (forCount=0; forCount < numberOfTweets || result.getTweets().get(forCount) == null; forCount++) {
				JsonObject tweets = new JsonObject();
				Status status = result.getTweets().get(forCount);
				tweets.addProperty("Id", status.getId());
				tweets.addProperty("Nickname", status.getUser().getScreenName());
				tweets.addProperty("Name", status.getUser().getName());
				tweets.addProperty("Text", status.getText());
				arrayOfTweets.add(tweets);
			}
		}catch (TwitterException te) {
			System.out.println("Couldn't connect: " + te);
			System.exit(-1);
		}catch (Exception e) {
			System.out.println("Something went wrong: " + e);
			System.exit(-1);
		}
		output.add("Tweets", arrayOfTweets);
		return output;
	}   
}
