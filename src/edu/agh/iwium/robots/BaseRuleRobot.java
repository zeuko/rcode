package edu.agh.iwium.robots;


import robocode.*;

import java.awt.*;
import java.util.Random;

public class BaseRuleRobot extends AdvancedRobot {

    private Event event = Event.NONE;
    private Event lastEvent = Event.NONE;
    private int bulletPower = 1;
    private RulesDispatcher dispatcher = new RulesDispatcher("rules-BaseRuleRobot.xml");

    private double enemyVelocity;

    @Override
    public void run() {

        setBodyColor(Color.yellow);
        setGunColor(Color.black);
        setRadarColor(Color.red);
        setBulletColor(Color.green);
        setScanColor(Color.green);

        while (true) {
            dispatcher.handleEvent(this);
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        this.enemyVelocity = e.getVelocity();
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

    public void fireAndMove(double power) {
        super.fire(power);
        this.ahead(new Random().nextInt(140));
    }

    public void fireNoMove(double power) {
        super.fire(power);
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

    public double getEnemyVelocity() {
        return enemyVelocity;
    }

}
