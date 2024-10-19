package frc.robot.subsystems.manager;

import frc.robot.Constants;
import frc.robot.pioneersLib.subsystem.Subsystem;
import frc.robot.subsystems.elevator.Elevator;
import frc.robot.subsystems.elevator.ElevatorIO;
import frc.robot.subsystems.elevator.ElevatorIOSim;
import frc.robot.subsystems.elevator.ElevatorIOSparkMax;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;

import static frc.robot.Constants.Controllers.*;

public class Manager extends Subsystem<ManagerStates> {

    private AutoManager autoManager;
    private Elevator elevator;
    
    public Manager() {
        super("Manager", ManagerStates.IDLE);

        switch (Constants.ROBOT_STATE) {
            case REAL:  
                elevator = new Elevator(new ElevatorIOSparkMax());
                break;
            case SIM:
                elevator = new Elevator(new ElevatorIOSim());
                break;
            case REPLAY:
                elevator = new Elevator(new ElevatorIO() {});
                break;
            default:
                break;
        }

        // Keep this below the swith statement to avoid null pointers :(
        autoManager = new AutoManager(this);

        // Elevator in and out
        addTrigger(ManagerStates.IDLE, ManagerStates.PASSING, () -> DRIVER_CONTROLLER.getAButtonPressed());
        addTrigger(ManagerStates.PASSING, ManagerStates.IDLE, () -> DRIVER_CONTROLLER.getAButtonPressed());
        
        // SysID Nonsense
        addRunnableTrigger(() -> CommandScheduler.getInstance().schedule(elevator.getDynamic(Direction.kForward)), () -> SYSID_CONTROLLER.getXButtonPressed());
        addRunnableTrigger(() -> CommandScheduler.getInstance().schedule(elevator.getDynamic(Direction.kReverse)), () -> SYSID_CONTROLLER.getAButtonPressed());
        addRunnableTrigger(() -> CommandScheduler.getInstance().schedule(elevator.getQualstatic(Direction.kForward)), () -> SYSID_CONTROLLER.getBButtonPressed());
        addRunnableTrigger(() -> CommandScheduler.getInstance().schedule(elevator.getQualstatic(Direction.kReverse)), () -> SYSID_CONTROLLER.getYButtonPressed());

        // Saftey for not doing bad stuff or sum
        addRunnableTrigger(() -> {CommandScheduler.getInstance().cancelAll(); setState(ManagerStates.IDLE);}, () -> OPERATOR_CONTROLLER.getXButtonPressed());
    }

    @Override
    public void runState() {

        // Run Subsystem Periodics (comment out for sysId if u wana be safe ig)
        elevator.periodic();

        // Run Subsystem States 
        elevator.setState(getState().getElevatorState());
    }

    public Command getAutoCommand() {
        return autoManager.getSelected();
    }
}

