package frc.robot.pioneersLib.CI;

import java.sql.Driver;
import java.util.concurrent.atomic.AtomicReference;

import org.littletonrobotics.junction.Logger;

import frc.robot.Robot;

import edu.wpi.first.hal.DriverStationJNI;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;

public class CrashCheck extends Robot {
    // Took a lot of stuff from 3173
    // lowkey atomics are peak
    private static final AtomicReference<CrashCheck> instance = new AtomicReference<>();
    private final AtomicReference<CrashCheckStates> currentState = new AtomicReference<>(CrashCheckStates.DISABLED);
    private final Timer timer = new Timer();

    // implementing subsystem states is nasty work
    private enum CrashCheckStates {
        DISABLED("DISABLED"),
        TELEOP("TELEOP"),
        AUTONOMOUS("AUTONOMOUS"),
        TEST("TEST");

        private String state;

        CrashCheckStates(String state) {
            this.state = state;
        }

        public String getStateString() {
            return state;
        }
    }

    // :( public bc needed for main
    public CrashCheck() {
        // Lalalalalala (Sets up important stuff)
        DriverStationSim.setEnabled(false);
        DriverStationSim.setAutonomous(false);
        DriverStationSim.setTest(false);
        driverStationConnected();
    }

    public static CrashCheck getInstance() {
        synchronized (CrashCheck.class) {
            if (instance.get() == null) {
                CrashCheck newInstance = new CrashCheck();
                instance.set(newInstance);
                return newInstance;
            }
            return instance.get();
        }
    }

    private void updateState() {
        if (DriverStation.isDisabled()) {
            currentState.set(CrashCheckStates.DISABLED);
        } else if (DriverStation.isTeleopEnabled()) {
            currentState.set(CrashCheckStates.TELEOP);
        } else if (DriverStation.isAutonomousEnabled()) {
            currentState.set(CrashCheckStates.AUTONOMOUS);
        } else if (DriverStation.isTestEnabled()) {
            currentState.set(CrashCheckStates.TEST);
        }
    }
    
    private void logState() {
        System.out.println("TestedRobotState " + currentState.get().getStateString());
    }

    private boolean hasErrors() {
        return (DriverStation.getMatchTime() < 0 || !DriverStation.isDSAttached() || !NetworkTableInstance.getDefault().isConnected());
    }
    

    private void runTest() {
        timer.start();
        if (timer.get() < 1) {
            currentState.set(CrashCheckStates.DISABLED);
            if (hasErrors()) {
                System.out.println("Crashes in Disabled");
                // System.exit(1);
            }        
        } else if (timer.get() < 2) {
            currentState.set(CrashCheckStates.TELEOP);
            if (hasErrors()) {
                System.out.println("Crashes in Teleop");
                // System.exit(1);
            }        
        } else if (timer.get() < 3) {
            currentState.set(CrashCheckStates.AUTONOMOUS);
            if (hasErrors()) {
                System.out.println("Crashes in Auto");
                // System.exit(1);
            }
        } else {
            System.exit(0);
        }
    }

    @Override
    public void loopFunc() {
        DriverStation.refreshData();
        updateState();
        runTest();

        if (currentState.get() == CrashCheckStates.DISABLED) {
            DriverStationJNI.observeUserProgramDisabled();
        } else if (currentState.get() == CrashCheckStates.TELEOP) {
            DriverStationJNI.observeUserProgramTeleop();
        } else if (currentState.get() == CrashCheckStates.AUTONOMOUS) {
            DriverStationJNI.observeUserProgramAutonomous();
        } else if (currentState.get() == CrashCheckStates.TEST) {
            DriverStationJNI.observeUserProgramTest();
        }

        logState();
        NetworkTableInstance.getDefault().flushLocal();
    }
}
