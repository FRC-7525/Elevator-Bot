// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.NT4Publisher;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.manager.Manager;

public class Robot extends LoggedRobot {

	private Manager managerSubsystem;
	private CommandScheduler scheduler;

	@Override
	public void robotInit() {
		managerSubsystem = new Manager();
		scheduler = CommandScheduler.getInstance();

		// PLEASE NEVER COMMENT THIS
		DriverStation.silenceJoystickConnectionWarning(true);

		Logger.addDataReceiver(new NT4Publisher());
		Logger.start();
	}

	@Override
	public void robotPeriodic() {
		managerSubsystem.periodic();
	}

	@Override
	public void autonomousInit() {
		scheduler.schedule(managerSubsystem.getAutoCommand());
	}

	@Override
	public void autonomousPeriodic() {
	}

	@Override
	public void teleopInit() {
		// I hate command based!!!
		scheduler.cancelAll();
		managerSubsystem.exitSysId();
	}

	@Override
	public void teleopPeriodic() {
	}

	@Override
	public void disabledInit() {
		// Set ur sysId routine to "none" once you disable (so when you stop running the test)
		// managerSubsystem.resetSysIdState();
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
