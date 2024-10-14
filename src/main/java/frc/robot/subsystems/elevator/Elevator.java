package frc.robot.subsystems.elevator;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Voltage;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.Constants;
import frc.robot.pioneersLib.subsystem.Subsystem;
import frc.robot.subsystems.elevator.ElevatorIO.ElevatorIOOutputs;

public class Elevator extends Subsystem<ElevatorStates> {
    
    private ElevatorIO io;
    private ElevatorIOInputsAutoLogged inputs;
    private ElevatorIOOutputs outputs;
    private boolean runningSysId = false;

    private SysIdRoutine.Config sysIdConfig;
    private SysIdRoutine.Mechanism sysIdMechanism;
    private SysIdRoutine sysIdRoutine;
    private String state;

    private XboxController sysIdController = new XboxController(3);
    private final CommandScheduler commandScheduler = CommandScheduler.getInstance();

    public Elevator(ElevatorIO io) {
        super(Constants.Elevator.SUBSYTEM_NAME, ElevatorStates.IN);
        this.io = io;

        this.inputs = new ElevatorIOInputsAutoLogged();
        this.outputs = new ElevatorIOOutputs();

        sysIdConfig = new SysIdRoutine.Config(null, null, null, (state) -> Logger.recordOutput("SysId", state.toString()));
        sysIdMechanism = new SysIdRoutine.Mechanism((volts) -> setVolts(volts), null, this);
        sysIdRoutine = new SysIdRoutine(sysIdConfig, sysIdMechanism);
        state = "none";
    } 

    public void runSum() {
        CommandScheduler.getInstance().schedule(sysIdRoutine.quasistatic(Direction.kForward));
        System.out.println("Running SysId");
    }

    @Override
    public void runState() {
        // Update Logged Values
        Logger.processInputs(Constants.Elevator.SUBSYTEM_NAME, inputs);
        io.updateInputs(inputs);
        io.updateOutputs(outputs);

        if (sysIdController.getAButton()) {
            commandScheduler.schedule(sysIdRoutine.quasistatic(SysIdRoutine.Direction.kForward));
            state = "quasistatic-forward";
        }
        if (sysIdController.getBButton()) {
                commandScheduler.schedule(sysIdRoutine.quasistatic(SysIdRoutine.Direction.kReverse));
                state = "quasistatic-reverse";
        }
        if (sysIdController.getXButton()) {
                commandScheduler.schedule(sysIdRoutine.dynamic(SysIdRoutine.Direction.kForward));
                state = "dynamic-forward";
        }
        if (sysIdController.getYButton()) {
                commandScheduler.schedule(sysIdRoutine.dynamic(SysIdRoutine.Direction.kReverse));
                state = "dynamic-reverse";
        }

        Logger.recordOutput("state", state);
        // if (runningSysId) {return;}
        // System.out.println("lalala");

        // if (io.elevatorZeroed()) {
        //     // Run To Position
        //     io.setGoal(getState().getGoalState());
        //     io.runDistance();
        // } else {
        //     io.zero();
        // }
    }

    /**
     * Note: Do not run this in match, this should only be used for SysId
     * @param voltage
     */
    public void setVolts(Measure<Voltage> voltage) {
        runningSysId = true;
        System.out.println(voltage);
        io.runVolts(voltage);
    }

    public void exitSysId() {
        runningSysId = false;
    }
}
