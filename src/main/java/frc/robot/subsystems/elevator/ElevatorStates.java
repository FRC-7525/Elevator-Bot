package frc.robot.subsystems.elevator;

import edu.wpi.first.math.trajectory.TrapezoidProfile.State;
import frc.robot.pioneersLib.subsystem.SubsystemStates;

public enum ElevatorStates implements SubsystemStates{
    IN("IN", new State(0, 0)),
    OUT("Out", new State(1.5, 0));

    private String stateString;
    private State endState;
    
    ElevatorStates(String stateString, State endState) {
        this.stateString = stateString;
        this.endState = endState;
    }

    public String getStateString() {
        return stateString;
    }

    public State getEndState() {
        return endState;
    }
}
