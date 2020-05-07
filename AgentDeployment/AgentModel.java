package agent.launcher;

public enum AgentModel {

	AGENT1("Agent1"),
	AGENT2("Agent2"),
	AGENT3("Agent3"),
	DESCONOCIDO("Desconocido");

    private final String value;

    AgentModel(String value){ 
    	this.value = value; 
    	}

    public String getValue(){ 
    	return this.value; 
    }

    public static AgentModel getEnum(String value) {
        switch (value) {
            case "Agent1":
                return AGENT1;
            case "Agent2":
                return AGENT2;
            case "Agent3":
                return AGENT3;
            default:
                return DESCONOCIDO;
        }
    }
	
}
