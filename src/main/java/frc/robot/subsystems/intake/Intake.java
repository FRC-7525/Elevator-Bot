package frc.robot.subsystems.intake;

import frc.robot.pioneersLib.subsystem.Subsystem;
import frc.robot.subsystems.intake.IntakeIO.IntakeIOOutputs;

import org.littletonrobotics.junction.Logger;

import static frc.robot.Constants.Intake.*;

public class Intake extends Subsystem<IntakeStates> {

    private IntakeIO io;
    private IntakeIOInputsAutoLogged inputs;
    private IntakeIOOutputs outputs;

    public Intake(IntakeIO io) {
        super(SUBSYTEM_NAME, IntakeStates.INTAKE);
        // inputs = new IntakeIOInputsAutoLogged();
        outputs = new IntakeIOOutputs();

        this.io = io;
    }

    @Override
    public void runState() {
        Logger.processInputs(SUBSYTEM_NAME, inputs);
        io.updateInputs(inputs);
        io.updateOutputs(outputs);

        io.setPosition(getState().getSetPoint());
        io.setSpeed(getState().getSpeedPoint());
    }

    public void logPID(boolean logPID) {
        io.setLogPID(logPID);
    }

    public boolean atSetpoint() {
        return io.atSetpoint();
    }
}
