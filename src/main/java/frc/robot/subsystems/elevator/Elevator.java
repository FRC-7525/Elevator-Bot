package frc.robot.subsystems.elevator;

import org.littletonrobotics.junction.Logger;

import frc.robot.Constants;
import frc.robot.pioneersLib.subsystem.Subsystem;
import frc.robot.subsystems.elevator.ElevatorIO.ElevatorIOOutputs;

public class Elevator extends Subsystem<ElevatorStates> {
    
    private ElevatorIO io;
    private ElevatorIOInputsAutoLogged inputs;
    private ElevatorIOOutputs outputs;

    public Elevator(ElevatorIO io) {
        super(Constants.Elevator.SUBSYTEM_NAME, ElevatorStates.IN);
        this.io = io;

        this.inputs = new ElevatorIOInputsAutoLogged();
        this.outputs = new ElevatorIOOutputs();
    }
    
    @Override
    public void runState() {
        // Update Logged Values
        Logger.processInputs(Constants.Elevator.SUBSYTEM_NAME, inputs);
        io.updateInputs(inputs);
        io.updateOutputs(outputs);
        
        // Run To Position
        io.setGoal(getState().getGoalState());
        io.runDistance();
    }
}
