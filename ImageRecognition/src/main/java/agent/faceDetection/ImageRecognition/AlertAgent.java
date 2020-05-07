package agent.faceDetection.ImageRecognition;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import agent.launcher.AgentBase;
import agent.launcher.AgentModel;
import jade.core.behaviours.OneShotBehaviour;

public class AlertAgent extends AgentBase {

	private static final long serialVersionUID = 1L;
	
	private TelegramBot bot = new TelegramBot(TELEGRAM_ID);

	public static final String NICKNAME = "Alert";
	
	protected void setup(){
		super.setup();
		this.type = AgentModel.ALERT;
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		addBehaviour(new Alert());
		registerAgentDF();
	}

	private class Alert extends OneShotBehaviour{
	
		//Envía una alerta al bot
		@Override
		public void action() {
			System.out.println("AlertAgent deploy!");
			long chatId = 4551748;
			SendMessage request = new SendMessage(chatId, "Face detected!")
			        .parseMode(ParseMode.HTML)
			        .disableWebPagePreview(true)
			        .disableNotification(true)
			        .replyToMessageId(1)
			        .replyMarkup(new ForceReply());
			SendResponse sendResponse = bot.execute(request);
			sendResponse.isOk();
			sendResponse.message();
			myAgent.doDelete();	
		}
	}

}
