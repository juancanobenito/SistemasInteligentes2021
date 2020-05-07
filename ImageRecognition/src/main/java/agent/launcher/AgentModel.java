package agent.launcher;

public enum AgentModel {

	CAMERA("Camera"),
	WATCHMEN("Watchmen"),
	ALERT("Alert"),
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
		case "Camera":
			return CAMERA;
		case "Watchmen":
			return WATCHMEN;
		case "Alert":
			return ALERT;
		default:
			return DESCONOCIDO;
		}
	}


}
