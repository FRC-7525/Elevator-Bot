package frc.robot.subsystems.manager;

import frc.robot.pioneersLib.subsystem.SubsystemStates;

public enum ManagerStates implements SubsystemStates{
    IDLE("IDLE");

    private String stateString;
    
    ManagerStates(String stateString) {
        this.stateString = stateString;
    }

    public String getStateString() {
        return stateString;
    }
}
