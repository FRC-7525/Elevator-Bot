package frc.robot.pioneersLib.CI;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.hal.DriverStationJNI;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;
import frc.robot.Robot;

public class CrashCheck extends RobotBase {
    private static final AtomicReference<CrashCheck> instance = new AtomicReference<>();
    private final Robot robot;
    private final AtomicReference<CrashCheckStates> currentState = new AtomicReference<>(CrashCheckStates.DISABLED);
    private final Timer timer = new Timer();
    private final AtomicBoolean hasError = new AtomicBoolean(false);
    private String lastError = "";
    // Holy I hate this notation so much (Required as a flag)
    private boolean m_robotMainOverridden;

    private enum CrashCheckStates {
        DISABLED("DISABLED", () -> {
            getInstance().getRobot().robotPeriodic();
            getInstance().getRobot().disabledPeriodic();
        }, () -> {
            getInstance().getRobot().disabledInit();
        }, () -> {
            getInstance().getRobot().disabledExit();
        }),
        TELEOP("TELEOP", () -> {
            getInstance().getRobot().robotPeriodic();
            getInstance().getRobot().teleopPeriodic();
        }, () -> {
            getInstance().getRobot().teleopInit();
        }, () -> {
            getInstance().getRobot().teleopExit();
        }),
        AUTONOMOUS("AUTONOMOUS", () -> {
            getInstance().getRobot().robotPeriodic();
            getInstance().getRobot().autonomousPeriodic();
        }, () -> {
            getInstance().getRobot().autonomousInit();
        }, () -> {
            getInstance().getRobot().autonomousExit();
        }),
        TEST("TEST", () -> {
            getInstance().getRobot().robotPeriodic();
            getInstance().getRobot().testPeriodic();
        }, () -> {
            getInstance().getRobot().testInit();
        }, () -> {
            getInstance().getRobot().testExit();
        });

        private String state;
        private Runnable periodic;
        private Runnable init;
        private Runnable exit;

        CrashCheckStates(String state, Runnable periodic, Runnable init, Runnable exit) {
            this.state = state;
            this.periodic = periodic;
            this.init = init;
            this.exit = exit;
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

        public void exit() {
            exit.run();
        }
    }

    public CrashCheck() {
        this.robot = new Robot();
        DriverStationSim.setEnabled(false);
        DriverStationSim.setAutonomous(false);
        DriverStationSim.setTest(false);

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            handleError(throwable);
        });
    }

    public Robot getRobot() {
        return robot;
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
            if (lastState != null) {
                lastState.exit();
            }
            currentState.get().init();
        }
        currentState.get().periodic();
    }

    // L
    @Override
    public void startCompetition() {
        robot.robotInit();
        
        System.out.println("********** Robot program startup complete **********");
        
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                System.exit(1);
                return;
            }
            
            DriverStation.refreshData();
            if (hasErrors()) {
                System.out.println("Crashed In " + currentState.get().getStateString());
            }
            runTest();
            updateState();

            if (currentState.get() == CrashCheckStates.DISABLED) {
                DriverStationJNI.observeUserProgramDisabled();
            } else if (currentState.get() == CrashCheckStates.TELEOP) {
                DriverStationJNI.observeUserProgramTeleop();
            } else if (currentState.get() == CrashCheckStates.AUTONOMOUS) {
                DriverStationJNI.observeUserProgramAutonomous();
            } else if (currentState.get() == CrashCheckStates.TEST) {
                DriverStationJNI.observeUserProgramTest();
            }

            NetworkTableInstance.getDefault().flushLocal();
        }
    }

    @Override
    public void endCompetition() {
        m_robotMainOverridden = true;
    }

    private void runTest() {
        timer.start();
        if (timer.get() < 2) {
            currentState.set(CrashCheckStates.DISABLED);
        } else if (timer.get() < 4) {
            currentState.set(CrashCheckStates.TELEOP);
        } else if (timer.get() < 6) {
            currentState.set(CrashCheckStates.AUTONOMOUS);
        } else {
            System.exit(0);
            this.endCompetition();
        }
    }

    private void handleError(Throwable t) {
        hasError.set(true);
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Error in state ").append(currentState.get().getStateString())
               .append(": ").append(t.toString()).append("\n");
        
        // Add stack trace
        for (StackTraceElement element : t.getStackTrace()) {
            errorMsg.append("    at ").append(element.toString()).append("\n");
        }
        
        lastError = errorMsg.toString();
        System.err.println(lastError);
    }

    private boolean hasErrors() {
        if (hasError.get()) {
            System.err.println("Test failed in state " + currentState.get().getStateString());
            System.err.println("Last error:\n" + lastError);
            return true;
        }
        return false;
    }
}