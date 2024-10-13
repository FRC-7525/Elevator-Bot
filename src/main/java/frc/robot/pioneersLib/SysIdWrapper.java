package frc.robot.pioneersLib;

import java.util.function.Consumer;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Voltage;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;

public class SysIdWrapper extends SubsystemBase {

    private SysIdRoutine sysId;

    // Nah bc why they call this a consumer instead of a eating function or something
    /**
     * Create a SysIdWrapper so u don't have to extend the subsystem base class :)
     * @param subsystemName
     * @param runVolts
     */
    public SysIdWrapper(String subsystemName, Consumer<Measure<Voltage>> runVolts) {
        sysId = new SysIdRoutine(
                new SysIdRoutine.Config(
                        null,
                        null,
                        null,
                        (state) -> Logger.recordOutput(subsystemName + "/SysIdState", state.toString())),
                new SysIdRoutine.Mechanism(
                        (voltage) -> {
                            runVolts.accept(voltage);
                        },
                        null,
                        this));
    }

    /**
     * Get the quasistatic forward command
     */
    public Command getQualstaticForward() {
        return sysId.quasistatic(Direction.kForward);
    }

    /**
     * Get the quasistatic reverse command
     */
    public Command getQualstaticReverse() {
        return sysId.quasistatic(Direction.kReverse);
    }

    /**
     * Get the dynamic forward command
     */
    public Command getDynamicForward() {
        return sysId.dynamic(Direction.kForward);
    }
    
    /**
     * Get the dynamic reverse command
     */
    public Command getDynamicReverse() {
        return sysId.dynamic(Direction.kReverse);
    }
}