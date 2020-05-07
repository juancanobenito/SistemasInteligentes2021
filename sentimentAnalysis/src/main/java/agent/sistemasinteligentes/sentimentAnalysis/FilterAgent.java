package agent.sistemasinteligentes.sentimentAnalysis;

import java.util.Scanner;

import agent.launcher.AgentBase;
import agent.launcher.AgentModel;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;


public class FilterAgent extends AgentBase{
	private static final long serialVersionUID = 1L;

	public static final String NICKNAME = "Filter";

	protected void setup(){
		super.setup();
		this.type = AgentModel.FILTER;
		addBehaviour(new Filter());
		registerAgentDF();
	}

	private class Filter extends CyclicBehaviour{

		Scanner myObj = new Scanner(System.in);

		//Solicita palabra y número de tweets
		@Override
		public void action() {		
			String [] arguments = new String[2];
			System.out.println("Enter word, username or hashtag: ");
			arguments[0] = myObj.nextLine();
			//Rate limiting is set to 15 every 15 minutes -> https://developer.twitter.com/en/docs/basics/rate-limiting
			System.out.println("Enter number of tweets (< 15): ");
			arguments[1] = myObj.nextLine();
			while(Integer.parseInt(arguments[1]) > 15) {
				System.out.println("Enter number of tweets (< 15): ");
				arguments[1] = myObj.nextLine();
			}
			System.out.println("Recopiling " + arguments[1].toString() + " tweets with " + arguments[0].toString() + " " );
			
			ACLMessage finish = new ACLMessage(ACLMessage.REQUEST);
			finish.setSender(getAID());
			AID id = new AID("Search@192.168.56.1:1200/JADE", AID.ISGUID);
			finish.addReceiver(id);
			String toSend = arguments[0].toString() + "_" +  arguments[1].toString();
			finish.setContent(toSend);
			send(finish);
			block();
		}
	}
}
