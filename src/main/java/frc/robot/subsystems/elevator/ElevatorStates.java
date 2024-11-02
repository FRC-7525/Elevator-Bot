package frc.robot.subsystems.elevator;

import edu.wpi.first.math.trajectory.TrapezoidProfile.State;
import frc.robot.pioneersLib.subsystem.SubsystemStates;

import static frc.robot.Constants.Elevator.SetStates.*;


public enum ElevatorStates implements SubsystemStates{
    IN("IN", ELEVATOR_IN),
    OUT("Out", ELEVATOR_OUT);

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
