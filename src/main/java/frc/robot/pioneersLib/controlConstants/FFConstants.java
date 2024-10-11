package frc.robot.pioneersLib.controlConstants;

public class FFConstants {

	public double kS;
	public double kV;
	public double kA;
	public double kG;

	public FFConstants(double kG, double ks, double kv, double ka) {
		this.kS = ks;
		this.kV = kv;
		this.kA = ka;
		this.kG = kG;
	}

	public FFConstants(double ks, double kv, double ka) {
		this(0, ks, kv, ka);
	}

	public FFConstants(double ks, double kv) {
		this(0, ks, kv, 0);
	}

	public FFConstants(double kv) {
		this(0, 0, kv, 0);
	}
}
