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
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Voltage;
import edu.wpi.first.wpilibj.DigitalInput;

public class ElevatorIOSparkMax implements ElevatorIO {
    // "Comment your code" 🤓
    private ProfiledPIDController pidController;
    private ElevatorFeedforward ffController;

    private PIDConstants pidConstants;
    private FFConstants ffConstants;

    private CANSparkMax rightMotor;
    private CANSparkMax leftMotor;
    private RelativeEncoder encoder;

    private DigitalInput limitSwitch;

    private boolean rightMotorZeroed;
    private boolean leftMotorZeroed;

    private double metersPerRotation;

    public ElevatorIOSparkMax() {
        // Sparkmax configs
        // TODO: Set to correct motor IDs
        leftMotor = new CANSparkMax(Constants.Elevator.LEFT_CAN_ID, MotorType.kBrushless);
        rightMotor = new CANSparkMax(Constants.Elevator.RIGHT_CAN_ID, MotorType.kBrushless);
        encoder = rightMotor.getEncoder();

        rightMotor.setInverted(Constants.Elevator.RIGHT_INVERTED);
        rightMotor.setIdleMode(Constants.Elevator.IDLE_MODE);

        leftMotor.follow(rightMotor);
        leftMotor.setInverted(Constants.Elevator.LEFT_INVERTED);
        leftMotor.setIdleMode(Constants.Elevator.IDLE_MODE);

        leftMotor.setSmartCurrentLimit((int) Constants.Elevator.SMART_CURRENT_LIMIT.magnitude());
        rightMotor.setSmartCurrentLimit((int) Constants.Elevator.SMART_CURRENT_LIMIT.magnitude());

        rightMotor.burnFlash();
        leftMotor.burnFlash();


        // Configure FF and PID controllers, kA can be ignored for FF, PID is just PID but with a motion profile
        ffConstants = Constants.Elevator.ELEVATOR_FF;
        ffController = new ElevatorFeedforward(ffConstants.kS, ffConstants.kG, ffConstants.kV, ffConstants.kA);

        pidConstants = Constants.Elevator.ELEVATOR_PID;
        pidController.setTolerance(Constants.Elevator.DISTANCE_TOLERANCE.magnitude(),
                Constants.Elevator.VELOCITY_TOLERANCE.magnitude());
        pidController = new ProfiledPIDController(pidConstants.kP, pidConstants.kI, pidConstants.kD,
                new TrapezoidProfile.Constraints(Constants.Elevator.ELEVATOR_MAX_VELOCITY_MPS,
                        Constants.Elevator.ELEVATOR_MAX_ACCEL_MPSSQ));
        pidController.setIZone(pidConstants.iZone);

        metersPerRotation = Constants.Elevator.DISTANCE_PER_ROTATION.magnitude();

        limitSwitch = new DigitalInput(Constants.Elevator.LIMIT_SWITCH_DIO);

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
        outputs.rightMotorCurrent = rightMotor.getAppliedOutput();
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
        rightMotor.setVoltage(pidController.calculate(encoder.getPosition() * metersPerRotation)
                + ffController.calculate(pidController.getSetpoint().velocity));

    }

    // TODO: Does left motor volts also need to be set
    // Open loop control for SYSID
    @Override
    public void runVolts(Measure<Voltage> volts) {
        rightMotor.setVoltage(volts.magnitude());
    }

    // At setpoint for state transitions
    @Override
    public boolean atSetpoint() {
        return pidController.atSetpoint();
    }

    // TODO: Is this needed? (ask Nick)
    // Zero the elevator
    @Override
    public void zero() {
        double leftZeroingSpeed = -Constants.Elevator.ZEROING_SPEED;
        double rightZeroingSpeed = -Constants.Elevator.ZEROING_SPEED;

        if (rightMotor.getOutputCurrent() > Constants.Elevator.ZEROING_CURRENT_LIMIT.magnitude() || !limitSwitch.get()) {
            rightZeroingSpeed = 0;
            if (!rightMotorZeroed) encoder.setPosition(0); rightMotorZeroed = true;
        }

        if (leftMotor.getOutputCurrent() > Constants.Elevator.ZEROING_CURRENT_LIMIT.magnitude() || !limitSwitch.get()) {
            leftZeroingSpeed = 0;
            if (!leftMotorZeroed) encoder.setPosition(0); leftMotorZeroed = true;
        }

        rightMotor.set(rightZeroingSpeed);
        leftMotor.set(leftZeroingSpeed);
    }

    @Override
    public boolean elevatorZeroed() {
        return leftMotorZeroed && rightMotorZeroed;
    }
}
