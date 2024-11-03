package frc.robot.pioneersLib.CI;

import java.util.concurrent.atomic.AtomicReference;

import org.littletonrobotics.junction.LoggedRobot;

import frc.robot.Robot;

import edu.wpi.first.hal.DriverStationJNI;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;

public class CrashCheck extends LoggedRobot {
    // Took a lot of stuff from 3173
    // lowkey atomics are peak
    private static final AtomicReference<CrashCheck> instance = new AtomicReference<>();
    private static Robot robot = new Robot();
    private final AtomicReference<CrashCheckStates> currentState = new AtomicReference<>(CrashCheckStates.DISABLED);
    private final Timer timer = new Timer();

    // implementing subsystem states is nasty work
    private enum CrashCheckStates {
        DISABLED("DISABLED", () -> {
            robot.disabledPeriodic();
        }, () -> {
            robot.disabledInit();
        }),
        TELEOP("TELEOP", () -> {
            robot.teleopPeriodic();
        }, () -> {
            robot.teleopInit();
        }),
        AUTONOMOUS("AUTONOMOUS", () -> {
            robot.autonomousPeriodic();
        }, () -> {
            robot.autonomousInit();
        }),
        TEST("TEST", () -> {
            robot.testPeriodic();
        }, () -> {
            robot.testInit();
        });

        private String state;
        private Runnable periodic;
        private Runnable init;

        CrashCheckStates(String state, Runnable periodic, Runnable init) {
            this.state = state;
            this.periodic = periodic;
            this.init = init;
        }

        public String getStateString() {
            return state;
        }

        public void periodic() {
            periodic.run();
        }

        public void init() {
            init.run();
        }
    }

    // :( public bc needed for main
    public CrashCheck() {
        // Lalalalalala (Sets up important stuff)
        DriverStationSim.setEnabled(false);
        DriverStationSim.setAutonomous(false);
        DriverStationSim.setTest(false);
        driverStationConnected();
        this.startCompetition();
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
        CrashCheckStates lastState = currentState.get();
        if (DriverStation.isDisabled()) {
            currentState.set(CrashCheckStates.DISABLED);
        } else if (DriverStation.isTeleopEnabled()) {
            currentState.set(CrashCheckStates.TELEOP);
        } else if (DriverStation.isAutonomousEnabled()) {
            currentState.set(CrashCheckStates.AUTONOMOUS);
        } else if (DriverStation.isTestEnabled()) {
            currentState.set(CrashCheckStates.TEST);
        }

        if (lastState != currentState.get()) {
            currentState.get().init();
        }
        currentState.get().periodic();
    }

    private void logState() {
        // System.out.println("TestedRobotState " + currentState.get().getStateString());
    }

    private boolean hasErrors() {  
        return false;
    }

    private void runTest() {
        timer.start();
        if (timer.get() < 1) {
            currentState.set(CrashCheckStates.DISABLED);
            if (hasErrors()) {
                // System.out.println("Crashes in Disabled");
                // System.exit(1);
            }
        } else if (timer.get() < 2) {
            currentState.set(CrashCheckStates.TELEOP);
            if (hasErrors()) {
                // System.out.println("Crashes in Teleop");
                // System.exit(1);
            }
        } else if (timer.get() < 3) {
            currentState.set(CrashCheckStates.AUTONOMOUS);
            if (hasErrors()) {
                // System.out.println("Crashes in Auto");
                // System.exit(1);
            }
        } else {
            System.exit(0);
            this.endCompetition();
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
