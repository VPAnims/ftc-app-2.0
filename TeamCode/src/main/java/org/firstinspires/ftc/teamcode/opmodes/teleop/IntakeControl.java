//package org.firstinspires.ftc.teamcode.opmodes.teleop;
//
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.firstinspires.ftc.robotcore.external.Telemetry;
//import org.firstinspires.ftc.teamcode.hardware.ClawExplain;
//import org.firstinspires.ftc.teamcode.hardware.Intake;
//import org.firstinspires.ftc.teamcode.hardware.Lift;
//import org.firstinspires.ftc.teamcode.hardware.Robot;
//import org.firstinspires.ftc.teamcode.input.ControllerMap;
//
//public class IntakeControl extends ControlModule {
//    public ClawExplain Deposit;
//    public boolean isRightOpen = false;
//    public boolean isLeftOpen = false;
//    private ControllerMap.ButtonEntry LeftButton;
//    private ControllerMap.ButtonEntry RightButton;
//    private double openClawPos = 0;
//    private double closeClawPos = 0;
//
//    public IntakeControl(String name) {
//        super(name);
//    }
//
//    @Override
//    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {
//        this.intake = robot.intake;
//        LeftButton = controllerMap.getButtonMap("LeftButton","gamepad1","left_bumper");
//        RightButton = controllerMap.getButtonMap("RightButton","gamepad1","right_bumper");
//    }
//
//    @Override
//    public void update(Telemetry telemetry) {
//        if(LeftButton.edge() == -1){
//            if(isLeftOpen == true){
//                Deposit.setLeftClaw(closeClawPos);
//                isLeftOpen = false;
//            }
//            if(isLeftOpen == false){
//                Deposit.setRightClaw(openClawPos);
//                isLeftOpen = true;
//            }
//        }
//        if(RightButton.edge() == -1){
//            if(isRightOpen == true){
//                Deposit.setRightClaw(closeClawPos);
//                isRightOpen = false;
//            }
//            if(isRightOpen == false){
//                Deposit.setRightClaw(openClawPos);
//                isRightOpen = true;
//            }
//        }
//    }
//}