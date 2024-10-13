package frc.robot.subsystems.manager;

import edu.wpi.first.units.Measure;
import frc.robot.Constants;
import frc.robot.pioneersLib.subsystem.Subsystem;
import frc.robot.subsystems.elevator.Elevator;
import frc.robot.subsystems.elevator.ElevatorIO;
import frc.robot.subsystems.elevator.ElevatorIOSim;
import frc.robot.subsystems.elevator.ElevatorIOSparkMax;

import static edu.wpi.first.units.Units.*;

import edu.wpi.first.units.Voltage;
import edu.wpi.first.wpilibj2.command.Command;
public class Manager extends Subsystem<ManagerStates> {

    private AutoManager autoManager;
    private Elevator elevator;
    
    public Manager() {
        super("Manager", ManagerStates.IDLE);

        autoManager = new AutoManager(this);

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

        // Elevator in and out
        addTrigger(ManagerStates.IDLE, ManagerStates.PASSING, () -> Constants.DRIVER_CONTROLLER.getAButtonPressed());
        addTrigger(ManagerStates.PASSING, ManagerStates.IDLE, () -> Constants.DRIVER_CONTROLLER.getAButtonPressed());
    }

    @Override
    public void runState() {
        // Run Subsystem Periodics
        elevator.periodic();

        // Run Subsystem States
        elevator.setState(getState().getElevatorState());
    }

    // SysId Util
    public void setElevatorVolts(Measure<Voltage> voltage) {
        elevator.setVolts(voltage);
    }

    public Command getAutoCommand() {
        return autoManager.getSelected();
    }
}

