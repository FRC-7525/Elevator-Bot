package frc.robot.subsystems.intake;

import org.littletonrobotics.junction.AutoLog;
import org.littletonrobotics.junction.AutoLogOutput;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Velocity;
import edu.wpi.first.units.Voltage;

public interface IntakeIO {
    @AutoLog
    public class IntakeIOInputs {
        double positionDeg = 0.0;
        double setPointDeg = 0.0;
        double speedPointRPS = 0.0;
        double speedRPS = 0.0;
        double spinnerVolts = 0.0;
        double positionVolts = 0.0;
    }

    public class IntakeIOOutputs {
        @AutoLogOutput(key = "Intake/Spinner Current")
        double spinnerCurrent = 0.0;
        @AutoLogOutput(key = "Intake/Position Current")
        double positionCurrent = 0.0;
    }

    public default void updateInputs(IntakeIOInputs inputs) {}
    
    public default void updateOutputs(IntakeIOOutputs outputs) {}

    public default void runSpinnerVolts(Measure<Voltage> volts) {}

    public default void runPostionVolts(Measure<Voltage> volts) {}

    public default void setPosition(Rotation2d position) {}

    public default void setSpeed(Measure<Velocity<Angle>> speed) {}

    public default void setLogPID(boolean logPID) {}

    public default boolean atSetpoint() {
        return false;
    }

    public default boolean atSpeedPoint() {
        return false;
    }
}
