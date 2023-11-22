/*package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.broadcom.BroadcomColorSensor;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Intake {

    private final Servo claw;
    private final DistanceSensor claw_sensor;
    private boolean intaken = false;

    public Intake(Servo claw, DistanceSensor claw_sensor){
        this.claw = claw;
        this.claw_sensor = claw_sensor;
    }
  //  public void setClaw(double pos) {
    }

//    public double getDistance() {
        return claw_sensor.getDistance(DistanceUnit.MM);
    }

  //  public double getClawPosition() {
        return claw.getPosition();
    }
//
//}*/
package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Intake {

    private final DistanceSensor claw_sensor;
    private final Servo claw;
    private final Servo wrist;

    private boolean waiting_for_claw_close = false;

    private ElapsedTime timer = new ElapsedTime();

    public Intake(DistanceSensor claw_sensor, Servo claw, Servo wrist){
        this.claw_sensor = claw_sensor;
        this.claw = claw;
        this.wrist = wrist;
    }

    public void setClawPosition(double position){
        claw.setPosition(position);
    }

    public double getClawPosition() {
        return claw.getPosition();
    }

    public void setWristPosition(double position) {
        wrist.setPosition(position);
    }

    public double getWristPosition() {
        return wrist.getPosition();
    }

    public double getDistance() {
        return claw_sensor.getDistance(DistanceUnit.MM);
    }

    public boolean clawIsClosed() {
        if (getClawPosition() == 0.23) {
            waiting_for_claw_close = false;
        }

        if (getClawPosition() == 0.37 && !waiting_for_claw_close) {
            timer.reset();
            waiting_for_claw_close = true;
        }

        return timer.seconds() > 0.35 && waiting_for_claw_close;
    }

    public boolean intaken() {
        return getDistance() < 17 && clawIsClosed();
    }
}