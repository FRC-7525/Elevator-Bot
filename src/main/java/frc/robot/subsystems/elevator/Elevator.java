package frc.robot.subsystems.elevator;

import static edu.wpi.first.units.Units.Volts;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Voltage;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.Constants;
import frc.robot.pioneersLib.FillerSubsystem;
import frc.robot.pioneersLib.SysIdWrapper;
import frc.robot.pioneersLib.subsystem.Subsystem;
import frc.robot.subsystems.elevator.ElevatorIO.ElevatorIOOutputs;

public class Elevator extends Subsystem<ElevatorStates> {
    
    private ElevatorIO io;
    private ElevatorIOInputsAutoLogged inputs;
    private ElevatorIOOutputs outputs;
    private SysIdWrapper sysIdWrapper;
    private boolean runningSysId = false;

    public Elevator(ElevatorIO io) {
        super(Constants.Elevator.SUBSYTEM_NAME, ElevatorStates.IN);
        this.io = io;

        this.inputs = new ElevatorIOInputsAutoLogged();
        this.outputs = new ElevatorIOOutputs();

        sysIdWrapper = new SysIdWrapper("Elevator", (setVolts) -> setVolts(setVolts));
    } 

    public Command getSysIdCommand() {
        return sysIdWrapper.getDynamicForward();
    }

    public void scheduleSysIdCommand() {
        sysIdWrapper.scheduleQuasistaticForward();
    }

    @Override
    public void runState() {
        // Update Logged Values
        Logger.processInputs(Constants.Elevator.SUBSYTEM_NAME, inputs);
        io.updateInputs(inputs);
        io.updateOutputs(outputs);

        // if (runningSysId) {return;}
        // System.out.println("lalala");

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
        io.runVolts(voltage);
    }

    public void exitSysId() {
        runningSysId = false;
    }
}
