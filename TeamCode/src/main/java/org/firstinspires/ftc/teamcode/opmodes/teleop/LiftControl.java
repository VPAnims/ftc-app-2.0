package org.firstinspires.ftc.teamcode.opmodes.teleop;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

public class LiftControl extends ControlModule{
    private Lift lift;
    private double Target;
    private double Low = 20;
    private double Medium = 30;
    private double High = 40;
    private double MaxPosition = 1;
    private double currentPosition = 0;
    private double Position = 0;
    private ControllerMap.ButtonEntry A;
    private ControllerMap.ButtonEntry B;
    private ControllerMap.ButtonEntry Y;
    private ControllerMap.AxisEntry ax_horizontal_left_x;

    public LiftControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
        A = controllerMap.getButtonMap("lowPos", "gamepad1", "a");
        B = controllerMap.getButtonMap("mediumPos", "gamepad1", "b");
        Y = controllerMap.getButtonMap("highPos", "gamepad1", "y");
        ax_horizontal_left_x = controllerMap.getAxisMap("fineAdjust", "gamepad2", "left_stick_x");
    }

    @Override
    public void update(Telemetry telemetry) {
        lift.setLiftPos(Position);
        if (A.edge() == -1) {
            lift.setTarget(Low);
        }
        if (B.edge() == -1) {
            lift.setTarget(Medium);
        }
        if (Y.edge() == -1) {
            lift.setTarget(High);
        }

        currentPosition += (ax_horizontal_left_x.get() * 110);
        if (currentPosition > MaxPosition) currentPosition = MaxPosition;
        if (currentPosition < 0) currentPosition = 0;
        lift.setTarget(currentPosition);

        Position = lift.getTarget();
        lift.setLiftPos(Position);

        telemetry.addData("Lift Left Position: ", lift.getLeftPos());
        telemetry.addData("Lift Right Position: ", lift.getRightPos());

    }
}
