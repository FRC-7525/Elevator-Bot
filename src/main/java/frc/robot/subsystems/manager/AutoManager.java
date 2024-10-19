package frc.robot.subsystems.manager;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.PrintCommand;

public class AutoManager {
    private SendableChooser<Command> autoChooser;


    public AutoManager(Manager manager) {
        autoChooser = new SendableChooser<>();

        autoChooser.setDefaultOption("Do Nothing", new PrintCommand("Does Literally Nothing"));

        SmartDashboard.putData("Auto Chooser", autoChooser);
    }

    public Command getSelected() {
        System.out.println("Command for auto scheduled");
        return autoChooser.getSelected();
    }
}
