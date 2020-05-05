package utils;
import java.util.Random;

public final class Photon {

	private Complex alpha; // |H> component
	private Complex beta; // |V> component

	Random rand = new Random();

	public Photon() {
		alpha = new Complex();
		beta = new Complex();
	}

	// This is for debugging purposes only!
	public String toString() {
		String str = new String();
		if (alpha.isReal()) {
			str = alpha.toString() + "|H> ";
		} else {
			str = "(" + alpha.toString() + ")|H> ";
		}
		if (beta.isReal()) {
			if (beta.getRe() >= 0) {
				str += "+ " + Double.toString(beta.getRe()) + "|V>";
			} else {
				str += "- " + Double.toString(-beta.getRe()) + "|V>";
			}
		} else {
			str += "+ (" + beta.toString() + ")|V>";
		}
		return str;
	}

	public static String toString(Photon q) {
		return q.toString();
	}

	public void prepareVacuum() {
		double energyPerMode = 0.5; // in units of hbar*omega
		double x0 = Math.sqrt(energyPerMode) * rand.nextGaussian()
				/ Math.sqrt(2);
		double y0 = Math.sqrt(energyPerMode) * rand.nextGaussian()
				/ Math.sqrt(2);
		double x1 = Math.sqrt(energyPerMode) * rand.nextGaussian()
				/ Math.sqrt(2);
		double y1 = Math.sqrt(energyPerMode) * rand.nextGaussian()
				/ Math.sqrt(2);
		alpha = new Complex(x0, y0);
		beta = new Complex(x1, y1);
	}

	public void prepare(Complex a, Complex b, double avgPhotonNumber) {
		if (avgPhotonNumber < 0) {
			throw new IllegalArgumentException();
		}
		Photon vac = new Photon();
		vac.prepareVacuum();
		alpha = a.mult(Math.sqrt(avgPhotonNumber)).add(vac.alpha);
		beta = b.mult(Math.sqrt(avgPhotonNumber)).add(vac.beta);
	}

	public void prepareH(double avgPhotonNumber) {
		Complex a = new Complex(1, 0);
		Complex b = new Complex(0, 0);
		this.prepare(a, b, avgPhotonNumber);
	}

	public void prepareV(double avgPhotonNumber) {
		Complex a = new Complex(0, 0);
		Complex b = new Complex(1, 0);
		this.prepare(a, b, avgPhotonNumber);
	}

	public void prepareD(double avgPhotonNumber) {
		Complex a = new Complex(1 / Math.sqrt(2), 0);
		Complex b = new Complex(1 / Math.sqrt(2), 0);
		this.prepare(a, b, avgPhotonNumber);
	}

	public void prepareA(double avgPhotonNumber) {
		Complex a = new Complex(1 / Math.sqrt(2), 0);
		Complex b = new Complex(-1 / Math.sqrt(2), 0);
		this.prepare(a, b, avgPhotonNumber);
	}

	public void prepareR(double avgPhotonNumber) {
		Complex a = new Complex(1 / Math.sqrt(2), 0);
		Complex b = new Complex(0, 1 / Math.sqrt(2));
		this.prepare(a, b, avgPhotonNumber);
	}

	public void prepareL(double avgPhotonNumber) {
		Complex a = new Complex(1 / Math.sqrt(2), 0);
		Complex b = new Complex(0, -1 / Math.sqrt(2));
		this.prepare(a, b, avgPhotonNumber);
	}

	public String measureHV(double probDarkCount) {
		if (probDarkCount < 0 | probDarkCount > 1) {
			throw new IllegalArgumentException();
		}
		double threshold = -0.5 * Math.log(1 - Math.sqrt(1 - probDarkCount));
		double intensityH = alpha.absSqr();
		double intensityV = beta.absSqr();
		// The photon is absorbed by the detector:
		this.prepareVacuum();
		// The outcome is determined by threshold exceedances:
		if (intensityH <= threshold & intensityV <= threshold) {
			return "N"; // no detection (invalid measurement)
		} else if (intensityH > threshold & intensityV <= threshold) {
			return "H"; // single H photon detected
		} else if (intensityH <= threshold & intensityV > threshold) {
			return "V"; // single V photon detected
		} else {
			return "M"; // multiple detections (invalid measurement)
		}
	}

	public String measureDA(double probDarkCount) {
		Complex a = this.alpha;
		Complex b = this.beta;
		alpha = a.add(b).div(Math.sqrt(2));
		beta = a.sub(b).div(Math.sqrt(2));
		String outcome = this.measureHV(probDarkCount);
		switch (outcome) {
			case "H" :
				return "D";
			case "V" :
				return "A";
			default :
				return outcome;
		}
	}

	public String measureRL(double probDarkCount) {
		Complex a = this.alpha;
		Complex b = this.beta;
		Complex I = new Complex(0, 1);
		alpha = a.sub(b.mult(I)).div(Math.sqrt(2));
		beta = a.add(b.mult(I)).div(Math.sqrt(2));
		String outcome = this.measureHV(probDarkCount);
		switch (outcome) {
			case "H" :
				return "R";
			case "V" :
				return "L";
			default :
				return outcome;
		}
	}

