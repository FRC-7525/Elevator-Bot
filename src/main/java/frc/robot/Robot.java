// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.NT4Publisher;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobotBase;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.pioneersLib.misc.CommandsUtil;
import frc.robot.subsystems.manager.Manager;

public class Robot extends LoggedRobot {

	private Manager managerSubsystem;

	@Override
	public void robotInit() {
		managerSubsystem = new Manager();

		// PLEASE NEVER COMMENT THIS
		DriverStation.silenceJoystickConnectionWarning(true);
		IterativeRobotBase.suppressExitWarning(true);

		Logger.addDataReceiver(new NT4Publisher());
		Logger.start();

		// Logs all running commands & unique commands (debugging for auto/sysId)
		CommandsUtil.logCommands();
	}

	@Override
	public void robotPeriodic() {
		managerSubsystem.periodic();

		// Nah bc why wasn't this getting called before
		// TODO: Subsystem periodics called twice bc custom subsystem extends
		// subsystembase for sysid & pplib, so either make wrapper classes for
		// interacting with sysId using & pplib using dummy subsystems or deal with it
		// ig? Optionally just do CommandScheduler.getInstance().disable() in teleop init & when sysId is off
		// would still double run in tele but whatever
		CommandScheduler.getInstance().run();
	}

	@Override
	public void autonomousInit() {
		CommandScheduler.getInstance().cancelAll();
	}

	@Override
	public void autonomousPeriodic() {
	}

	@Override
	public void teleopInit() {
		CommandScheduler.getInstance().cancelAll();
	}

	@Override
	public void teleopPeriodic() {
	}

	@Override
	public void disabledInit() {
	}

	@Override
	public void disabledPeriodic() {
	}

	@Override
	public void testInit() {
	}

	@Override
	public void testPeriodic() {
	}

	@Override
	public void simulationInit() {
	}

	@Override
	public void simulationPeriodic() {
	}
}
