package frc.robot.pioneersLib;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class FillerSubsystem extends SubsystemBase {

    /**
     * Note: Subsystem name should be UNIQUE for each instance to avoid conflicts
     * this class should be used as a filler for sysid and other thigns that
     * require a subsystem object when we don't use the wpilib subsystem class
     * @param subsystemName
     */
    public FillerSubsystem(String subsystemName) {
        // Config names!
        setName(subsystemName);
        setSubsystem(subsystemName);
    }
}
