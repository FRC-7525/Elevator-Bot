package frc.robot.subsystems.elevator;

import org.littletonrobotics.junction.AutoLog;
import org.littletonrobotics.junction.AutoLogOutput;

import edu.wpi.first.math.trajectory.TrapezoidProfile.State;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Voltage;

public interface ElevatorIO {
    @AutoLog
    public class ElevatorIOInputs {
        double elevatorPositionMeters = 0.0;
        double elevatorDistancePointGoalMeters = 0.0;
        double elevatorSpeedPointGoalMS = 0.0;
        double elevatorDistancePointMeters = 0.0;
        double elevatorDistanceSpeedPointMS = 0.0;
        boolean elevatorZeroed = false;
    }

    public class  ElevatorIOOutputs {
        @AutoLogOutput
        double leftMotorCurrent = 0.0;
        @AutoLogOutput
        double rightMotorCurrent = 0.0;
    }

    public default void updateInputs(ElevatorIOInputs inputs) {}
    
    public default void updateOutputs(ElevatorIOOutputs outputs) {}

    public default void setGoal(State goaLState) {}

    public default void runDistance() {}

    public default void runVolts(Measure<Voltage> volts) {}

    public default void zero() {}

    public default boolean elevatorZeroed() {
        return false;
    }

    public default boolean atSetpoint() {
        return false;
    }
}
