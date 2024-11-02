package frc.robot;

import com.revrobotics.CANSparkBase.IdleMode;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.trajectory.TrapezoidProfile.State;
import edu.wpi.first.units.Current;
import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Mass;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Velocity;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.pioneersLib.controlConstants.FFConstants;
import frc.robot.pioneersLib.controlConstants.PIDConstants;
import frc.robot.pioneersLib.subsystem.SubsystemStateContainer;

import static edu.wpi.first.units.Units.*;


public final class Constants {

    public static enum RobotModes {
        REAL,
        SIM,
        REPLAY
    }

    public static final RobotModes ROBOT_STATE = RobotModes.SIM;

    public static final class Conversions {
        public static final double METERS_TO_FEET = 3.28084;
        public static final double METERS_TO_INCHES = 39.3701;
        public static final double METERS_TO_CM = 100;
        public static final double METERS_TO_MM = 1000;
        public static final double METERS_TO_MILES = 0.000621371;
        public static final double METERS_TO_YARDS = 1.09361;
        public static final double METERS_TO_KM = 0.001;
        public static final double METERS_TO_NAUTICAL_MILES = 0.000539957;
        public static final double METERS_TO_LIGHT_YEARS = 1.057e-16;
        public static final double MILES_TO_METERS = 1609.34;
        public static final double INCHES_TO_METERS = 0.0254;
        public static final double CM_TO_METERS = 0.01;
        public static final double MM_TO_METERS = 0.001;
        public static final double YARDS_TO_METERS = 0.9144;
        public static final double KM_TO_METERS = 1000;
        public static final double NAUTICAL_MILES_TO_METERS = 1852;
        public static final double LIGHT_YEARS_TO_METERS = 9.461e15;
        public static final double FEET_TO_METERS = 0.3048;
        public static final double ROTATIONS_TO_DEGREES = 360;
        public static final double DEGREES_TO_ROTATIONS = 1/360;
        public static final double ROTATIONS_TO_RADIANS = 2*Math.PI;
        public static final double RADIANS_TO_ROTATIONS = 1/(2*Math.PI);
        public static final double DEGREES_TO_RADIANS = Math.PI/180;
        public static final double RADIANS_TO_DEGREES = 180/Math.PI;
        public static final double HOURS_TO_SECONDS = 3600;
        public static final double MINUTES_TO_SECONDS = 60;
        public static final double SECONDS_TO_HOURS = 1/3600;
        public static final double SECONDS_TO_MINUTES = 1/60;
        public static final double HOURS_TO_MINUTES = 60;
        public static final double MINUTES_TO_HOURS = 1/60;
        public static final double HOURS_TO_MILLISECONDS = 3600000;
        public static final double MINUTES_TO_MILLISECONDS = 60000;
        public static final double SECONDS_TO_MILLISECONDS = 1000;
        public static final double MILLISECONDS_TO_HOURS = 1/3600000;
        public static final double MILLISECONDS_TO_MINUTES = 1/60000;
        public static final double MILLISECONDS_TO_SECONDS = 1/1000;
        public static final double HOURS_TO_MICROSECONDS = 3600000000.0;
        public static final double MINUTES_TO_MICROSECONDS = 60000000.0;
    }
    
    // Driver drives, operator preps states, sys id is for tuning only
    public static final class Controllers {
        public static final XboxController DRIVER_CONTROLLER = new XboxController(0);
        public static final XboxController OPERATOR_CONTROLLER = new XboxController(1);
        public static final XboxController SYSID_CONTROLLER = new XboxController(2);
    }

    public static final class Manager {
        // Should hold toggles for different triggers
    }

    public static final class Elevator {
        // Subsystem Constants
        public static final String SUBSYTEM_NAME = "Elevator";

        // Configs for control loop & stuff
        public static final Measure<Distance> DISTANCE_TOLERANCE = Meters.of(0.05);
        public static final Measure<Velocity<Distance>> VELOCITY_TOLERANCE = MetersPerSecond.of(0.05);

        public static final Measure<Distance> DISTANCE_PER_ROTATION = Meters.of(0.2);

