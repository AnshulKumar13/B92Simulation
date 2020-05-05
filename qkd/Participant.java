package qkd;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import utils.Photon;

public class Participant {
	private Random rand;

	private String name;
	private List<Photon> sentPhotons;
	private List<Integer> key;
	private int mCount;
	private int nCount;

	public Participant(String name) {
		mCount = 0;
		nCount = 0;
		rand = new Random();
		sentPhotons = new ArrayList<Photon>();
		key = new ArrayList<Integer>();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getMCount() {
		return mCount;
	}

	public int getNCount() {
		return nCount;
	}
	
	public void addToKey(int val) {
		key.add(val);
	}
	
	public void setKeyBit(int index, int val) {
		key.set(index, val);
	}
	
	public List<Integer> getKey(){
		return key;
	}
	
	public String getKeyString() {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		for(int val : key) {
			if(val < 0) {
				builder.append("- ");
			}else {
				builder.append(val + " ");
			}
		}
		builder.setCharAt(builder.length() - 1, ']');
		return builder.toString();
	}
	

	/*
	 * Prepares a photon in the given basis
	 * 0 maps to H, and 1 maps to D
	 */
	Photon preparePhoton(int basis, double avgPhotonNumber) {
		Photon photon = new Photon();

		if (basis == 0) {
			photon.prepareH(avgPhotonNumber);
		} else {
			photon.prepareD(avgPhotonNumber);
		}

		return photon;
	}

	/*
	 * Prepares a photon in a randomly selected basis.
	 */
	Photon preparePhoton(double avgPhotonNumber) {
		return rand.nextBoolean() ? preparePhoton(0, avgPhotonNumber) : preparePhoton(1, avgPhotonNumber);
	}

	/*
	 * Measures the photon in the given basis. Returns either a 1, 0, or < 0 for
	 * inconclusive measurement. 0 corresponds to the HV basis, and 1
	 * corresponds to the DA basis.
	 */
	int measurePhoton(Photon photon, int basis, double darkCount) {
		String measurement;

		if (basis == 0) {
			measurement = photon.measureHV(darkCount);
		} else {
			measurement = photon.measureDA(darkCount);
		}

		if (measurement.equals("V")) {
			return 1;
		}else if (measurement.equals("A")) {
			return 0;
		}else if (measurement.equals("M")) {
			mCount++;
			return -1;
		}else if (measurement.equals("N")) {
			nCount++;
			return -1;
		}else if(measurement.equals("H")) {
			return -2;
		}else {
			return -3;
		}
	}

	/*
	 * Measures given photon in randomly chosen basis Returns either 1, 0, or < 0
	 * if inconclusive
	 */
	int measurePhoton(Photon photon, double darkCount) {
		return rand.nextBoolean() ? measurePhoton(photon, 0, darkCount) : measurePhoton(photon, 1, darkCount);
	}
	
	@Override
	public String toString() {
		return name + "\n" + getKeyString();
	}
}
