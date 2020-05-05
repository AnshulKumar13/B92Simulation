package qkd;

import java.util.Scanner;

public class SimulationEngine {
	private Simulation simulation;
	private boolean singleStep;
	private Scanner scan;
	
	public SimulationEngine(double avgPhotonNumber, double darkCount, int numPhotons) {
		simulation = new Simulation(numPhotons, false, avgPhotonNumber, darkCount);
		singleStep = false;
		scan = new Scanner(System.in);
	}
	
	public void run() {
		System.out.println("Welcome to B92 Simulator. Type 'Help' for Help.");
		//Scanner scan = new Scanner(System.in);
		String command = "";
		while(!command.equals("Quit")) {
			if(scan.hasNextLine()) {
				command = scan.nextLine();
				String[] splitCommand = command.split(" ");
				String input1 = splitCommand[0];
				String input2 = splitCommand.length > 1 ? splitCommand[1] : "";
				processInput(input1, input2);
			}
		}
		scan.close();
	}
	
	private void processInput(String command, String operand) {
		
		try {
			switch(command) {
				case "Help":
					printHelpMessage();
					break;
				case "Start":
					start();
					break;
				case "Settings":
					printSettings();
					break;
				case "DarkCount":
					simulation.setDarkCount(Double.parseDouble(operand));
					break;
				case "AvgPhotonNumber":
					simulation.setPhotonNumber(Double.parseDouble(operand));
					break;
				case "NumPhotons":
					simulation.setNumPhotons(Integer.parseInt(operand));
					break;
				case "Threshold":
					simulation.setThreshold(Double.parseDouble(operand));
					break;
				case "SamplePercent":
					simulation.setSamplePercent(Double.parseDouble(operand));
					break;
				case "Sampling":
					simulation.setSample(Boolean.parseBoolean(operand));
					break;
				case "Eve":
					simulation.setEve(Boolean.parseBoolean(operand));
					break;
				case "SingleStep":
					singleStep = Boolean.parseBoolean(operand);
					break;
			}
		}catch(Exception ex) {
			System.out.println("Unexpected input. Try again.");
			printHelpMessage();
			ex.printStackTrace();
		}
	}
	
	private void printHelpMessage() {
		System.out.println("\n\n");
		System.out.println("Help");
		String string = new String("Type 'Start' to start the simulation or type 'Settings' to see settings.\n"
				+ "To change a setting, type the name of the setting followed by a value.\n"
				+ "Type 'Quit' to quit.");
		System.out.println(string);
	}
	
	private void start() {
		if(singleStep) {
			simulation.stepSimulation(scan);
		}else {
			simulation.runSimulation();
		}
		simulation.reset();
	}
	
	private void printSettings() {
		System.out.println("\n\n");
		System.out.println("Settings");
		System.out.println("DarkCount: " + simulation.getDarkCount());
		System.out.println("AvgPhotonNumber: " + simulation.getPhotonNumber());
		System.out.println("NumPhotons: " + simulation.getNumPhotons());
		System.out.println("Threshold: " + simulation.getThreshold());
		System.out.println("SamplePercent: " + simulation.getSamplePercent());
		System.out.println("Sampling: " + simulation.sample());
		System.out.println("Eve: " + simulation.includeEve());
		System.out.println("SingleStep: " + singleStep);
		System.out.println();
	}

}
