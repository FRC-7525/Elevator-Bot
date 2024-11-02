package frc.robot.subsystems.intake;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Velocity;
import frc.robot.pioneersLib.subsystem.SubsystemStateContainer;
import frc.robot.pioneersLib.subsystem.SubsystemStates;

import static frc.robot.Constants.Intake.SetStates.*;

public enum IntakeStates implements SubsystemStates{
    INTAKE(sINTAKE),
    OUTTAKE(sOUTTAKE),
    IDLE(sIDLE);
    
    IntakeStates(SubsystemStateContainer state) {
        this.state = state;
    }

    private SubsystemStateContainer state;

    @Override
    public String getStateString() {
        return state.stateString();
    }

    public Measure<Velocity<Angle>> getSpeedPoint() {
        return state.angularVelocity();
    }

    public Rotation2d getSetPoint() {
        return state.angularPosition();
    }
}

