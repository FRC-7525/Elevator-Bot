package frc.robot.subsystems.elevator;

import static edu.wpi.first.units.Units.Volts;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Voltage;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.Constants;
import frc.robot.pioneersLib.FillerSubsystem;
import frc.robot.pioneersLib.subsystem.Subsystem;
import frc.robot.subsystems.elevator.ElevatorIO.ElevatorIOOutputs;

public class Elevator extends Subsystem<ElevatorStates> {
    
    private ElevatorIO io;
    private ElevatorIOInputsAutoLogged inputs;
    private ElevatorIOOutputs outputs;
    private boolean runningSysId = false;
    private SysIdRoutine sysId;

    public Elevator(ElevatorIO io) {
        super(Constants.Elevator.SUBSYTEM_NAME, ElevatorStates.IN);
        this.io = io;

        this.inputs = new ElevatorIOInputsAutoLogged();
        this.outputs = new ElevatorIOOutputs();

        sysId = new SysIdRoutine(
            new SysIdRoutine.Config(
                    null,
                    null,
                    null,
                    (state) -> Logger.recordOutput("Elevator" + "/SysIdState", state.toString())),
            new SysIdRoutine.Mechanism(
                (voltage) -> setVolts(voltage), null, new FillerSubsystem("Elevator")));
    }

    public Command getQualstaticForward() {
        CommandScheduler.getInstance().schedule(sysId.quasistatic(Direction.kForward));
        return sysId.quasistatic(Direction.kForward);
    }   

    @Override
    public void runState() {
        // Update Logged Values
        Logger.processInputs(Constants.Elevator.SUBSYTEM_NAME, inputs);
        io.updateInputs(inputs);
        io.updateOutputs(outputs);

        if (runningSysId) return;

        if (io.elevatorZeroed()) {
            // Run To Position
            io.setGoal(getState().getGoalState());
            io.runDistance();
        } else {
            io.zero();
        }
    }

    /**
     * Note: Do not run this in match, this should only be used for SysId
     * @param voltage
     */
    public void setVolts(Measure<Voltage> voltage) {
        runningSysId = true;
        // System.out.println("lallala");
        io.runVolts(voltage);
    }

    public void exitSysId() {
        runningSysId = false;
    }
}
