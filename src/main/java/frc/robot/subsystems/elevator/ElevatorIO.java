package frc.robot.subsystems.elevator;

import org.littletonrobotics.junction.AutoLog;
import org.littletonrobotics.junction.AutoLogOutput;

public interface ElevatorIO {
    @AutoLog
    public class ElevatorIOInputs {
        double elevatorPositionMeters = 0.0;

        double elevatorDistancePointGoalMeters = 0.0;
        double elevatorSpeedPointGoalMS = 0.0;
        double elevatorDistancePointMeters = 0.0;
        double elevatorDistanceSpeedPointMS = 0.0;

    }

    public class  ElevatorIOOutputs {
        @AutoLogOutput
        double leftMotorCurrent = 0.0;
        @AutoLogOutput
        double rightMotorCurrent = 0.0;
    }

    public default void updateInputs(ElevatorIOInputs inputs) {}
    
    public default void updateOutputs(ElevatorIOOutputs outputs) {}

    public default void setDistance(double distancePointGoalMeters) {}

    public default boolean atSetpoint() {
        return false;
    }
}
