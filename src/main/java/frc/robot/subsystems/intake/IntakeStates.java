package frc.robot.subsystems.intake;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Velocity;
import frc.robot.pioneersLib.subsystem.SubsystemStates;

public enum IntakeStates implements SubsystemStates{
    INTAKE("Intake", RotationsPerSecond.of(1), Rotation2d.fromDegrees(0.0)),
    OUTTAKE("Outtake", RotationsPerSecond.of(1), Rotation2d.fromDegrees(0.0));

    IntakeStates(String stateString, Measure<Velocity<Angle>>  speedPoint, Rotation2d setPoint) {
        this.stateString = stateString;
        this.speedPoint = speedPoint;
        this.setPoint = setPoint;
    }

    String stateString;
    Measure<Velocity<Angle>>  speedPoint;
    Rotation2d setPoint;

    @Override
    public String getStateString() {
        return stateString;
    }

    public Measure<Velocity<Angle>>  getSpeedPoint() {
        return speedPoint;
    }

    public Rotation2d getSetPoint() {
        return setPoint;
    }
}

