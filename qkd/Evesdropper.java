package qkd;

import java.util.Random;

import utils.Photon;

public class Evesdropper extends Participant {
	private Random rand;

	public Evesdropper(String name) {
		super(name);
		rand = new Random();
	}

	public Photon interceptPhoton(Photon photon, int basis, double avgPhotonNumber, double darkCount) {
		int measurement = measurePhoton(photon, basis, darkCount);

		addToKey(measurement);

		if (measurement == -1) {
			return preparePhoton(avgPhotonNumber);
		} else {
			return preparePhoton(measurement, avgPhotonNumber);
		}
	}

	public Photon interceptPhoton(Photon photon, double avgPhotonNumber, double darkCount) {
		return rand.nextBoolean()
				? interceptPhoton(photon, 0, avgPhotonNumber, darkCount)
				: interceptPhoton(photon, 1, avgPhotonNumber, darkCount);
	}

}
