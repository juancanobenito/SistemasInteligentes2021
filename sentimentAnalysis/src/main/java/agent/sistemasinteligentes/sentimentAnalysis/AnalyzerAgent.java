package agent.sistemasinteligentes.sentimentAnalysis;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.cloud.language.v1.Document.Type;

import agent.launcher.AgentBase;
import agent.launcher.AgentModel;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class AnalyzerAgent extends AgentBase{
	private static final long serialVersionUID = 1L;
	public static final String NICKNAME = "Analyzer";

	protected void setup(){
		super.setup();
		this.type = AgentModel.ANALYZER;
		addBehaviour(new Analyzer());
		registerAgentDF();
	}

	private class Analyzer extends CyclicBehaviour{

		public void reset() {
			super.reset();
		}

		@Override
		public void action() {
			ACLMessage message = receive();
			if(message!=null) {
				JsonObject output = finalJSON(message.getContent());
				try {
					LocalTime l = LocalTime.now();
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					Files.writeString(Paths.get("Exports\\sentiments"+l.getHour()+l.getMinute()+l.getSecond()+".json"), gson.toJson(output));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			ACLMessage finish = new ACLMessage(ACLMessage.REQUEST);
			finish.setSender(getAID());
			AID id = new AID("Filter@192.168.56.1:1200/JADE", AID.ISGUID);
			finish.addReceiver(id);
			finish.setContent("Ready");
			send(finish);
			block();
		}
	}
	
	/**
	 * Llama al método getSentiment con el texto del tweet y añade los resultados al JSON
	 * @param json
	 * @return
	 */
	public JsonObject finalJSON(String json) {
		JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
		JsonArray tweetsArray = jsonObject.getAsJsonArray("Tweets");
		JsonObject output = new JsonObject();
		JsonArray arrayOfTweets = new JsonArray();
		for(JsonElement tweets : tweetsArray) {
			Sentiments result = new Sentiments();
			JsonObject tweetsText = tweets.getAsJsonObject();
			String getText;
			try {
				getText = new String(tweetsText.get("Text").getAsString().getBytes(),"UTF-8");
				result = getSentiment(getText);
				tweetsText.addProperty("score", result.getScore());
				tweetsText.addProperty("magnitude", result.getMagnitude());
				arrayOfTweets.add(tweetsText);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		output.add("Tweets", arrayOfTweets);
		return output;
	}

	/**
	 * Realiza el análisis de sentimientos
	 * @param tweets
	 * @return
	 */
	private Sentiments getSentiment(String tweets){
		Sentiments output = new Sentiments(0, 0);
		try (LanguageServiceClient language = LanguageServiceClient.create()) {
	      Document doc = Document.newBuilder().setContent(tweets).setType(Type.PLAIN_TEXT).build();
	      Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();
	      output.setScore(sentiment.getScore());
	      output.setMagnitude(sentiment.getMagnitude());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}
}
