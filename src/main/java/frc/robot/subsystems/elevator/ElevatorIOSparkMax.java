package frc.robot.subsystems.elevator;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;

import frc.robot.Constants;
import frc.robot.pioneersLib.controlConstants.FFConstants;
import frc.robot.pioneersLib.controlConstants.PIDConstants;

import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.trajectory.TrapezoidProfile.State;

public class ElevatorIOSparkMax implements ElevatorIO {
    // "Comment your code" 🤓
    private ProfiledPIDController pidController;
    private ElevatorFeedforward ffController;

    private PIDConstants pidConstants;
    private FFConstants ffConstants;

    private CANSparkMax rigtMotor;
    private CANSparkMax leftMotor;
    private RelativeEncoder encoder;

    private boolean rightMotorZeroed;
    private boolean leftMotorZeroed;

    private double metersPerRotation;

    public ElevatorIOSparkMax() {
        // Sparkmax configs
        // TODO: Set to correct motor IDs
        leftMotor = new CANSparkMax(Constants.Elevator.LEFT_CAN_ID, MotorType.kBrushless);
        rigtMotor = new CANSparkMax(Constants.Elevator.RIGHT_CAN_ID, MotorType.kBrushless);
        encoder = rigtMotor.getEncoder();

        rigtMotor.setInverted(Constants.Elevator.RIGHT_INVERTED);
        rigtMotor.setIdleMode(Constants.Elevator.IDLE_MODE);

        leftMotor.follow(rigtMotor);
        leftMotor.setInverted(Constants.Elevator.LEFT_INVERTED);
        leftMotor.setIdleMode(Constants.Elevator.IDLE_MODE);

        leftMotor.setSmartCurrentLimit(Constants.Elevator.SMART_CURRENT_LIMIT_AMPS);
        rigtMotor.setSmartCurrentLimit(Constants.Elevator.SMART_CURRENT_LIMIT_AMPS);

        rigtMotor.burnFlash();
        leftMotor.burnFlash();


        // Configure FF and PID controllers, kA can be ignored for FF, PID is just PID but with a motion profile
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

        // No null pointers
        rightMotorZeroed = false;
        leftMotorZeroed = false;
    }

    // Update set of logged inputs
    @Override
    public void updateInputs(ElevatorIOInputs inputs) {
        inputs.elevatorDistancePointMeters = pidController.getSetpoint().position;
        inputs.elevatorDistanceSpeedPointMS = pidController.getSetpoint().velocity;
        inputs.elevatorDistancePointGoalMeters = pidController.getGoal().position;
        inputs.elevatorSpeedPointGoalMS = pidController.getGoal().velocity;
        inputs.elevatorPositionMeters = encoder.getPosition() * metersPerRotation;
        inputs.elevatorZeroed = elevatorZeroed();
    }

    // Update set of logged outputs
    @Override
    public void updateOutputs(ElevatorIOOutputs outputs) {
        outputs.leftMotorCurrent = leftMotor.getAppliedOutput();
        outputs.rightMotorCurrent = rigtMotor.getAppliedOutput();
    }

    // Sets the end state of the PID controller, don't need to do anything for ff because it bases its setpoint off the PID setpoint
    @Override
    public void setGoal(State goalState) {
        if (!goalState.equals(pidController.getGoal())) pidController.setGoal(goalState);
    }

    // Basically a periodic, runs the motor velocity based on the goal state using FF + profile PID
    // Accounting for gear ratio in getPosition is unecessary because meters per rotation takes it into account
    @Override
    public void runDistance() {
        rigtMotor.setVoltage(pidController.calculate(encoder.getPosition() * metersPerRotation)
                + ffController.calculate(pidController.getSetpoint().velocity));

    }

    // At setpoint for state transitions
    @Override
    public boolean atSetpoint() {
        return pidController.atSetpoint();
    }

    // TODO: Is this needed?
    // Zero the elevator
    @Override
    public void zero() {
        double leftZeroingSpeed = -0.25;
        double rightZeroingSpeed = -0.25;

        if (rigtMotor.getOutputCurrent() > Constants.Elevator.ZEROING_CURRENT_LIMIT_AMPS) {
            rightZeroingSpeed = 0;
            if (!rightMotorZeroed) encoder.setPosition(0); rightMotorZeroed = true;
        }

        if (leftMotor.getOutputCurrent() > Constants.Elevator.ZEROING_CURRENT_LIMIT_AMPS) {
            leftZeroingSpeed = 0;
            if (!leftMotorZeroed) encoder.setPosition(0); leftMotorZeroed = true;
        }

        rigtMotor.set(rightZeroingSpeed);
        leftMotor.set(leftZeroingSpeed);
    }

    @Override
    public boolean elevatorZeroed() {
        return leftMotorZeroed && rightMotorZeroed;
    }
}
