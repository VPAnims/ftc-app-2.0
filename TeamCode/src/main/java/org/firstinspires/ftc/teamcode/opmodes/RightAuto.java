//package org.firstinspires.ftc.teamcode.opmodes;
//
//import static org.opencv.core.CvType.CV_8UC4;
//
//import android.graphics.Bitmap;
//import android.graphics.ImageFormat;
//
//import com.arcrobotics.ftclib.geometry.Pose2d;
//import com.arcrobotics.ftclib.geometry.Rotation2d;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.util.Range;
//
//import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
//import org.firstinspires.ftc.teamcode.hardware.Intake;
//import org.firstinspires.ftc.teamcode.hardware.Lift;
//import org.firstinspires.ftc.teamcode.hardware.Robot;
//import org.firstinspires.ftc.teamcode.hardware.navigation.OdometryNav;
//import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
//import org.firstinspires.ftc.teamcode.util.Logger;
//import org.firstinspires.ftc.teamcode.util.LoopTimer;
//import org.firstinspires.ftc.teamcode.vision.ConeInfoDetector;
//import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;
//import org.opencv.android.OpenCVLoader;
//import org.opencv.android.Utils;
//import org.opencv.core.Mat;
//
//@Disabled
//@Autonomous(name = "Right Auto")
//public class RightAuto extends LoggingOpMode{
//
//    private Drivetrain drivetrain;
//    private Lift lift;
//    private Intake intake;
//    private OdometryNav odometry;
//    private Webcam camera;
//    private Webcam.SimpleFrameHandler frameHandler;
//    private Mat cvFrame;
//    private volatile boolean serverFrameUsed = true;
//    private Bitmap serverFrameCopy;
//    private String result = "Nothing";
//
//    private int main_id = 0;
//
//    private final double ARM_LOWER_LENGTH = 488.89580;
//    private final double ARM_UPPER_LENGTH = 424.15230;
//
//    private double x = 0;
//    private double y = 250;
//
//    double al_error = 0;
//    double au_error = 0;
//
//    private final double AL_DEGREES_PER_TICK = -(360.0/8192.0);
//    private final double AU_DEGREES_PER_TICK = (360.0/8192.0);
//    private final double WRIST_DEGREES_PER_TICK = (360.0/128.0);
//
//    private final PID arm_lower = new PID(0.0323,0.0003,0.00091, 0.167,100,0.8);
//    private final PID arm_upper = new PID(0.042,0.00200,0.0018,0.11,100,0.8); // 0.029, 0.0022, 0.001 then 0.027, 0.00228
//    private final PID wrist = new PID(0.02,0,0,0,0,0);
//
//    private final Logger log = new Logger("Right Auto");
//
//    static
//    {
//        OpenCVLoader.initDebug();
//    }
//
//    @Override
//    public void init() {
//        super.init();
//        Robot robot = Robot.initialize(hardwareMap);
//        drivetrain = robot.drivetrain;
//        lift = robot.lift;
//        intake = robot.intake;
//        odometry = robot.odometryNav;
//        camera = Webcam.forSerial("3522DE6F");
//        if (camera == null)
//            throw new IllegalArgumentException("Could not find a webcam with serial number 3522DE6F");
//        frameHandler = new Webcam.SimpleFrameHandler();
//        camera.open(ImageFormat.YUY2, 1920, 1080, 30, frameHandler);
//        cvFrame = new Mat(1920, 1080, CV_8UC4);
//        Pose2d start_pose = new Pose2d(0,3.5,new Rotation2d(Math.toRadians(45.0)));
//        odometry.updatePose(start_pose);
//    }
//
//    @Override
//    public void init_loop() {
//        super.init_loop();
//
//        if (frameHandler.newFrameAvailable) {
//            frameHandler.newFrameAvailable = false;
//            Utils.bitmapToMat(frameHandler.currFramebuffer, cvFrame);
//            if (serverFrameUsed) {
//                if (serverFrameCopy != null) serverFrameCopy.recycle();
//                serverFrameCopy = frameHandler.currFramebuffer.copy(Bitmap.Config.ARGB_8888, false);
//                serverFrameUsed = false;
//            }
//
//            ConeInfoDetector detector = new ConeInfoDetector(cvFrame,log,-1006,255);
//
//            if (!detector.detect().equals("Nothing Detected"))
//            {
//                result = detector.detect();
//            }
//
//            camera.requestNewFrame();
//        }
//
//        telemetry.addData("Detected", result);
//
//        telemetry.update();
//    }
//
//    @Override
//    public void start() {
//        super.start();
//        drivetrain.resetEncoders();
//    }
//
//    @Override
//    public void loop() {
//
//        odometry.updatePose();
//
//        double[] angles = new double[2];
//
//        if (Math.sqrt(Math.pow(x,2) + Math.pow(y,2)) >= (488.89580+424.15230-5)) {
//            angles[0] = Math.toDegrees(Math.atan2(y,x));
//            angles[1] = angles[0];
//        }
//        else {
//            angles = lift.get_ang(ARM_LOWER_LENGTH, ARM_UPPER_LENGTH, x, y, 90, -90);
//        }
//
//        double[] cur_angles = lift.getEncoderValue();
//        cur_angles[0] *= AL_DEGREES_PER_TICK;
//        cur_angles[1] *= AU_DEGREES_PER_TICK;
//        cur_angles[2] *= -WRIST_DEGREES_PER_TICK;
//        cur_angles[0] += 0;//149.39559808298318;
//        cur_angles[1] += 0;//-165.8935546875;
//        cur_angles[2] += 0;//-2.8125;
//
//        double al_f = Math.cos(Math.toRadians(cur_angles[0]));
//        double au_f = Math.cos(Math.toRadians(cur_angles[0]) + Math.toRadians(cur_angles[1]));
//
//
//        double al_pow = arm_lower.getOutPut(angles[0], cur_angles[0], al_f);
//        double au_pow = -1 * arm_upper.getOutPut((-angles[0] + angles[1]), cur_angles[1], au_f);
//        double wrist_pow = wrist.getOutPut((-angles[1]), cur_angles[2], 0);
//
//        al_error = Math.abs(angles[0] - cur_angles[0]);
//        au_error = Math.abs((-angles[0] + angles[1]) - cur_angles[1]);
//        double wr_error = Math.abs((-angles[1]) - cur_angles[2]);
//
//        lift.setLiftPower(Range.clip(al_pow, -0.5, 0.8), Range.clip(au_pow, -0.6, 0.6), wrist_pow);
//
//        switch (main_id) {
//            case 0:
//                x = 96;
//                y = 855;
//                drivetrain.autoMove(21,0,0,0,2,6,10, odometry.getPose(),telemetry);
//                if (drivetrain.hasReached()) {
//                    main_id += 1;
//                }
//                break;
//            case 1:
//                drivetrain.autoMove(24,-33.8,0,0,1,1,2, odometry.getPose(),telemetry);
//                if (drivetrain.hasReached()) {
//                    main_id += 1;
//                }
//                break;
//            case 2:
//            case 12:
////            case 17:
//                drivetrain.stop();
//
//                if (al_error < 3 && au_error < 3.6 && wr_error < 10) {
//                    main_id += 1;
//                }
//                break;
//            case 3:
////            case 18:
//                intake.setClaw(0.11);
//                if ((intake.getClawPosition() == 0.11) && (intake.getDistance() > 20)) {
//                    main_id += 1;
//                    x = 0;
//                    y = 600;
//                }
//                break;
//            case 4:
//                switch (result) {
//                    case "FTC8813: 1":
//                        drivetrain.autoMove(24,-24,0,0,1,1,10, odometry.getPose(),telemetry);
//                        if (drivetrain.hasReached()) {
//                            main_id += 1;
//                        }
//                        break;
//                    case "FTC8813: 3":
//                        drivetrain.autoMove(24,24,0,0,1,1,10, odometry.getPose(),telemetry);
//                        if (drivetrain.hasReached()) {
//                            main_id += 1;
//                        }
//                        break;
//                    default:
//                        drivetrain.autoMove(24,-1,0,0,1,1,10, odometry.getPose(),telemetry);
//                        if (drivetrain.hasReached()) {
//                            main_id += 1;
//                        }
//                        break;
//                }
//                break;
//            case 5:
//                drivetrain.stop();
//                break;
////            case 4:
////                drivetrain.autoMove(23,0,0,0,1,1,3, odometry.getPose(),telemetry);
////                if (drivetrain.hasReached()) {
////                    main_id += 1;
////                }
////                break;
////            case 5:
////                drivetrain.autoMove(44.1,0,0,0,1,1,3, odometry.getPose(),telemetry);
////                if (drivetrain.hasReached()) {
////                    main_id += 1;
////                }
////                break;
////            case 6:
////                drivetrain.autoMove(44.1,10,0,0,1,1,3, odometry.getPose(),telemetry);
////                if (drivetrain.hasReached()) {
////                    main_id += 1;
////                }
////                break;
////            case 7:
////                drivetrain.autoMove(44.1,10,82,0,0.5,0.5,0.03, odometry.getPose(),telemetry);
////                if (drivetrain.hasReached()) {
////                    main_id += 1;
////                }
////                break;
////            case 8:
////                x = 1000;
////                y = -40;
////                drivetrain.autoMove(44.1,10,82,0,0.5,0.5,0.03, odometry.getPose(),telemetry);
////                if (al_error < 3 && au_error < 3 && wr_error < 10) {
////                    main_id += 1;
////                }
////                break;
////            case 9:
//////            case 15:
////                drivetrain.autoMove(44.1,10,82,0,0.5,0.5,0.03, odometry.getPose(),telemetry);
////                if (intake.getDistance() < 20) {
////                    intake.setClaw(0.63);
////                }
////                if ((intake.getClawPosition() == 0.63) && (intake.getDistance() < 20)){
////                    main_id += 1;
////                }
////                break;
////            case 10:
//////            case 16:
////                x = -380;
////                y = 870;
////                drivetrain.autoMove(44.1,35,82,0,0.5,0.5,0.03, odometry.getPose(),telemetry);
////                if (drivetrain.hasReached()) {
////                    main_id += 1;
////                }
////                break;
////            case 11:
////                drivetrain.autoMove(44.1,35,3,0,0.5,0.5,0.03, odometry.getPose(),telemetry);
////                if (drivetrain.hasReached()) {
////                    main_id += 1;
////                }
////                break;
////            case 14:
////                x = 0;
////                y = 250;
////                drivetrain.autoMove(44.1,10,3,0,0.5,0.5,0.03, odometry.getPose(),telemetry);
////                if (drivetrain.hasReached()) {
////                    main_id += 1;
////                }
////                break;
////            case 15:
////                x = 1000;
////                y = -25;
////                drivetrain.autoMove(44.1,10,82,0,0.5,0.5,0.03, odometry.getPose(),telemetry);
////                if (drivetrain.hasReached()) {
////                    main_id += 1;
////                }
////                break;
//
////            case 14:
////                x = 1000;
////                y = -25;
////                drivetrain.autoMove(44.1,10,82,0,0.5,0.5,0.03, odometry.getPose(),telemetry);
////                if (al_error < 3 && au_error < 3 && wr_error < 10) {
////                    main_id += 1;
////                }
//
//        }
//        telemetry.addData("AL Target Angle",angles[0]);
//        telemetry.addData("AU Target Angle",(-1*(angles[0] - angles[1])));
//        telemetry.addData("WR Target Angle",-angles[1]);
//
//        telemetry.addData("AL Angle",cur_angles[0]);
//        telemetry.addData("AU Angle",cur_angles[1]);
//        telemetry.addData("WR Angle",cur_angles[2]);
//
//        telemetry.addData("AL Power",al_pow);
//        telemetry.addData("AU Power",au_pow);
//        telemetry.addData("WR Power",wrist_pow);
//
//
//        telemetry.addData("X", x);
//        telemetry.addData("Y", y);
//
//        telemetry.addData("Loop Time: ", LoopTimer.getLoopTime());
//        telemetry.addData("Error AU: ", au_error);
//        telemetry.addData("Error AL: ", al_error);
//        telemetry.addData("Main Id: ", main_id);
//        telemetry.update();
//
//        LoopTimer.resetTimer();
//
//    }
//
//    @Override
//    public void stop() {
//        super.stop();
//    }
//
//}
