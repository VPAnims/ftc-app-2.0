package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.ClawExplain;
import org.firstinspires.ftc.teamcode.hardware.DroneLauncher;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class DroneModule extends ControlModule{
    public double DroneSpinPos = 0.9;
    public DroneLauncher droneLauncher;
    public ControllerMap.ButtonEntry left_trigger;
    public DroneModule(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        droneLauncher = robot.droneLauncher;
        left_trigger = controllerMap.getButtonMap("DroneLaunch","gamepad2","left_trigger");
    }

    @Override
    public void update(Telemetry telemetry) {
        droneLauncher.SetDronePos(DroneSpinPos);
        if(left_trigger.edge() == -1){
            droneLauncher.SetDroneSpin();
        }
        telemetry.addData("Drone Position: ", droneLauncher.ReturnDronePos());

    }
}
