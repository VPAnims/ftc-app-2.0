package org.firstinspires.ftc.teamcode.opmodes;

import static org.opencv.core.CvType.CV_8UC4;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;

import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.hardware.navigation.AutoStates;
import org.firstinspires.ftc.teamcode.hardware.navigation.OdometryNav;
import org.firstinspires.ftc.teamcode.hardware.navigation.PID;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.util.Logger;
import org.firstinspires.ftc.teamcode.util.LoopTimer;
import org.firstinspires.ftc.teamcode.vision.ConeInfoDetector;
import org.firstinspires.ftc.teamcode.vision.webcam.Webcam;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

@Autonomous(name = "Test Auto")
public class TestingAuto extends LoggingOpMode{
    private Drivetrain drivetrain;
    private AutoStates autoStates;
    private OdometryNav odometry;
    private Webcam camera;
    private Webcam.SimpleFrameHandler frameHandler;
    private Mat cvFrame;
    private volatile boolean serverFrameUsed = true;
    private Bitmap serverFrameCopy;
    private String result = "Nothing";

    private int main_id = 0;

    private final Logger log = new Logger("Test Auto");
    @Override
    public void init(){
        super.init();
        Robot robot = Robot.initialize(hardwareMap);
        drivetrain = robot.drivetrain;
        camera = Webcam.forSerial("3522DE6F");
        if (camera == null)
            throw new IllegalArgumentException("Could not find a webcam with serial number 3522DE6F");
        frameHandler = new Webcam.SimpleFrameHandler();
        camera.open(ImageFormat.YUY2, 1920, 1080, 30, frameHandler);
        cvFrame = new Mat(1920, 1080, CV_8UC4);
        Pose2d start_pose = new Pose2d(0,0,new Rotation2d(Math.toRadians(45.0)));
        odometry.updatePose(start_pose);
    }

    @Override
    public void Start(){
      super.start();
      drivetrain.resetEncoders();
    }
    @Override
    public void loop() {
        drivetrain.stop();
    }
    @Override
    public void Stop(){
        super.stop();
    }
}
