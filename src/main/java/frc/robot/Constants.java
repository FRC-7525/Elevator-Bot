package frc.robot;

import com.revrobotics.CANSparkBase.IdleMode;

import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.trajectory.TrapezoidProfile.State;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.pioneersLib.controlConstants.FFConstants;
import frc.robot.pioneersLib.controlConstants.PIDConstants;

public final class Constants {

    public static enum RobotModes {
        REAL,
        SIM,
        REPLAY
    }

    public static final RobotModes ROBOT_STATE = RobotModes.REAL;
    
    public static final XboxController DRIVER_CONTROLLER = new XboxController(0);

    public static final class Manager {
        // Should hold toggles for different triggers
    }

    public static final class Elevator {
        // Subsystem Constants
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

        // Sim/Physical values
        public static final DCMotor GEARBOX = DCMotor.getNEO(2);
        public static final double GEARING = 20;
        public static final double CARRIAGE_MASS_KG = 2;
        public static final double DRUM_RADIUS_METERS = 0.5;
        public static final double MIN_HEIGH_METERS = 0;
        public static final double MAX_HEIGHT_METERS = 2;
        public static final double STARTING_HEIGHT_METERS = 0;
        public static final boolean SIMULATE_GRAVITY = false;

        // Set-States
        public static final State ELEVATOR_OUT = new State(1.5, 0);
        public static final State ELEVATOR_IN = new State(0, 0);

        // TODO: Confirm these are acceptable
        public static final double ZEROING_CURRENT_LIMIT_AMPS = 10.0;
        public static final int SMART_CURRENT_LIMIT_AMPS = 30;

        // CAN IDs
        public static final int LEFT_CAN_ID = 1;
        public static final int RIGHT_CAN_ID = 2;

        // Inversions
        public static final boolean LEFT_INVERTED = true;
        public static final boolean RIGHT_INVERTED = !LEFT_INVERTED;

        // Idle Mode
        public static final IdleMode IDLE_MODE = IdleMode.kBrake;

        // TODO: SYSID/Tune
        // Note, kA can prolly stay at 0, use https://www.reca.lc/ to calculate others or just SYSID
        public static final FFConstants ELEVATOR_FF = new FFConstants(1, 1, 1, 0);
        public static final PIDConstants ELEVATOR_PID = new PIDConstants(0.0, 0.0, 0.0, 0.0);
    }
}
