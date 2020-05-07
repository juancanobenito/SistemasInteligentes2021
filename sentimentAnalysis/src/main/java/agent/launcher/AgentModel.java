package agent.launcher;

public enum AgentModel {

	FILTER("Filter"),
	SEARCH("SearchEngine"),
	ANALYZER("Analyzer"),
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
            case "Filter":
                return FILTER;
            case "SearchEngine":
                return SEARCH;
            case "Analyzer":
                return ANALYZER;
            default:
                return DESCONOCIDO;
        }
    }
	
}
