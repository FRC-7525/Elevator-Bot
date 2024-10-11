package frc.robot.subsystems.manager;

import frc.robot.Constants;
import frc.robot.pioneersLib.subsystem.Subsystem;
import frc.robot.subsystems.elevator.Elevator;
import frc.robot.subsystems.elevator.ElevatorIOSparkMax;

public class Manager extends Subsystem<ManagerStates> {
    
    private Elevator elevator;
    
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
    }

    @Override
    public void runState() {
        elevator.periodic();
    }
}

