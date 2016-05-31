package edu.agh.iwium.robots;

import robocode.*;

import java.awt.*;

/**
 * Created by Mere on 2016-05-31.
 */
public class SimpleBowRobot extends RateControlRobot {

    public void run() {

        setBodyColor(Color.yellow);
        setGunColor(Color.black);
        setRadarColor(Color.red);
        setBulletColor(Color.green);
        setScanColor(Color.green);

        while(true) {
            ahead(100);
            turnGunRight(360);
            turnRight(120);
            ahead(200);
            turnRight(120);
        }
    }

    public void onHitByBullet(HitByBulletEvent hitByBulletEvent) {
        turnLeft(90);
        ahead(100);
    }

    public void onHitWall(HitWallEvent hitWallEvent) {
        if(hitWallEvent.getBearing() > -90 && hitWallEvent.getBearing() <= 90) {
            setVelocityRate(-1 * getVelocityRate());
        }
        else {
            ahead(300);
        }
    }

    public void onHitRobot(HitRobotEvent hitRobotEvent) {
        if(hitRobotEvent.isMyFault()) {
            if(hitRobotEvent.getBearing() > -90 && hitRobotEvent.getBearing() <= 90) {
                back(200);
            }
            else {
                turnRight(60);
                ahead(200);
            }
        }
        else {
            if(hitRobotEvent.getBearing() > -90 && hitRobotEvent.getBearing() <= 90) {
                turnRight(hitRobotEvent.getBearing());
            }
            else {
                turnRight(60);
                ahead(200);
            }
        }
    }

    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {

        if (scannedRobotEvent.getDistance() > 300) { fire(1); }
        else if(scannedRobotEvent.getDistance() < 150) { fire(3); }
        else { fire(2); }
    }

}
