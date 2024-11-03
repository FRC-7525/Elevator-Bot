package frc.robot.subsystems.manager;

import frc.robot.Constants;
import frc.robot.pioneersLib.subsystem.Subsystem;
import frc.robot.subsystems.elevator.Elevator;
import frc.robot.subsystems.elevator.ElevatorIO;
import frc.robot.subsystems.elevator.ElevatorIOSim;
import frc.robot.subsystems.elevator.ElevatorIOSparkMax;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.intake.IntakeIOSim;
import frc.robot.subsystems.intake.IntakeIOSparkMax;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;

import static frc.robot.Constants.Controllers.*;

public class Manager extends Subsystem<ManagerStates> {

    private AutoManager autoManager;
    private Elevator elevator;
    private Intake intake;
    
    public Manager() {
        super("Manager", ManagerStates.IDLE);

        switch (Constants.ROBOT_STATE) {
            case REAL:  
                elevator = new Elevator(new ElevatorIOSparkMax());
                intake = new Intake(new IntakeIOSparkMax());
                break;
            case SIM:
                elevator = new Elevator(new ElevatorIOSim());
                intake = new Intake(new IntakeIOSim());
                break;
            case REPLAY:
                elevator = new Elevator(new ElevatorIO() {});
                intake = new Intake(new IntakeIOSparkMax());
                break;
            default:
                break;
        }

        autoManager = new AutoManager(this);
        setupTesting();

        // Passing
        addTrigger(ManagerStates.IDLE, ManagerStates.RISING, () -> DRIVER_CONTROLLER.getAButtonPressed());
        addTrigger(ManagerStates.RISING, ManagerStates.PASSING, () -> elevator.atSetpoint());
        addTrigger(ManagerStates.PASSING, ManagerStates.IDLE, () -> DRIVER_CONTROLLER.getAButtonPressed());

        // Intaking
        addTrigger(ManagerStates.IDLE, ManagerStates.INTAKING, () -> DRIVER_CONTROLLER.getBButtonPressed());
        addTrigger(ManagerStates.INTAKING, ManagerStates.IDLE, () -> DRIVER_CONTROLLER.getBButtonPressed());
        
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
        intake.periodic();

        // Run Subsystem States 
        elevator.setState(getState().getElevatorState());
    }

    private void setupTesting() {
        elevator.logPID(Constants.TUNING);
        intake.logPID(Constants.TUNING);
    }

    public Command getAutoCommand() {
        return autoManager.getSelected();
    }
}

