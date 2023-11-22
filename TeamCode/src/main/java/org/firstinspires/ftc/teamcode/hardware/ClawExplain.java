package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class ClawExplain {
    public Servo leftClaw;
    public Servo rightClaw;

    public ClawExplain(Servo leftClaw, Servo rightClaw){
        this.leftClaw = leftClaw;
        this.rightClaw = rightClaw;
    }

    public void setLeftClaw(double Pos){
        leftClaw.setPosition(Pos);
    }
    public void setRightClaw(double Pos){
        rightClaw.setPosition(Pos);
    }
    public double getRightClaw(){
        return rightClaw.getPosition();
    }
    public double getLeftClaw(){
        return leftClaw.getPosition();
    }
}