        public static final Measure<Velocity<Distance>> ELEVATOR_MAX_VELOCITY_MPS = MetersPerSecond.of(0.2);
        public static final Measure<Velocity<Velocity<Distance>>> ELEVATOR_MAX_ACCEL_MPSSQ = MetersPerSecondPerSecond.of(0.1);

        // Sim/Physical values
        public static final DCMotor GEARBOX = DCMotor.getNEO(2);
        public static final double GEARING = 20;
        public static final Measure<Mass> CARRIAGE_MASS = Kilograms.of(2);
        public static final Measure<Distance> DRUM_RADIUS = Meters.of(0.5);
        public static final Measure<Distance> MIN_HEIGHT = Meters.of(0);
        public static final Measure<Distance> MAX_HEIGHT = Meters.of(2);
        public static final Measure<Distance> STARTING_HEIGHT = Meters.of(0);
        public static final boolean SIMULATE_GRAVITY = false;

        // Set-States
        public static final class SetStates {
            public static final State ELEVATOR_OUT = new State(1.5, 0);
            public static final State ELEVATOR_IN = new State(0, 0);
        }

        // TODO: Confirm these are acceptable
        public static final Measure<Current> ZEROING_CURRENT_LIMIT = Amps.of(10.0);
        public static final Measure<Current> SMART_CURRENT_LIMIT = Amps.of(30);

        // Device IDs
        public static final int LEFT_CAN_ID = 1;
        public static final int RIGHT_CAN_ID = 2;
        public static final int LIMIT_SWITCH_DIO = 0;

        // Inversions
        public static final boolean LEFT_INVERTED = true;
        public static final boolean RIGHT_INVERTED = !LEFT_INVERTED;

        // Idle Mode
        public static final IdleMode IDLE_MODE = IdleMode.kBrake;

        // Misc
        public static final double ZEROING_SPEED = -0.25;

        // TODO: SYSID/Tune
        // Note, kA can prolly stay at 0, use https://www.reca.lc/ to calculate others or just SYSID
        public static final FFConstants ELEVATOR_FF = new FFConstants(0, 0, 0, 0);
        public static final PIDConstants ELEVATOR_PID = new PIDConstants(1.0, 0.1, 0.1, 0.01);
    }

    public static final class Intake {
        // Subsystem Constants
        public static final String SUBSYTEM_NAME = "Intake";

        // CAN IDs
        public static final int POSITION_ID = 3;
        public static final int SPINNER_ID = 4;

        // Inversions
        public static final boolean PIVOT_INVERTED = false;
        public static final boolean SPINNER_INVERTED = false;

        // Control Constants
        public static final class Sim {

        }

        public static final class SparkMax {
            public static final FFConstants SPINNER_FF = new FFConstants(0, 0, 0, 0);
            public static final PIDConstants SPINNER_PID = new PIDConstants(1.0, 0.1, 0.1, 0.01);
            public static final PIDConstants POSITION_PID = new PIDConstants(1.0, 0.1, 0.1, 0.01);

            public static final IdleMode SPINN_IDLE_MODE = IdleMode.kCoast;
            public static final IdleMode POSITION_IDLE_MODE = IdleMode.kBrake;
        }

        public static final class SetStates {
            public static final SubsystemStateContainer sIDLE = SubsystemStateContainer.fromOpeningIntakeStates("Idle", Rotation2d.fromDegrees(60), RotationsPerSecond.of(0));
            public static final SubsystemStateContainer sINTAKE = SubsystemStateContainer.fromOpeningIntakeStates("Intake", Rotation2d.fromDegrees(0), RotationsPerSecond.of(-10));
            public static final SubsystemStateContainer sOUTTAKE = SubsystemStateContainer.fromOpeningIntakeStates("Outtake", Rotation2d.fromDegrees(0), RotationsPerSecond.of(30));
        }
        // Thresholds
        public static final Rotation2d POSITON_TOLERANCE = Rotation2d.fromDegrees(2);
        public static final Measure<Velocity<Distance>> VELOCITY_TOLERANCE = MetersPerSecond.of(0.05);
    }
}
