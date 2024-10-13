package frc.robot.subsystems.elevator;

import frc.robot.Constants;
import frc.robot.pioneersLib.controlConstants.FFConstants;
import frc.robot.pioneersLib.controlConstants.PIDConstants;

import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.trajectory.TrapezoidProfile.State;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Voltage;
import edu.wpi.first.wpilibj.simulation.ElevatorSim;

public class ElevatorIOSim implements ElevatorIO {

    private ProfiledPIDController pidController;
    private ElevatorFeedforward ffController;

    private PIDConstants pidConstants;
    private FFConstants ffConstants;

    private boolean simZeroed;

    private ElevatorSim sim;

    public ElevatorIOSim() {
        sim = new ElevatorSim(Constants.Elevator.GEARBOX, Constants.Elevator.GEARING,
                Constants.Elevator.CARRIAGE_MASS_KG, Constants.Elevator.DRUM_RADIUS_METERS,
                Constants.Elevator.MIN_HEIGH_METERS, Constants.Elevator.MAX_HEIGHT_METERS,
                Constants.Elevator.SIMULATE_GRAVITY, Constants.Elevator.STARTING_HEIGHT_METERS);

        // Configure FF and PID controllers, kA can be ignored for FF, PID is just PID
        // but with a motion profile
        ffConstants = Constants.Elevator.ELEVATOR_FF;
        ffController = new ElevatorFeedforward(ffConstants.kS, ffConstants.kG, ffConstants.kV, ffConstants.kA);

        pidConstants = Constants.Elevator.ELEVATOR_PID;
        pidController.setTolerance(Constants.Elevator.DISTANCE_TOLERANCE_METERS,
                Constants.Elevator.VELOCITY_TOLERANCE_MS);
        pidController = new ProfiledPIDController(pidConstants.kP, pidConstants.kI, pidConstants.kD,
                new TrapezoidProfile.Constraints(Constants.Elevator.ELEVATOR_MAX_VELOCITY_MPS,
                        Constants.Elevator.ELEVATOR_MAX_ACCEL_MPSSQ));
        pidController.setIZone(pidConstants.iZone);
        // No null pointers
        simZeroed = false;
    }

    // Update set of logged inputs
    @Override
    public void updateInputs(ElevatorIOInputs inputs) {
        inputs.elevatorDistancePointMeters = pidController.getSetpoint().position;
        inputs.elevatorDistanceSpeedPointMS = pidController.getSetpoint().velocity;
        inputs.elevatorDistancePointGoalMeters = pidController.getGoal().position;
        inputs.elevatorSpeedPointGoalMS = pidController.getGoal().velocity;
        inputs.elevatorPositionMeters = sim.getPositionMeters();
    }

    // Update set of logged outputs
    @Override
    public void updateOutputs(ElevatorIOOutputs outputs) {
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
        sim.setInputVoltage(pidController.calculate(sim.getPositionMeters())
                + ffController.calculate(pidController.getSetpoint().velocity));
    }

    // Open loop control for SYSID
    @Override
    public void runVolts(Measure<Voltage> volts) {
        sim.setInputVoltage(volts.magnitude());
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
