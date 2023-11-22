package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Arm;
import org.firstinspires.ftc.teamcode.hardware.Horizontal;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.navigation.IntakeStates;
import org.firstinspires.ftc.teamcode.hardware.navigation.LiftStates;
import org.firstinspires.ftc.teamcode.hardware.navigation.Modes;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.hardware.navigation.TPMotionProfile;
import org.firstinspires.ftc.teamcode.input.ControllerMap;

@Config

public class RobotControl extends ControlModule{

    private Lift lift;
    private Intake intake;
    private Horizontal horizontal;
    private Arm arm;

    private ElapsedTime intakeTimer;
    private boolean intakeTimerReset = false;
    private ElapsedTime liftTimer;
    private boolean liftTimerReset = false;
    private ElapsedTime loop;

    private final ElapsedTime game_timer = new ElapsedTime();

    private double lift_accel = 2.7; //bigger is faster accel
    private double lift_decel = 0.003; //bigger is faster decel
    private TPMotionProfile lift_trapezoid;

    private double lift_last_height;

    private boolean dumped = false;

    private ControllerMap.ButtonEntry y_button;
    private ControllerMap.ButtonEntry b_button;
    private ControllerMap.ButtonEntry a_button;
    private ControllerMap.ButtonEntry x_button;
    private ControllerMap.ButtonEntry dpad_left;

    private ControllerMap.ButtonEntry sense;
    private ControllerMap.ButtonEntry sense2;
    private ControllerMap.ButtonEntry dump;

    private ControllerMap.ButtonEntry rec_right_bumper;
    private ControllerMap.AxisEntry rec_right_trigger;
    private ControllerMap.ButtonEntry rec_left_bumper;
    private ControllerMap.AxisEntry rec_left_trigger;

    private ControllerMap.ButtonEntry switchFast;
    private ControllerMap.ButtonEntry switchGround;
    private ControllerMap.ButtonEntry switchCircuit;

    private ControllerMap.AxisEntry ax_horizontal_left_x;
    private ControllerMap.AxisEntry ax_lift_left_y;
    private ControllerMap.AxisEntry ax_arm_right_y;

    private ControllerMap.AxisEntry servo_kill;

    private LiftStates stateForLift;
    private IntakeStates stateForIntake;
    private Modes mode;

    private double LIFTDOWNPOS = 0;
    private double LIFTDOWNPOSFAST = 35;
    private double LIFTDOWNPOSLOWGOALS = 35;
    private double LIFTLOWPOS = 135;
    private double LIFTMIDPOS = 450;
    private double LIFTHIGHPOS = 750;
    private double LIFTHIGHPOSFAST = 730;

    private double DEPOSITLOW = 0.38;
    private double DEPOSITLOW2 = 0.5;
    private double DEPOSITLOW3 = 0;
    public static double LOWTIMEOUT = 1.3;

    private double DEPOSITMID = 0.40;
    private double DEPOSITHIGH = 0.40;

    private double DEPOSITHIGHFAST = 0.42;

    public static double DEPOSITTRANSFER = 0.10;
    public static double DEPOSITTRANSFER2 = 0.097;
    private double DEPOSITTRANSFERFAST = 0.10;
    private double DEPOSITTRANSFERFAST2 = 0.097;

    private double DEPOSITLIFT = 0.38;
    private double DEPOSITLIFTFAST = 0.35;

    public static double ARMCOMPLETEDOWNPOS = -123;
    private double ARMMIDPOS = -36;
    private double ARMMIDPOS2 = -35; //used while the horiz slide is retracting
    public static double ARMMIDPOS3 = -12;
    public static double ARMHIGHPOS = -9; //transfer position

    private double ARMLOWGOAL = -76;
    private double ARMGROUNDGOAL = -120;

    private double WRISTLOOKINGFORCONE = 0.021;
    private double WRISTTRANSFER = 0.692;

    private double MAXEXTENDEDHORIZ = 1440;
    private double FASTMODEHORIZCONST = 410;
    private double FASTMODEHORIZ = 410;
    private double ADJUSTHORIZ = 0;
    private double HORIZRETRACTED = 0;
    public static double HORIZ_KICK = 45;

    public static double PICKUP_TIMER = 0.07;

    public static double HORIZ_KP = 0.01;
    public static double HORIZ_KP_FINE = 0.005;

