package frc.robot.subsystems.elevator;

import org.littletonrobotics.junction.Logger;

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

    public Elevator(ElevatorIO io) {
        super(Constants.Elevator.SUBSYTEM_NAME, ElevatorStates.IN);
        this.io = io;

        this.inputs = new ElevatorIOInputsAutoLogged();
        this.outputs = new ElevatorIOOutputs();

        sysIdRoutine = new SysIdRoutine(
                new SysIdRoutine.Config(null, null, null, (state) -> Logger.recordOutput("SysIdState", state.toString())),
                new SysIdRoutine.Mechanism(io::runVolts, null, this));
    }

    @Override
    public void runState() {
        // Update Logged Values
        Logger.processInputs(Constants.Elevator.SUBSYTEM_NAME, inputs);
        io.updateInputs(inputs);
        io.updateOutputs(outputs);

        if (io.elevatorZeroed()) {
            // Run To Position
            io.setGoal(getState().getGoalState());
            io.runDistance();
        } else {
            io.zero();
        }
    }

    // Command Factories
    public Command getQualstatic(Direction direction) {
        System.out.println("qualstatic routine");
        return sysIdRoutine.quasistatic(direction);
    }

    public Command getDynamic(Direction direction) {
        System.out.println("dynamic routine");
        return sysIdRoutine.dynamic(direction);
    }
}
