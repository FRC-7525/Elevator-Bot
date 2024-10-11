package frc.robot.subsystems.manager;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Constants;
import frc.robot.pioneersLib.subsystem.Subsystem;
import frc.robot.subsystems.elevator.Elevator;
import frc.robot.subsystems.elevator.ElevatorIOSparkMax;

public class Manager extends Subsystem<ManagerStates> {
    
    private Elevator elevator;

    private XboxController driverController;
    private XboxController operatorController;
    
    public Manager() {
        super("Manager", ManagerStates.IDLE);

        switch (Constants.ROBOT_STATE) {
            case REAL:  
                elevator = new Elevator(new ElevatorIOSparkMax());
                break;
            case SIM:
                break;
            case REPLAY:
                break;
            default:
                break;
        }

        driverController = Constants.DRIVER_CONTROLLER;
        operatorController = Constants.OPERATOR_CONTROLLER;

        // Elevator in and out
        addTrigger(ManagerStates.IDLE, ManagerStates.PASSING, () -> driverController.getAButtonPressed());
        addTrigger(ManagerStates.PASSING, ManagerStates.IDLE, () -> driverController.getAButtonPressed());
    }

    @Override
    public void runState() {
        // Run Subsystem Periodics
        elevator.periodic();

        // Run Subsystem States
        elevator.setState(getState().getElevatorState());
    }
}

