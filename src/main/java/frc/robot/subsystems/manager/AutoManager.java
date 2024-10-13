package frc.robot.subsystems.manager;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import frc.robot.pioneersLib.SysIdWrapper;

public class AutoManager {
    private SendableChooser<Command> autoChooser;

    private SysIdWrapper elevatorSysId;

    public AutoManager(Manager manager) {
        // Elevator stuff
        elevatorSysId = new SysIdWrapper("Elevator", (voltage) -> {
            manager.setElevatorVolts(voltage);
        });

        autoChooser = new SendableChooser<>();

        autoChooser.setDefaultOption("Do Nothing", new PrintCommand("Does Literally Nothing"));

        // Elevator SysId
        autoChooser.addOption("Elevator Dynamic Forward", elevatorSysId.getDynamicForward());
        autoChooser.addOption("Elevator Dynamic Reverse", elevatorSysId.getDynamicReverse());
        autoChooser.addOption("Elevator Quasistatic Forward", elevatorSysId.getQualstaticForward());
        autoChooser.addOption("Elevator Quasistatic Reverse", elevatorSysId.getQualstaticReverse());
    }

    public Command getSelected() {
        return autoChooser.getSelected();
    }
}