    private double horiz_kp_var = HORIZ_KP; //varies between the two values above

    public static double CLAWOPENPOS = 0.35;
    private double CLAWCLOSEPOS = 0.73;

    public static double CLAW_SENSOR_RANGE = 37;
    public static double POLE_SENSOR_RANGE = 18;

    private double LATCHOPENPOS = 0.8;
    private double LATCHGRABPOS = 0.075;
    private double LATCHTRANSFERPOS = 0.55;

    public static double LATCHDELAY = 0.2;

    private PID arm_PID;
    private PID horiz_PID;
    private PID lift_PID;
    private boolean GroundLow;

    private boolean waitForRetract = false;

    public static double LIFT_KP = 0.015;

    private Gamepad gamepad1;
    private Gamepad gamepad2;

    private boolean rumble = true;
    private boolean end_game = false;
    private boolean ten_seconds_left = false;

    private boolean stop_setting_arm_position = false;
    private boolean game_timer_reset = false;
    private boolean initialize = false;

    public RobotControl(String name) {
        super(name);
    }

    @Override
    public void initialize(Robot robot, ControllerMap controllerMap, ControlMgr manager) {

        intakeTimer = new ElapsedTime();
        liftTimer = new ElapsedTime();
        loop = new ElapsedTime();

        lift_trapezoid = new TPMotionProfile(lift_accel, lift_decel, 0.4, 1);

        this.lift = robot.lift;
        this.intake = robot.intake;
        this.arm = robot.arm;
        this.horizontal = robot.horizontal;

        gamepad1 = controllerMap.gamepad1;
        gamepad2 = controllerMap.gamepad2;

        sense = controllerMap.getButtonMap("senseCone", "gamepad1", "right_bumper");
        sense2 = controllerMap.getButtonMap("senseCone2", "gamepad2", "right_bumper");
        dump = controllerMap.getButtonMap("dump", "gamepad1", "left_bumper");

        y_button = controllerMap.getButtonMap("lift:high","gamepad1","y");
        b_button = controllerMap.getButtonMap("lift:mid","gamepad1","b");
        a_button = controllerMap.getButtonMap("lift:low","gamepad1","a");
        x_button = controllerMap.getButtonMap("lift:ground","gamepad1","x");

        rec_right_bumper = controllerMap.getButtonMap("lift:reset_encoder_rb","gamepad2","right_bumper");
        rec_right_trigger = controllerMap.getAxisMap("lift:reset_encoder_rt","gamepad2","right_trigger");
        rec_left_bumper = controllerMap.getButtonMap("lift:reset_encoder_lb","gamepad2","left_bumper");
        rec_left_trigger = controllerMap.getAxisMap("lift:reset_encoder_lt","gamepad2","left_trigger");

        servo_kill = controllerMap.getAxisMap("lift:servo_kill","gamepad1","right_trigger");

        switchFast = controllerMap.getButtonMap("switchFast", "gamepad2", "dpad_up");
        switchGround = controllerMap.getButtonMap("switchGround", "gamepad2", "dpad_down");
        switchCircuit = controllerMap.getButtonMap("switchCircuit", "gamepad2", "dpad_right");
        dpad_left = controllerMap.getButtonMap("claw:close_pos","gamepad2","dpad_left");

        ax_horizontal_left_x = controllerMap.getAxisMap("horizontal:left_x", "gamepad2", "left_stick_x"); //finetuning
        ax_lift_left_y = controllerMap.getAxisMap("lift:left_y", "gamepad2", "left_stick_y");
        ax_arm_right_y = controllerMap.getAxisMap("arm:right_y", "gamepad2", "right_stick_y");

        stateForLift = LiftStates.LiftDown;
        stateForIntake = IntakeStates.DrivingAround;
        mode = Modes.Circuit;


        GroundLow = false;

        //horizontal.setHorizTarget(0); //used for run to position
    }

