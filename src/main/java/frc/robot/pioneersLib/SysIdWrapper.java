package frc.robot.pioneersLib;

import java.util.function.Consumer;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Voltage;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;

public class SysIdWrapper extends SubsystemBase {
    private SysIdRoutine routine;
    private SysIdRoutine.Config config;
    private SysIdRoutine.Mechanism mechanism;

    public SysIdWrapper(String subsystemName, Consumer<Measure<Voltage>> setVolts) {
        setName(subsystemName);
        setSubsystem(subsystemName);

        // TODO: Fix this trash code pls
        mechanism = new SysIdRoutine.Mechanism(setVolts, null, this);
        config = new SysIdRoutine.Config(null, null, null, (state) -> Logger.recordOutput(subsystemName + "/SysIdState", state.toString()));

        routine = new SysIdRoutine(config, mechanism);
    }

    public void runCommand() {
        // :/
        CommandScheduler.getInstance().schedule(routine.dynamic(Direction.kForward));
    }

    public Command getQualstaticForward() {
        return routine.quasistatic(SysIdRoutine.Direction.kForward);
    }

    public Command getQualstaticReverse() {
        return routine.quasistatic(SysIdRoutine.Direction.kReverse);
    }

    public Command getDynamicForward() {
        return routine.dynamic(SysIdRoutine.Direction.kForward);
    }

    public Command getDynamicReverse() {
        return routine.dynamic(SysIdRoutine.Direction.kReverse);
    }

    public void scheduleQuasistaticForward() {
        CommandScheduler.getInstance().schedule(routine.quasistatic(Direction.kForward));
    }
}
