package frc.robot;

import edu.wpi.first.math.trajectory.TrapezoidProfile.State;
import frc.robot.pioneersLib.controlConstants.FFConstants;
import frc.robot.pioneersLib.controlConstants.PIDConstants;

public final class Constants {

    public static enum RobotModes {
        REAL,
        SIM,
        REPLAY
    }

    public static final RobotModes ROBOT_STATE = RobotModes.REAL;

    public static final class Elevator {
        public static final String SUBSYTEM_NAME = "Elevator";

        // Configs for control loop & stuff
        public static final double DISTANCE_OUT_METERS = 0.75;
        public static final double DISTANCE_MEDIUM_METERS = 0.5;
        public static final double DISTANCE_IN_METERS = 0.0;

        public static final double DISTANCE_TOLERANCE_METERS = 0.05;
        public static final double VELOCITY_TOLERANCE_MS = 0.05;

        public static final double DISTANCE_METERS_PER_ROTATION = 0.2;

        public static final double ELEVATOR_MAX_VELOCITY_MPS = 0.2;
        public static final double ELEVATOR_MAX_ACCEL_MPSSQ = 0.1;

        // Set-States
        public static final State ELEVATOR_OUT = new State(1.5, 0);
        public static final State ELEVATOR_IN = new State(0, 0);

        // TODO: Confirm these are acceptable
        public static final double ZEROING_CURRENT_LIMIT_AMPS = 10.0;
        public static final int SMART_CURRENT_LIMIT_AMPS = 30;

        // TODO: SYSID/Tune
        // Note, kA can prolly stay at 0, use https://www.reca.lc/ to calculate others
        public static final FFConstants ELEVATOR_FF = new FFConstants(1, 1, 1, 0);
        public static final PIDConstants ELEVATOR_PID = new PIDConstants(0.0, 0.0, 0.0, 0.0);
    }
}