    @Override
    public void init_loop(Telemetry telemetry) {
        super.init_loop(telemetry);

        if (!initialize) {
            lift.setHolderPosition(DEPOSITTRANSFER);
            arm.setPosition(ARMHIGHPOS);
            lift.setPower(-0.2);
            horizontal.setPower(-0.3);
            initialize = true;
        }

        arm.update();
        arm.resetEncoders();

        lift.resetEncoders();
        horizontal.resetEncoders();

        if(lift.getCurrentAmps() > 1.5){
            lift.resetEncoders();
            lift.setPower(0);
        }
        if(horizontal.getCurrentAmps() > 3){
            horizontal.resetEncoders();
            horizontal.setPower(0);
        }

        if (lift.getPower() == 0 && horizontal.getPower() == 0) {
            telemetry.addData("READY", "GO");
            if (rumble) {
                gamepad1.rumble(1000);
                gamepad2.rumble(1000);
                rumble = false;
            }
        }

        telemetry.addData("Lift Amps", lift.getCurrentAmps());
        telemetry.addData("Horizonal Amps", horizontal.getCurrentAmps());
    }

    @Override
    public void update(Telemetry telemetry) {

        if (!game_timer_reset) {
            game_timer.reset();
            game_timer_reset = true;
        }

        loop.reset();

        arm.update();
        lift.update();
        horizontal.updatePosition();

        if (dpad_left.edge() == -1) {
            if (CLAWCLOSEPOS == 0.065) {
                CLAWCLOSEPOS = 0.09;
            }
            else if (CLAWCLOSEPOS == 0.09) {
                CLAWCLOSEPOS = 0.065;
            }
        }

        if (game_timer.seconds() > 120 && !end_game) {
            rumble = true;
            end_game = true;
        }

        if (game_timer.seconds() > 140 && !ten_seconds_left) {
            rumble = true;
            ten_seconds_left = true;
        }

        if (rumble) {
            gamepad1.rumble(500);
            gamepad2.rumble(500);
            rumble = false;
        }

        horiz_PID = new PID(horiz_kp_var, 0, 0, 0, 0, 0);
        lift_PID = new PID(LIFT_KP, 0, 0, 0.03, 0, 0);

        lift_trapezoid.setSlopes(lift_accel, lift_decel);

        if (mode == Modes.Circuit) {
            if ((a_button.edge() == -1 || b_button.edge() == -1 || y_button.edge() == -1) && !(stateForIntake == IntakeStates.Transfer && intakeTimer.seconds() < 0.2) && stateForIntake != IntakeStates.PickingConeUp) {
                stateForLift = LiftStates.LiftUp;
                lift.setLatchPosition(LATCHGRABPOS);
            }
        }
        //lift
        switch (stateForLift) {
            case LiftDown:
                lift_trapezoid.updateMotionProfile(lift.getLiftTarget(), false, true);
                if (mode == Modes.Fast) {
                    lift.setLiftTarget(LIFTDOWNPOSFAST);
                } else {
                    lift.setLiftTarget(LIFTDOWNPOS);
                }

                if (lift.getCurrentPosition() < 200 && dumped && (stateForIntake == IntakeStates.DrivingAround || stateForIntake == IntakeStates.LookingForCone || stateForIntake == IntakeStates.Ground)) {
                    if (lift_last_height == LIFTHIGHPOS || lift_last_height == LIFTMIDPOS || lift_last_height == LIFTHIGHPOSFAST) {
                        if (mode == Modes.Fast) {
                            lift.setHolderPosition(DEPOSITTRANSFERFAST);
                        } else {
                            lift.setHolderPosition(DEPOSITTRANSFER);
                        }
                        lift.setLatchPosition(LATCHOPENPOS);
                        waitForRetract = false;
                        dumped = false;
                    }
                }
                liftTimerReset = false;
                if (lift_last_height == LIFTLOWPOS) {
                    lift.setLiftTarget(LIFTDOWNPOSLOWGOALS);
                    if (dumped) {
                        if (liftTimer.seconds() > 0.25) {
                            lift.setHolderPosition(DEPOSITLOW3);
                            lift.setLatchPosition(LATCHOPENPOS);
                            dumped = false;
                        }
                    }
                    if (liftTimer.seconds() > LOWTIMEOUT - 0.2 && liftTimer.seconds() < LOWTIMEOUT) {
                        lift.setHolderPosition(DEPOSITTRANSFER);
                        lift.setLatchPosition(LATCHTRANSFERPOS);
                        waitForRetract = false;
                        lift.setLiftTarget(LIFTDOWNPOS);
                    }
                }
//                if (lift_last_height == LIFTHIGHPOS || lift_last_height == LIFTMIDPOS || lift_last_height == LIFTHIGHPOSFAST) {
//                    if (dumped) {
//                        if (liftTimer.seconds() > LATCHDELAY) {
//                            if (stateForIntake == IntakeStates.PickingConeUp) {
//                                lift.setLatchPosition(LATCHTRANSFERPOS);
//                            } else {
//                                lift.setLatchPosition(LATCHOPENPOS);
//                            }
//                        }
//                    }
//                }
                break;

            case LiftUp:
                if (mode == Modes.Fast) {
                    lift.setHolderPosition(DEPOSITLIFTFAST);
                } else {
                    lift.setHolderPosition(DEPOSITLIFT);
                }

                if (mode == Modes.Circuit) {
                    if (a_button.edge() == -1) {
                        lift.setLiftTarget(LIFTLOWPOS);
                    }

                    if (b_button.edge() == -1) {
                        lift.setLiftTarget(LIFTMIDPOS);
                    }

                    if (y_button.edge() == -1) {
                        lift.setLiftTarget(LIFTHIGHPOS);
                    }

                } else if (mode == Modes.Fast){
                    lift.setLiftTarget(LIFTHIGHPOSFAST);
                    if (Math.abs(lift.getCurrentPosition() - lift.getLiftTarget()) <= 30) {
                        stateForLift = LiftStates.Dump;
                    }
                }
                lift_trapezoid.updateMotionProfile(lift.getLiftTarget(), true, true);
                break;

            case Dump:
                if (lift.getLiftTarget() == LIFTMIDPOS) {
                    lift.setHolderPosition(DEPOSITMID);
                }

                if (mode == Modes.Circuit) {
                    lift.setHolderPosition(DEPOSITHIGH);
                } else if (mode == Modes.Fast) {
                    lift.setHolderPosition(DEPOSITHIGHFAST);
                }

                if (!liftTimerReset) {
                    liftTimer.reset();
                    liftTimerReset = true;
                }

                lift_last_height = lift.getLiftTarget();

                if (lift.getLiftTarget() == LIFTLOWPOS) {
                    lift.setHolderPosition(DEPOSITLOW);
                    if (liftTimer.seconds() > 0.1) {
                        dumped = true;
                        liftTimer.reset();
                        stateForLift = LiftStates.LiftDown;
                    }
                } else {
                    dumped = true;
                    stateForLift = LiftStates.LiftDown;
                }
                break;
        }
        //intake
        switch (stateForIntake) {
            case LookingForCone:
                if (!stop_setting_arm_position) {
                    arm.setPosition(ARMCOMPLETEDOWNPOS);
                    stop_setting_arm_position = true;
                }
                intake.setWristPosition(WRISTLOOKINGFORCONE);
                intake.setClawPosition(CLAWOPENPOS);
                if (mode == Modes.Fast) {
                    if (!intakeTimerReset) {
                        intakeTimer.reset();
                        intakeTimerReset = true;
                    }
                    if (intakeTimer.seconds() > 0.1) {
                        horizontal.setHorizTarget(FASTMODEHORIZ);
                    }
                }
                if ((intake.getDistance() <= CLAW_SENSOR_RANGE || sense.edge() == -1 || sense2.edge() == -1) && Math.abs(ARMCOMPLETEDOWNPOS - arm.getCurrentEncoderPosition()) < 50) {
                    stateForLift = LiftStates.LiftDown;
                    intakeTimerReset = false;
                    if (mode == Modes.Ground) {
                        GroundLow = true;
                        intake.setClawPosition(CLAWCLOSEPOS);
                        stateForIntake = IntakeStates.GroundDrivingAround;
                    } else {
                        stateForIntake = IntakeStates.PickingConeUp;
                    }
                }
                break;

            case PickingConeUp:
                intake.setClawPosition(CLAWCLOSEPOS);
                lift.setLatchPosition(LATCHTRANSFERPOS);
                horizontal.setHorizTarget(Range.clip(horizontal.getHorizTarget() + HORIZ_KICK, 0,MAXEXTENDEDHORIZ));
                if (!intakeTimerReset) {
                    intakeTimer.reset();
                    intakeTimerReset = true;
                }
                if (lift.getCurrentPosition() < 40) {
                    if (intakeTimer.seconds() > 0.03 + PICKUP_TIMER) {
                        intake.setWristPosition(WRISTTRANSFER);
                    }
                    if (intakeTimer.seconds() > 0.2 + PICKUP_TIMER) {
                        if (horizontal.getCurrentPosition() > 50) {
                            arm.setPosition(ARMMIDPOS2);
                        } else {
                            arm.setPosition(ARMHIGHPOS);
                        }
                        horizontal.setHorizTarget(HORIZRETRACTED);
                        if (mode == Modes.Fast) {
                            lift.setHolderPosition(DEPOSITTRANSFERFAST);
                        } else {
                            lift.setHolderPosition(DEPOSITTRANSFER);
                        }
                    }
                }
                if (lift.getCurrentPosition() < 40 && horizontal.getCurrentPosition() < 35) {
                    if (((mode == Modes.Fast && arm.getCurrentEncoderPosition() > -34) || (mode == Modes.Circuit && arm.getCurrentEncoderPosition() > -34)) && intakeTimer.seconds() > 0.2) {
                        stateForIntake = IntakeStates.Transfer;
                        intakeTimerReset = false;
                    }
                }
                break;

            case Transfer:
                if (!intakeTimerReset) {
                    intakeTimer.reset();
                    intakeTimerReset = true;
                }

                if (mode == Modes.Fast) {
                    FASTMODEHORIZ = FASTMODEHORIZCONST;
                    if (intakeTimer.seconds() > 0.12) { //used to be 0.2 and 0.25
                        intake.setClawPosition(CLAWOPENPOS);
                        lift.setLatchPosition(LATCHGRABPOS);
                        lift.setHolderPosition(DEPOSITTRANSFERFAST2);
                    }
                    if (intakeTimer.seconds() > 0.05) {
                        stateForLift = LiftStates.LiftUp;
                    }
                    if (intakeTimer.seconds() > 0.25) {
                        stateForIntake = IntakeStates.LookingForCone;
                        stop_setting_arm_position = false;
                        intakeTimerReset = false;
                    }
                }
                if (mode == Modes.Circuit) {
                    if (intakeTimer.seconds() > 0.08) {
                        lift.setLatchPosition(LATCHGRABPOS);
                    }
                    if (intakeTimer.seconds() > 0.2) {
                        intake.setClawPosition(CLAWOPENPOS);
                        lift.setHolderPosition(DEPOSITTRANSFER2);
                    }
                    if (intakeTimer.seconds() > 0.2) {
                        stateForIntake = IntakeStates.DrivingAround;
                        intakeTimerReset = false;
                    }
                }
                break;

            case DrivingAround:
                if (!intakeTimerReset) {
                    intakeTimer.reset();
                    intakeTimerReset = true;
                }
                if (mode == Modes.Circuit) {
                    if (intakeTimer.seconds() > 0.12) {
                        arm.setPosition(ARMMIDPOS3);
                    }
                    if (intakeTimer.seconds() > 0.3) {
                        arm.setPosition(ARMMIDPOS);
                    }
                } else {
                    arm.setPosition(ARMMIDPOS);
                }

                ADJUSTHORIZ = 0;
                if (x_button.edge() == -1) {
                    intakeTimerReset = false;
                    stateForIntake = IntakeStates.LookingForCone;
                    stop_setting_arm_position = false;
                }
                break;

            case GroundDrivingAround:
                if (!intakeTimerReset) {
                    intakeTimer.reset();
                    intakeTimerReset = true;
                }
                if (intakeTimer.seconds() > 0.2) {
                    arm.setPosition(ARMMIDPOS);
                    ADJUSTHORIZ = 0;
                    waitForRetract = true;
                    stateForIntake = IntakeStates.Ground;
                    intakeTimerReset = false;
                }
                break;

            case Ground:
                if (a_button.edge() == -1 && GroundLow) {
                    arm.setPosition(ARMLOWGOAL);
                    GroundLow = false;
                } else if (!GroundLow && a_button.edge() == -1) {
                    arm.setPosition(ARMGROUNDGOAL);
                    GroundLow = true;
                }

                if (dump.edge() == -1 && mode == Modes.Ground) {
                    intake.setClawPosition(CLAWOPENPOS);
                    intakeTimer.reset(); //after the cone is dropped, the timer resets. . .
                    intakeTimerReset = true;
                }

                if (intakeTimerReset && intakeTimer.seconds() > 0.1) { //. . . causing the arm to go up
                    arm.setPosition(ARMMIDPOS);
                }

                if (x_button.edge() == -1) {
                    stateForIntake = IntakeStates.LookingForCone;
                    stop_setting_arm_position = false;
                }

                if (!waitForRetract) {
                    if (!intakeTimerReset) {
                        intakeTimer.reset();
                        intakeTimerReset = true;
                    }
                    intake.setWristPosition(WRISTTRANSFER);
                    if (intakeTimer.seconds() > 0.3) {
                        stateForIntake = IntakeStates.PickingConeUp;
                    }
                }
                break;
        }

        if (switchFast.edge() == -1) {
            mode = Modes.Fast;

        }

        if (switchGround.edge() == -1) {
            mode = Modes.Ground;
        }

        if (switchCircuit.edge() == -1) {
            mode = Modes.Circuit;
        }

        if ((dump.edge() == -1 || lift.getPoleDistance() < POLE_SENSOR_RANGE) && stateForLift == LiftStates.LiftUp) {
            stateForLift = LiftStates.Dump;
        }

//        if (rec_right_bumper.get() && rec_left_bumper.get() && (rec_right_trigger.get() >= 0.3) && (rec_left_trigger.get() >= 0.3)) { // reset encoders
//            lift.resetLiftEncoder();
//        }

        if(stateForIntake == IntakeStates.LookingForCone || stateForIntake == IntakeStates.Ground || stateForIntake == IntakeStates.GroundDrivingAround) {
            horiz_kp_var = HORIZ_KP_FINE;
            if (mode == Modes.Fast) {
                FASTMODEHORIZ += (ax_horizontal_left_x.get() * 90);
                if (FASTMODEHORIZ > MAXEXTENDEDHORIZ) FASTMODEHORIZ = MAXEXTENDEDHORIZ;
                if (FASTMODEHORIZ < 0) FASTMODEHORIZ = 0;
                horizontal.setHorizTarget(FASTMODEHORIZ);
            } else {
                ADJUSTHORIZ += (ax_horizontal_left_x.get() * 110);
                if (ADJUSTHORIZ > MAXEXTENDEDHORIZ) ADJUSTHORIZ = MAXEXTENDEDHORIZ;
                if (ADJUSTHORIZ < 0) ADJUSTHORIZ = 0;
                horizontal.setHorizTarget(ADJUSTHORIZ);
            }
        } else {
            horiz_kp_var = HORIZ_KP;
        }

        lift.setHolderState(servo_kill.get() < 0.2);

        double lift_power = lift_trapezoid.getProfiledPower((lift.getLiftTarget() - lift.getCurrentPosition()), lift_PID.getOutPut(lift.getLiftTarget(), lift.getCurrentPosition(), 1), 0.03);

        lift.setPower(lift_power);
        horizontal.setPower(horiz_PID.getOutPut(horizontal.getHorizTarget(), horizontal.getCurrentPosition(), 0));

        arm.setPosition(arm.getTargetPosition() - ax_arm_right_y.get());

        if (arm.getTargetPosition() > ARMHIGHPOS) {
            arm.setPosition(ARMHIGHPOS);
        }

        telemetry.addData("Claw Position", CLAWCLOSEPOS);

        telemetry.addData("Lift Pow", lift_power);
        telemetry.addData("Lift Target", lift.getLiftTarget());
        telemetry.addData("Lift Encoder", lift.getCurrentPosition());

        telemetry.addData("Horiz Pow", horiz_PID.getOutPut(horizontal.getHorizTarget(), horizontal.getCurrentPosition(), 0));
        telemetry.addData("Horiz Target", horizontal.getHorizTarget());
        telemetry.addData("Horiz Encoder", horizontal.getCurrentPosition());

        telemetry.addData("Arm Encoder", arm.getCurrentEncoderPosition());

        telemetry.addData("Current Intake State", stateForIntake);
        telemetry.addData("Current Lift State", stateForLift);
        telemetry.addData("Current Mode", mode);

        telemetry.addData("Pole Distance", lift.getPoleDistance());

        //telemetry.addData("Sensor Distance", intake.getDistance());
        telemetry.addData("Loop Time", loop.time());

    }
}