package edu.agh.iwium.robots;


import robocode.*;
import robocode.Robot;

import java.awt.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;

public class SimpleRuleRobot extends Robot {

    private Event event = Event.NONE;
    private Event lastEvent = Event.NONE;
    private int bulletPower = 1;
    private RulesDispatcher dispatcher = new RulesDispatcher();

    @Override
    public void run() {

        setBodyColor(Color.yellow);
        setGunColor(Color.black);
        setRadarColor(Color.red);
        setBulletColor(Color.green);
        setScanColor(Color.green);

        // Move to a corner
        //goCorner();


        while (true) {
            dispatcher.handleEvent(this);
        }
    }

    public void goCorner() {
        // turn to face the wall to the "right" of our desired corner.
        turnRight(normalRelativeAngleDegrees(0 - getHeading()));

        // Move to that wall
        ahead(5000);
        // Turn to face the corner
        turnLeft(90);
        // Move to the corner
        ahead(5000);
        // Turn gun to starting point
        turnGunLeft(90);
    }


    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        processEvent(Event.ON_SCANNED_ROBOT, e);
    }

    @Override
    public void onHitRobot(HitRobotEvent e) {
        processEvent(Event.ON_HIT_ROBOT, e);
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        processEvent(Event.ON_HIT_BY_BULLET, e);
    }

    @Override
    public void onHitWall(HitWallEvent e) {
        processEvent(Event.ON_HIT_WALL, e);
    }

    @Override
    public void turnLeft(double degrees) {
        super.turnLeft(degrees);
    }

    @Override
    public void onBulletHit(BulletHitEvent e) {
        this.event = Event.ON_BULLET_HIT;
        dispatcher.handleEvent(this, e);
    }

    @Override
    public void onBulletMissed(BulletMissedEvent e) {
        processEvent(Event.ON_BULLET_MISSED, e);
    }

    private void processEvent(Event e, robocode.Event robocodeEvent) {
        this.event = e;
        dispatcher.handleEvent(this, robocodeEvent);
        reset(e);
    }

    public int getBulletPower() {
        return bulletPower;
    }

    public void setBulletPower(int bulletPower) {
        this.bulletPower = bulletPower;
    }

    public String getEvent() {
        return event.name();
    }

    public void increasePower(int n) {
        bulletPower += n;
    }

    public void decreasePower(int n) {
        bulletPower -= n;
    }

    public void reset(Event lastEvent) {
        this.lastEvent = lastEvent;
        this.event = Event.NONE;
    }

    public String getLastEvent() {
        return lastEvent.name();
    }

    public void setLastEvent(String lastEvent) {
        this.lastEvent = Event.valueOf(lastEvent);
    }

    public void onDeath(DeathEvent e) {
        System.out.println("I died but did well.");
    }
}
