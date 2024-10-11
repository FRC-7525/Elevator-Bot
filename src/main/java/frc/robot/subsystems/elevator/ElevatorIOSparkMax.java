package frc.robot.subsystems.elevator;

import org.littletonrobotics.junction.console.RIOConsoleSource;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;

import frc.robot.Constants;
import frc.robot.pioneersLib.controlConstants.PIDConstants;

import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class ElevatorIOSparkMax implements ElevatorIO {

    private ProfiledPIDController controller;
    private PIDConstants controllerConstants;
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

        controllerConstants = Constants.Elevator.ELEVATOR_PID;
        controller.setPID(controllerConstants.kP, controllerConstants.kI, controllerConstants.kD);
        controller.setIZone(controllerConstants.iZone);

        controller.setTolerance(Constants.Elevator.DISTANCE_TOLERANCE_METERS, Constants.Elevator.VELOCITY_TOLERANCE_MS);
        controller = new ProfiledPIDController(controllerConstants.kP, controllerConstants.kI, controllerConstants.kD,
                new TrapezoidProfile.Constraints(Constants.Elevator.ELEVATOR_MAX_VELOCITY_MPS,
                        Constants.Elevator.ELEVATOR_MAX_ACCEL_MPSSQ));

        metersPerRotation = Constants.Elevator.DISTANCE_METERS_PER_ROTATION;
    }

    @Override
    public void updateInputs(ElevatorIOInputs inputs) {
        inputs.elevatorDistancePointMeters = controller.getSetpoint().position;
        inputs.elevatorDistanceSpeedPointMS = controller.getSetpoint().velocity;
        inputs.elevatorDistancePointGoalMeters = controller.getGoal().position;
        inputs.elevatorSpeedPointGoalMS = controller.getGoal().velocity;
        inputs.elevatorPositionMeters = encoder.getPosition() * metersPerRotation;
    }

    @Override
    public void updateOutputs(ElevatorIOOutputs outputs) {
        outputs.leftMotorCurrent = leftMotor.getAppliedOutput();
        outputs.rightMotorCurrent = rigtMotor.getAppliedOutput();
    }

    @Override
    public void setDistance(double distancePointGoalMeters) {
        rigtMotor.setVoltage(controller.calculate(encoder.getPosition() * metersPerRotation, distancePointGoalMeters));
    }

    @Override
    public boolean atSetpoint() {
        return controller.atSetpoint();
    }

}
