package frc.robot.subsystems.elevator;

import org.littletonrobotics.junction.console.RIOConsoleSource;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;

import frc.robot.Constants;
import frc.robot.pioneersLib.controlConstants.FFConstants;
import frc.robot.pioneersLib.controlConstants.PIDConstants;

import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class ElevatorIOSparkMax implements ElevatorIO {

    private ProfiledPIDController pidController;
    private ElevatorFeedforward ffController;

    private PIDConstants pidConstants;
    private FFConstants ffConstants;

    private CANSparkMax rigtMotor;
    private CANSparkMax leftMotor;
    private RelativeEncoder encoder;

    private double metersPerRotation;

    public ElevatorIOSparkMax() {
        leftMotor = new CANSparkMax(1, MotorType.kBrushless);
        rigtMotor = new CANSparkMax(2, MotorType.kBrushless);
        encoder = rigtMotor.getEncoder();

        leftMotor.follow(rigtMotor);
        leftMotor.setInverted(true);

        leftMotor.setSmartCurrentLimit(Constants.Elevator.SMART_CURRENT_LIMIT_AMPS);
        rigtMotor.setSmartCurrentLimit(Constants.Elevator.SMART_CURRENT_LIMIT_AMPS);

        rigtMotor.burnFlash();
        leftMotor.burnFlash();

        ffConstants = Constants.Elevator.ELEVATOR_FF;
        ffController = new ElevatorFeedforward(ffConstants.kS, ffConstants.kG, ffConstants.kV, ffConstants.kA);

        pidConstants = Constants.Elevator.ELEVATOR_PID;
        pidController.setTolerance(Constants.Elevator.DISTANCE_TOLERANCE_METERS,
                Constants.Elevator.VELOCITY_TOLERANCE_MS);
        pidController = new ProfiledPIDController(pidConstants.kP, pidConstants.kI, pidConstants.kD,
                new TrapezoidProfile.Constraints(Constants.Elevator.ELEVATOR_MAX_VELOCITY_MPS,
                        Constants.Elevator.ELEVATOR_MAX_ACCEL_MPSSQ));
        pidController.setIZone(pidConstants.iZone);

        metersPerRotation = Constants.Elevator.DISTANCE_METERS_PER_ROTATION;
    }

    @Override
    public void updateInputs(ElevatorIOInputs inputs) {
        inputs.elevatorDistancePointMeters = pidController.getSetpoint().position;
        inputs.elevatorDistanceSpeedPointMS = pidController.getSetpoint().velocity;
        inputs.elevatorDistancePointGoalMeters = pidController.getGoal().position;
        inputs.elevatorSpeedPointGoalMS = pidController.getGoal().velocity;
        inputs.elevatorPositionMeters = encoder.getPosition() * metersPerRotation;
    }

    @Override
    public void updateOutputs(ElevatorIOOutputs outputs) {
        outputs.leftMotorCurrent = leftMotor.getAppliedOutput();
        outputs.rightMotorCurrent = rigtMotor.getAppliedOutput();
    }

    @Override
    public void setDistance(double distancePointGoalMeters) {
        rigtMotor.setVoltage(pidController.calculate(encoder.getPosition() * metersPerRotation, distancePointGoalMeters)
                + ffController.calculate(pidController.getSetpoint().velocity));

    }

    @Override
    public boolean atSetpoint() {
        return pidController.atSetpoint();
    }

}
