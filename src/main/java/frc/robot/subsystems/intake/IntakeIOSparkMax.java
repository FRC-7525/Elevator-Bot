package frc.robot.subsystems.intake;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Velocity;
import edu.wpi.first.units.Voltage;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static frc.robot.Constants.Intake.*;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static frc.robot.Constants.Conversions.*;
import static frc.robot.Constants.Intake.SparkMax.*;

public class IntakeIOSparkMax implements IntakeIO {
    
    private CANSparkMax spinnerMotor;
    private CANSparkMax positionMotor;

    private SimpleMotorFeedforward speedFF;
    private PIDController spinnerPID;
    private PIDController positionPID;

    private boolean logPID;

    public IntakeIOSparkMax() {
        spinnerMotor = new CANSparkMax(SPINNER_ID, CANSparkMax.MotorType.kBrushless);
        positionMotor = new CANSparkMax(POSITION_ID, CANSparkMax.MotorType.kBrushless);

        speedFF = new SimpleMotorFeedforward(SPINNER_FF.kS, SPINNER_FF.kV, SPINNER_FF.kA);
        spinnerPID = new PIDController(SPINNER_PID.kP, SPINNER_PID.kI, SPINNER_PID.kD);
        positionPID = new PIDController(POSITION_PID.kP, POSITION_PID.kI, POSITION_PID.kD);
        logPID = false;

        spinnerMotor.setIdleMode(SPINN_IDLE_MODE);
        positionMotor.setIdleMode(POSITION_IDLE_MODE);
        
        spinnerMotor.getEncoder().setVelocityConversionFactor(1);
        spinnerMotor.getEncoder().setPositionConversionFactor(1);
        positionMotor.getEncoder().setVelocityConversionFactor(1);
        positionMotor.getEncoder().setPositionConversionFactor(ROTATIONS_TO_DEGREES);

        spinnerMotor.burnFlash();
        positionMotor.burnFlash();
    }

    @Override
    public void updateInputs(IntakeIOInputs inputs) {
        if (logPID) {
            SmartDashboard.putData(positionPID);
            SmartDashboard.putData(spinnerPID); 
        }
       
        inputs.positionDeg = positionMotor.getEncoder().getPosition();
        inputs.setPointDeg = positionPID.getSetpoint();
        inputs.speedPointRPS = spinnerPID.getSetpoint();
        inputs.speedRPS = spinnerMotor.getEncoder().getVelocity();
        inputs.spinnerVolts = spinnerMotor.getAppliedOutput();
        inputs.positionVolts = positionMotor.getAppliedOutput();
    }
    
    @Override
    public void updateOutputs(IntakeIOOutputs outputs) {
        outputs.spinnerCurrent = spinnerMotor.getOutputCurrent();
        outputs.positionCurrent = positionMotor.getOutputCurrent();
    }

    @Override
    public void runSpinnerVolts(Measure<Voltage> volts) {
        spinnerMotor.set(volts.magnitude());
    }

    @Override
    public void runPostionVolts(Measure<Voltage> volts) {
        positionMotor.set(volts.magnitude());
    }

    @Override
    public void setPosition(Rotation2d position) {
        if (position.getDegrees() != positionPID.getSetpoint()) positionPID.setSetpoint(position.getDegrees());
        positionMotor.set(positionPID.calculate(positionMotor.getEncoder().getPosition()));
    }

    @Override
    public void setSpeed(Measure<Velocity<Angle>> speed) {
        positionPID.setSetpoint(speed.in(RotationsPerSecond));
        spinnerMotor.set(speedFF.calculate(spinnerPID.calculate(spinnerMotor.getEncoder().getVelocity(), speed.in(RotationsPerSecond))) + positionPID.calculate(spinnerMotor.getEncoder().getVelocity()));
    }

    @Override
    public void setLogPID(boolean logPID) {
        this.logPID = logPID;
    }

    @Override
    public boolean atSetpoint() {
        return Math.abs(positionPID.getSetpoint() - positionMotor.getEncoder().getPosition()) < POSITON_TOLERANCE.getDegrees();
    }

    @Override
    public boolean atSpeedPoint() {
        return Math.abs(spinnerPID.getSetpoint() - spinnerMotor.getEncoder().getVelocity()) < VELOCITY_TOLERANCE.in(MetersPerSecond);
    }
}
