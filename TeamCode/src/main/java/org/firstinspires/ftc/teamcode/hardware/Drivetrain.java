package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

public class Drivetrain {
    DcMotor top_left;
    DcMotor bottom_left;
    DcMotor top_right;
    DcMotor bottom_right;
    DcMotor forward_enc;
    DcMotor side_enc;

    public Drivetrain(DcMotor top_left, DcMotor bottom_left, DcMotor top_right, DcMotor bottom_right, DcMotor forward_enc, DcMotor side_enc){
        this.top_left = top_left;
        this.bottom_left = bottom_left;
        this.top_right = top_right;
        this.bottom_right = bottom_right;
        this.forward_enc = forward_enc;
        this.side_enc = side_enc;

        //Reverses left side to match right side rotation and sets mode
        top_right.setDirection(REVERSE);
        bottom_right.setDirection(REVERSE);
        top_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bottom_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        top_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bottom_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void telemove(double left_stick_y, double right_stick_x){
        //Subtracts power from forward based on the amount of rotation in the other stick
        double left_wheel_speed = -left_stick_y+right_stick_x;
        double right_wheel_speed = -left_stick_y-right_stick_x;
        top_left.setPower(left_wheel_speed);
        bottom_left.setPower(left_wheel_speed);
        top_right.setPower(right_wheel_speed);
        bottom_right.setPower(right_wheel_speed);
    }

    public void autoy(double distance, double speed){
                 
    }

    // Calculates forward dist, side dist, and heading respectively
    public double[] distanceCalc(){
        forward_enc.getCurrentPosition();
        return null;
    }
}
