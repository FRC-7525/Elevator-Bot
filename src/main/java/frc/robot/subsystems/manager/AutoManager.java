package frc.robot.subsystems.manager;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.PrintCommand;

public class AutoManager {
    private SendableChooser<Command> autoChooser;


    public AutoManager(Manager manager) {
        // Elevator stuff
        

        autoChooser = new SendableChooser<>();

        autoChooser.setDefaultOption("Do Nothing", new PrintCommand("Does Literally Nothing"));

        // Elevator SysId
        // autoChooser.addOption("Elevator Qualstatic Forward", manager.getQualstaticForward());
        // autoChooser.addOption("Elevator Dynamic Reverse", elevatorSysId.getDynamicReverse());
        // autoChooser.addOption("Elevator Quasistatic Forward", elevatorSysId.getQualstaticForward());
        // autoChooser.addOption("Elevator Quasistatic Reverse", elevatorSysId.getQualstaticReverse());

        SmartDashboard.putData("Auto Chooser", autoChooser);
    }

    public Command getSelected() {
        return autoChooser.getSelected();
    }
}
