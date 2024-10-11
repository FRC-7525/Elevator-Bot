package frc.robot.subsystems.elevator;

import edu.wpi.first.math.trajectory.TrapezoidProfile.State;
import frc.robot.Constants;
import frc.robot.pioneersLib.subsystem.SubsystemStates;

public enum ElevatorStates implements SubsystemStates{
    IN("IN", Constants.Elevator.ELEVATOR_IN),
    OUT("Out", Constants.Elevator.ELEVATOR_OUT);

    private String stateString;
    private State endState;
    
    ElevatorStates(String stateString, State endState) {
        this.stateString = stateString;
        this.endState = endState;
    }

    public String getStateString() {
        return stateString;
    }

    public State getGoalState() {
        return endState;
    }
}
