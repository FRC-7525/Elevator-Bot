package frc.robot.pioneersLib.subsystem;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecondPerSecond;
import static edu.wpi.first.units.Units.RPM;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Velocity;

/**
 * A container for the state of a subsystem, prevents invalid states with eroneous vaues.
 * If your state is not included in one of the factory methods you need to split up your subsystem
 * or tell mech to build a different robot 
 */
public record SubsystemStateContainer(
    String stateString,
    Measure<Distance> position,
    Rotation2d angularPosition,
    Measure<Velocity<Distance>> velocity,
    Measure<Velocity<Angle>> angularVelocity,
    Measure<Velocity<Angle>> secondaryVelocity,
    Measure<Velocity<Velocity<Distance>>> acceleration
) {
    /**
     * Primary constructor for all fields.
     * Validates the state to prevent invalid combinations.
     */
    public SubsystemStateContainer {
        validateState();
    }

    /**
     * Static factory method for defining a subsystem with pivot states
     */
    public static SubsystemStateContainer fromPivotStates(String stateString, Rotation2d angularPosition) {
        return new SubsystemStateContainer(stateString, null, angularPosition, null, null, null, null);
    }

    /** 
     * Static factory method for defining a subsystem with just elevator states
    */
    public static SubsystemStateContainer fromElevatorStates(String stateString, Measure<Distance> position, Measure<Velocity<Distance>> velocity) {
        return new SubsystemStateContainer(stateString, position, null, velocity, null, null, null);
    }

    /** 
     * Static factory method for defining a subsystem with just flywheel states
    */
    public static SubsystemStateContainer fromFlywheelStates(String stateString, Measure<Velocity<Angle>> velocity) {
        return new SubsystemStateContainer(stateString, null, null, null, velocity, null, null);
    }

    /** 
     * Static factory method for defining a subsystem with just flywheel states (uneven flywheels for spin)
    */
    public static SubsystemStateContainer fromUnevenFlywheelStates(String stateString, Measure<Velocity<Angle>> velocity, Measure<Velocity<Angle>> secondaryVelocity) {
        return new SubsystemStateContainer(stateString, null, null, null, velocity, secondaryVelocity, null);
    }

    /**
     * Static factory method for defining a subsystem with spinner and pivot states
     */
    public static SubsystemStateContainer fromPivotWithSpinnerStates(String stateString, Rotation2d angularPosition, Measure<Velocity<Angle>> angularVelocity) {
        return new SubsystemStateContainer(stateString, null, angularPosition, null, angularVelocity, null, null);
    }  
    
    /** 
     * Static factory method for defining a subsystem with just turret states (mech pls build this)
    */
    public static SubsystemStateContainer fromTurettStates(String stateString, Rotation2d angularPosition, Measure<Velocity<Angle>> angularVelocity) {
        return new SubsystemStateContainer(stateString, null, angularPosition, null, angularVelocity, null, null);
    }

    /**
     * No stupid states!!!
     */
    private void validateState() {
        if (position.in(Meters) > 10) {
            throw new IllegalArgumentException("U prob have conversion errors or L code (position > 10m)");
        }
        
        if (velocity.in(MetersPerSecond) > 40) {
            throw new IllegalArgumentException("U prob have conversion errors or L code (velocity > 40m/s)");
        }

        if (angularVelocity.in(RPM)/60 > 200 || secondaryVelocity.in(RPM)/60 > 200) {
            throw new IllegalArgumentException("What motor is going 200 RPS or why is gearing so low? fix ur robot or code");
        }

        if (acceleration.in(MetersPerSecondPerSecond) > 30) {
            throw new IllegalArgumentException("U prob have conversion errors or L code (acceleration > 30 m/s^2)");
        }
    }
}
