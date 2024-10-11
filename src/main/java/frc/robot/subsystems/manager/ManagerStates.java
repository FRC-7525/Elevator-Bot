package frc.robot.subsystems.manager;

import frc.robot.pioneersLib.subsystem.SubsystemStates;
import frc.robot.subsystems.elevator.ElevatorStates;

public enum ManagerStates implements SubsystemStates{
    IDLE("IDLE", ElevatorStates.IN),
    PASSING("OUT", ElevatorStates.OUT);

    private String stateString;
    private ElevatorStates elevatorState;
    
    ManagerStates(String stateString, ElevatorStates elevatorState) {
        this.stateString = stateString;
        this.elevatorState = elevatorState;
    }

    public String getStateString() {
        return stateString;
    }

    public ElevatorStates getElevatorState() {
        return elevatorState;
    }
}
