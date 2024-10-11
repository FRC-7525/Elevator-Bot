package frc.robot.pioneersLib.controlConstants;

public class FFConstants {

	public double kS;
	public double kV;
	public double kA;

	public FFConstants(double ks, double kv, double ka) {
		this.kS = ks;
		this.kV = kv;
		this.kA = ka;
	}

	public FFConstants(double ks, double kv) {
		this(ks, kv, 0);
	}

	public FFConstants(double kv) {
		this(0, kv, 0);
	}
}
