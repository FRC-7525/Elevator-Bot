package frc.robot.subsystems.elevator;

import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.trajectory.TrapezoidProfile.State;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Voltage;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.simulation.ElevatorSim;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static frc.robot.Constants.Elevator.*;

public class ElevatorIOSim implements ElevatorIO {

    private ProfiledPIDController pidController;
    private ElevatorFeedforward ffController;

    private double volts;

    private boolean simZeroed;
    private boolean logPID;

    private ElevatorSim sim;

    public ElevatorIOSim() {
        sim = new ElevatorSim(GEARBOX, GEARING,
                CARRIAGE_MASS.magnitude(), DRUM_RADIUS.magnitude(),
                MIN_HEIGHT.magnitude(), MAX_HEIGHT.magnitude(),
                SIMULATE_GRAVITY, STARTING_HEIGHT.magnitude());

        // Configure FF and PID controllers, kA can be ignored for FF, PID is just PID
        // but with a motion profile
        ffController = new ElevatorFeedforward(ELEVATOR_FF.kS, ELEVATOR_FF.kG, ELEVATOR_FF.kV, ELEVATOR_FF.kA);

        pidController = new ProfiledPIDController(0, 0, 0, null);
        pidController.setTolerance(DISTANCE_TOLERANCE.magnitude(),
                VELOCITY_TOLERANCE.magnitude());
        pidController = new ProfiledPIDController(ELEVATOR_PID.kP, ELEVATOR_PID.kI, ELEVATOR_PID.kD,
                new TrapezoidProfile.Constraints(ELEVATOR_MAX_VELOCITY_MPS,
                        ELEVATOR_MAX_ACCEL_MPSSQ));
        pidController.setIZone(ELEVATOR_PID.iZone);
        // No null pointers
        simZeroed = false;
        logPID = false;
        volts = 0;
    }

    // Update set of logged inputs
    @Override
    public void updateInputs(ElevatorIOInputs inputs) {
        sim.update(0.02);
        inputs.elevatorDistancePointMeters = pidController.getSetpoint().position;
        inputs.elevatorDistanceSpeedPointMS = pidController.getSetpoint().velocity;
        inputs.elevatorSpeedMS = sim.getVelocityMetersPerSecond();
        inputs.elevatorDistancePointGoalMeters = pidController.getGoal().position;
        inputs.elevatorSpeedPointGoalMS = pidController.getGoal().velocity;
        inputs.elevatorPositionMeters = sim.getPositionMeters();
        inputs.elevatorZeroed = simZeroed;
        inputs.elevatorRightVolts = volts;
        inputs.elevatorLeftVolts = volts;
        if (logPID) SmartDashboard.putData("Elevator Controller", pidController);
    }

    // Update set of logged outputs
    @Override
    public void updateOutputs(ElevatorIOOutputs outputs) {
        // Otherwise current stays up when you're disabled :/ make an issue or sum idk
        if (DriverStation.isDisabled()) {
            outputs.leftMotorCurrent = 0;
            outputs.rightMotorCurrent = 0;
            return;
        }

        outputs.leftMotorCurrent = sim.getCurrentDrawAmps();
        outputs.rightMotorCurrent = sim.getCurrentDrawAmps();
    }

    // Sets the end state of the PID controller, don't need to do anything for ff
    // because it bases its setpoint off the PID setpoint
    @Override
    public void setGoal(State goalState) {
        if (!goalState.equals(pidController.getGoal()))
            pidController.setGoal(goalState);
    }

    // Basically a periodic, runs the motor velocity based on the goal state using
    // FF + profile PID
    @Override
    public void runDistance() {
        volts = pidController.calculate(sim.getPositionMeters())
                + ffController.calculate(pidController.getSetpoint().velocity);
        sim.setInputVoltage(volts);
    }

    // Open loop control for SYSID
    @Override
    public void runVolts(Measure<Voltage> volts) {
        this.volts = volts.magnitude();
        System.out.println(volts);
        sim.setInputVoltage(this.volts);
    }

    /**
     * Set the logPID boolean, should be enabled while tuning PID values
     * @param logPID if or not to log PID to SD
     */
    @Override
    public void setLogPID(boolean logPID) {
        this.logPID = logPID;
    }

    // At setpoint for state transitions
    @Override
    public boolean atSetpoint() {
        return pidController.atSetpoint();
    }

    // Sim is already zeroed, no need to do it again
    @Override
    public void zero() {
        simZeroed = true;
    }

    @Override
    public boolean elevatorZeroed() {
        return simZeroed;
    }

}
