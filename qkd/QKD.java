package qkd;

public class QKD {
	//default values
	public static double avgPhotonValue = 1;
	public static double darkCount = 0.3;
	public static int numPhotons = 5;
	
	public static void main(String[] args0) {
		SimulationEngine engine = new SimulationEngine(avgPhotonValue, darkCount, numPhotons);
		engine.run();
	}
}