	public void applyPolarizer(double theta, double phi) {
		// Apply a polarizing filter according to the input parameters.
		// theta=0, phi=0: H polarizer
		// theta=pi/2, phi=0: V polarizer
		// theta=pi/4, phi=0: D polarizer
		// theta=pi/4, phi=pi: A polarizer
		// theta=pi/4, phi=+pi/2: R polarizer
		// theta=pi/4, phi=-pi/2: L polarizer
		Complex z = new Complex(Math.cos(phi), Math.sin(phi));
		Complex a = this.alpha;
		Complex b = this.beta;
		alpha = a.mult((1 + Math.cos(2 * theta)) / 2)
				.add(b.mult(Math.sin(2 * theta) / 2).mult(z.conj()));
		beta = a.mult(Math.sin(2 * theta) / 2).mult(z)
				.add(b.mult((1 - Math.cos(2 * theta)) / 2));
		// Now add an extra vacuum component.
		Photon vac = new Photon();
		vac.prepareVacuum();
		a = vac.alpha;
		b = vac.beta;
		alpha = (this.alpha).add(a.mult((1 - Math.cos(2 * theta)) / 2)
				.add(b.mult(-Math.sin(2 * theta) / 2).mult(z.conj())));
		beta = (this.beta).add(a.mult(-Math.sin(2 * theta) / 2).mult(z)
				.add(b.mult((1 + Math.cos(2 * theta)) / 2)));
	}

	public void applyUnitaryGate(double theta, double phi, double lamb) {
		// Applies a unitary matrix of the form
		// | cos(theta/2) -sin(theta/2) |
		// | exp(i*phi)*sin(theta/2) exp(i*(lamb+phi))*cos(theta/2) |
		Complex[][] U = new Complex[2][2];
		Complex z1 = new Complex(Math.cos(phi), Math.sin(phi));
		Complex z2 = new Complex(-Math.cos(lamb), -Math.sin(lamb));
		Complex z3 = new Complex(Math.cos(lamb + phi), Math.sin(lamb + phi));
		U[0][0] = new Complex(Math.cos(theta / 2), 0);
		U[1][0] = new Complex(Math.sin(theta / 2), 0).mult(z1);
		U[0][1] = new Complex(Math.sin(theta / 2), 0).mult(z2);
		U[1][1] = new Complex(Math.cos(theta / 2), 0).mult(z3);
		Complex a = this.alpha;
		Complex b = this.beta;
		alpha = U[0][0].mult(a).add(U[0][1].mult(b));
		beta = U[1][0].mult(a).add(U[1][1].mult(b));
	}

	public void applyXGate() {
		// Applies the Pauli X gate
		this.applyUnitaryGate(Math.PI, 0, Math.PI);
	}

	public void applyYGate() {
		// Applies the Pauli Y gate
		this.applyUnitaryGate(Math.PI, Math.PI / 2, Math.PI / 2);
	}

	public void applyZGate() {
		// Applied the Pauli Z gate
		this.applyUnitaryGate(0, Math.PI, 0);
	}

	public void applyHGate() {
		// Applied the Hadamard (half-wavelength) gate
		this.applyUnitaryGate(Math.PI / 2, 0, Math.PI);
	}

	public void applyQGate() {
		// Applies the SH (quarter-wavelength) gate
		this.applyUnitaryGate(Math.PI / 2, Math.PI / 2, Math.PI);
	}

	public void applyNoisyGate(double p) {
		// This operation acts as a depolarizing channel.
		// p = 0 leaves the photon unchanged.
		// p = 1 yields a completely random photon.
		// 0 < p < 1 yields a partially random photon.
		if (p < 0 | p > 1) {
			throw new IllegalArgumentException();
		}
		double theta = Math.acos(1 - 2 * rand.nextDouble() * p);
		double phi = p * (2 * rand.nextDouble() - 1) * Math.PI;
		double lamb = p * (2 * rand.nextDouble() - 1) * Math.PI;
		this.applyUnitaryGate(theta, phi, lamb);
	}

	public void applyAttenuation(double r) {
		// This operation acts as a partially reflecting beam splitter.
		// r = 0 leaves the photon unchanged.
		// r = 1 completely absorbs the photon, leaving a vacuum state.
		// 0 < r < 1 partially attenuates the photon and adds some vacuum.
		// r is the reflectivity.
		if (r < 0 | r > 1) {
			throw new IllegalArgumentException();
		}
		double t = Math.sqrt(1 - r * r); // t is the transmissivity.
		Photon vac = new Photon();
		vac.prepareVacuum(); // vacuum state entering the other input port
		alpha = (this.alpha).mult(t).add(vac.alpha.mult(r));
		beta = (this.beta).mult(t).add(vac.beta.mult(r));
	}

}
