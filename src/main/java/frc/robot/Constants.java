package frc.robot;

import frc.robot.pioneersLib.controlConstants.PIDConstants;

public final class Constants {

    public static final class Elevator {
        // Distance Points UGHHH WHY ISNT THE NEW UNTITS THING OUT RAHHHHHH
        public static final double DISTANCE_OUT_METERS = 0.75;
        public static final double DISTANCE_MEDIUM_METERS = 0.5;
        public static final double DISTANCE_IN_METERS = 0.0;

        public static final double DISTANCE_TOLERANCE_METERS = 0.05;

        public static final double ELEVATOR_MAX_SPEED_MPS = 0.2;

        public static final double ZEROING_CURRENT_LIMIT_AMPS = 10.0;

        public static final PIDConstants ELEVATOR_PID = new PIDConstants(0.0, 0.0, 0.0);
    }
}
