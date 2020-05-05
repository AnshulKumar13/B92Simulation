package qkd;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

import utils.Photon;

public class Simulation {
	private Queue<Photon> photonBuffer;

	private int numPhotons;
	private double samplePercent;
	private double threshold;
	private double avgPhotonNumber;
	private double darkCount;
	private boolean includeEve;
	private boolean sample;
	private Participant alice;
	private Participant bob;
	private Participant eve;
	private Random rand;

	public Simulation(int numPhotons, boolean includeEve,
			double avgPhotonNumber, double darkCount) {
		rand = new Random();
		this.numPhotons = numPhotons;
		samplePercent = 0.1;
		threshold = 0.3;
		sample = false;
		setEve(includeEve);
		setPhotonNumber(avgPhotonNumber);
		setDarkCount(darkCount);
		reset();
	}

	public void reset() {
		alice = new Sender("Alice");
		bob = new Participant("Bob");
		eve = new Evesdropper("Eve");
		photonBuffer = new LinkedList<Photon>();
	}

	public double getDarkCount() {
		return darkCount;
	}

	public double getPhotonNumber() {
		return avgPhotonNumber;
	}
	
	public double getSamplePercent() {
		return samplePercent;
	}

	public int getNumPhotons() {
		return numPhotons;
	}
	
	public boolean sample() {
		return sample;
	}

	public boolean includeEve() {
		return includeEve;
	}
	
	public double getThreshold() {
		return threshold;
	}

	public void setEve(boolean includeEve) {
		this.includeEve = includeEve;
	}

	public void setPhotonNumber(double avgPhotonNumber) {
		this.avgPhotonNumber = avgPhotonNumber;
	}

	public void setNumPhotons(int numPhotons) {
		this.numPhotons = numPhotons;
	}

	public void setDarkCount(double darkCount) {
		this.darkCount = darkCount;
	}
	
	public void setSample(boolean sample) {
		this.sample = sample;
	}
	
	public void setSamplePercent(double samplePercent) {
		this.samplePercent = samplePercent;
	}
	
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	private double calculateError() {
		String aliceKey = alice.getKeyString();
		String bobKey = bob.getKeyString();
		int total = 0;
		int incorrect = 0;
		
		for(int i = 1; i < bobKey.length() - 1; i++) {
			if(bobKey.charAt(i) == '-' || bobKey.charAt(i) == ' ') {
				continue;
			}
			if(bobKey.charAt(i) != aliceKey.charAt(i)) {
				incorrect++;
			}
			
			total++;
		}
		
		return (double)incorrect/total;
	}
	
	private void siftKeys() {
		List<Integer> keyBob = bob.getKey();
		List<Integer> keyAlice = alice.getKey();
		List<Integer> keyEve = eve.getKey();
		
		for(int i = 0; i < keyBob.size(); i++) {
			if(keyBob.get(i) < 0) {
				((Sender)alice).addToSiftedKey(-1);
				if(includeEve) {
					keyEve.set(i, -1);
				}	
				
			}else {
				((Sender)alice).addToSiftedKey(keyAlice.get(i));
			}
		}
	}
	
	private double sampleError() {
		List<Integer> keyBob = bob.getKey();
		List<Integer> keyAlice = ((Sender)alice).getSiftedKey();
		
		int keySize = 0;
		
		for(int i = 0; i < keyBob.size(); i++) {
			if(keyBob.get(i) >=0) {
				keySize++;
			}
		}
		
		int sampleSize = (int)(keySize * samplePercent);
		int numSampled = 0;
		int numIncorrect = 0;
		List<Integer> sampleInd = new ArrayList<Integer>();
		Random rand = new Random();
		
		for(int i = 0; i < sampleSize; i++) {
			int ind = rand.nextInt(keyBob.size());
			while(keyBob.get(ind) < 0 || sampleInd.contains(ind)) {
				ind = rand.nextInt(keyBob.size());
			}
			sampleInd.add(ind);
		}
		
		for(int i = 0; i < keyBob.size(); i++) {
			if(!sampleInd.contains(i)) {
				continue;
			}
			
			if(keyBob.get(i) != keyAlice.get(i)) {
				numIncorrect++;
			}
			
			numSampled++;
			keyBob.set(i, -1);
			keyAlice.set(i, -1);
		}
		
		return (double)numIncorrect / numSampled;
	}
	
	private boolean checkForEve(double sampleError) {
		if(!sample) {
			return false; //means sampling is off
		}
		
		if(sampleError > threshold)
			return true;
		
		return false;
	}
	
	private void endSimulation() {
		siftKeys();
		double actualError = calculateError();
		double sampleError = sample ? sampleError() : -1;
		
		System.out.println(alice);

		if (includeEve)
			System.out.println(eve);

		System.out.println(bob);
		System.out.println("Actual QBER: " + actualError);
		System.out.println("Sampled QBER: " + sampleError);
		
		if(checkForEve(sampleError)) {
			System.out.println("CHANEL NOT SECURE");
		}
	}

	/*
	 * Advance one round in the simulation (ie) Alice prepares a single photon
	 * in given basis and sends it to Bob If Eve is enabled, she will intercept
	 * it.
	 */
	private void step(int basisAlice, int basisBob, boolean outputData) {
		Photon alicePhoton = alice.preparePhoton(basisAlice, avgPhotonNumber);
		photonBuffer.add(alicePhoton);
		alice.addToKey(basisAlice);

		if (includeEve) {
			Photon evePhoton = ((Evesdropper) eve).interceptPhoton(
					photonBuffer.remove(), avgPhotonNumber, darkCount);
			photonBuffer.add(evePhoton);
		}

		Photon bobPhoton = photonBuffer.remove();
		int bobMeasurement = bob.measurePhoton(bobPhoton, basisBob, darkCount);
		bob.addToKey(bobMeasurement);

		if (!outputData) {
			return;
		}

		String alicePolarization = basisAlice == 0 ? "|H>" : "|D>";
		String bobPolarization = basisBob == 0 ? "HV" : "DA";
		String bobDetected = "";
		String keyDetected = "NA";

		switch (bobMeasurement) {
			case 1 :
				bobDetected = "|V>";
				keyDetected = "1";
				break;
			case 0 :
				bobDetected = "|A>";
				keyDetected = "0";
				break;
			case -1 :
				bobDetected = "NA";
				break;
			case -2 :
				bobDetected = "|H>";
				break;
			case -3 :
				bobDetected = "|D>";
				break;
		}

		System.out.println("Alice: " + basisAlice + "->" + alicePolarization
				+ " Bob: " + bobPolarization + "->" + bobDetected + "->"
				+ keyDetected);

	}

	/*
	 * Steps through the entire simulation at the given time interval in 100s of
	 * MS
	 */
	public void runSimulation() {
		for (int i = 0; i < numPhotons; i++) {
			int basisAlice = rand.nextInt(2);
			int basisBob = rand.nextInt(2);
			step(basisAlice, basisBob, false);
		}
		endSimulation();
	}

	/*
	 * Steps through simulation as user presses ENTER
	 */
	public void stepSimulation(Scanner scan) {
		//Scanner scan = new Scanner(System.in);
		for (int i = 0; i < numPhotons; i++) {
			int basisAlice = rand.nextInt(2);
			int basisBob = rand.nextInt(2);
			step(basisAlice, basisBob, true);
			scan.nextLine();
		}
		//scan.close();
		endSimulation();
	}
}
