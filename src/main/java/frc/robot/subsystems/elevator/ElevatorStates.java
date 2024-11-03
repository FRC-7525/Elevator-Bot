package frc.robot.subsystems.elevator;

import edu.wpi.first.math.trajectory.TrapezoidProfile.State;
import frc.robot.pioneersLib.subsystem.SubsystemStates;
import frc.robot.pioneersLib.subsystem.SubsystemState;

import static edu.wpi.first.units.Units.Meter;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static frc.robot.Constants.Elevator.SetStates.*;


public enum ElevatorStates implements SubsystemStates{
    IN(ELEVATOR_IN),
    OUT(ELEVATOR_OUT);

    private String stateString;
    private SubsystemState state;
    
    ElevatorStates(SubsystemState state) {
        this.stateString = state.stateString();
        this.state = state;
    }

    @Override
    public String getStateString() {
        return stateString;
    }

    protected State getGoalState() {
        return new State(state.position().in(Meter), state.velocity().in(MetersPerSecond));
    }
}
