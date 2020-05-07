package agent.launcher;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import java.io.IOException;

import agent.sistemasinteligentes.sentimentAnalysis.AnalyzerAgent;
import agent.sistemasinteligentes.sentimentAnalysis.FilterAgent;
import agent.sistemasinteligentes.sentimentAnalysis.SearchAgent;

public class Main {

	private static jade.wrapper.AgentContainer cc;
	
    private static void loadBoot(){

        jade.core.Runtime rt = jade.core.Runtime.instance();

        rt.setCloseVM(true);
        System.out.println("Runtime created");

        Profile profile = new ProfileImpl(null, 1200, null);
        System.out.println("Profile created");

        System.out.println("Launching a whole in-process platform..."+profile);
        cc = rt.createMainContainer(profile);

        try {
            ProfileImpl pContainer = new ProfileImpl(null, 1200, null);
            rt.createAgentContainer(pContainer);
            System.out.println("Containers created");
            System.out.println("Launching the rma agent on the main container ...");
            cc.createNewAgent("rma","jade.tools.rma.rma", new Object[0]).start();
            cc.createNewAgent(FilterAgent.NICKNAME, FilterAgent.class.getName(), new Object[]{"0"}).start();
            cc.createNewAgent(SearchAgent.NICKNAME, SearchAgent.class.getName(), new Object[]{"0"}).start();
            cc.createNewAgent(AnalyzerAgent.NICKNAME, AnalyzerAgent.class.getName(), new Object[]{"0"}).start();
        } catch (StaleProxyException e) {
            System.err.println("Error during boot!!!");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static void main(String[] args) throws IOException {

        System.out.println("Starting...");

        loadBoot();

        System.out.println("MAS loaded...");
    }
}
