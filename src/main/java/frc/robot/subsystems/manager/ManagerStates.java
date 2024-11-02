package frc.robot.subsystems.manager;

import frc.robot.pioneersLib.subsystem.SubsystemStates;
import frc.robot.subsystems.elevator.ElevatorStates;
import frc.robot.subsystems.intake.IntakeStates;

public enum ManagerStates implements SubsystemStates{
    IDLE("IDLE", ElevatorStates.IN, IntakeStates.IDLE),
    INTAKING("INTAKING", ElevatorStates.IN, IntakeStates.INTAKE),
    RISING("RISING", ElevatorStates.OUT, IntakeStates.IDLE),
    PASSING("OUT", ElevatorStates.OUT, IntakeStates.OUTTAKE);

    private String stateString;
    private ElevatorStates elevatorState;
    private IntakeStates intakeState;
    
    ManagerStates(String stateString, ElevatorStates elevatorState, IntakeStates intakeState) {
        this.stateString = stateString;
        this.elevatorState = elevatorState;
        this.intakeState = intakeState;
    }

    public String getStateString() {
        return stateString;
    }

    public IntakeStates getIntakeState() {
        return intakeState;
    }

    public ElevatorStates getElevatorState() {
        return elevatorState;
    }
}
