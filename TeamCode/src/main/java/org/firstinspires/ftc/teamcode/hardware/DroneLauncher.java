package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.Servo;

public class DroneLauncher {
    public Servo DroneSpinner;
    public double SpinMotorPos;
    public DroneLauncher(Servo DroneSpinner){
        this.DroneSpinner = DroneSpinner;
    }
    public void SetDroneSpin(){
        DroneSpinner.setPosition(SpinMotorPos);
    }
    public void SetDronePos(double Pos){
        SpinMotorPos = Pos;
    }
    public double ReturnDronePos(){
        return SpinMotorPos;
    }

}
