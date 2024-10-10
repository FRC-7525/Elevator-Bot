package frc.robot.subsystems.elevator;

import frc.robot.pioneersLib.subsystem.SubsystemStates;

public enum ElevatorStates implements SubsystemStates{
    IN("IN");

    private String stateString;
    
    ElevatorStates(String stateString) {
        this.stateString = stateString;
    }

    public String getStateString() {
        return stateString;
    }
    
}
