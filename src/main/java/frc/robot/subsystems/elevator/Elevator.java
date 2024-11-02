package frc.robot.subsystems.elevator;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Voltage;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.Constants;
import frc.robot.pioneersLib.subsystem.Subsystem;
import frc.robot.subsystems.elevator.ElevatorIO.ElevatorIOOutputs;

public class Elevator extends Subsystem<ElevatorStates> {

    private ElevatorIO io;
    private ElevatorIOInputsAutoLogged inputs;
    private ElevatorIOOutputs outputs;

    private SysIdRoutine sysIdRoutine;

    // Find a good way to do this that isn't this
    private Boolean sysId = true;   

    public Elevator(ElevatorIO io) {
        super(Constants.Elevator.SUBSYTEM_NAME, ElevatorStates.IN);
        this.io = io;

        this.inputs = new ElevatorIOInputsAutoLogged();
        this.outputs = new ElevatorIOOutputs();

        this.sysIdRoutine = new SysIdRoutine(
                new SysIdRoutine.Config(null, null, null, (state) -> Logger.recordOutput("SysIdState", state.toString())),
                new SysIdRoutine.Mechanism(this::runVolts, null, this));
    }

    @Override
    public void runState() {
        // Update Logged Values
        Logger.processInputs(Constants.Elevator.SUBSYTEM_NAME, inputs);
        io.updateInputs(inputs);
        io.updateOutputs(outputs);

        // While sysId is getting run don't run state for other stuff
        if (sysId) {sysId = false; return;};

        if (io.elevatorZeroed()) {
            // Run To Position
            io.setGoal(getState().getGoalState());
            io.runDistance();
        } else {
            io.zero();
        }
    }

    public void runVolts(Measure<Voltage> volts) {
        sysId = true;
        io.runVolts(volts);
    }

    // Command Factories
    public Command getQualstatic(Direction direction) {
        return sysIdRoutine.quasistatic(direction);
    }

    public Command getDynamic(Direction direction) {
        return sysIdRoutine.dynamic(direction);
    }

    public void logPID(boolean logPID) {
        io.setLogPID(logPID);
    }
}
