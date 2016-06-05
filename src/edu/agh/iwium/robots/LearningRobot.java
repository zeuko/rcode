package edu.agh.iwium.robots;


import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import robocode.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.agh.iwium.robots.Event;

public class LearningRobot extends AdvancedRobot {

    private Event event = Event.NONE;
    private Event lastEvent = Event.NONE;
    private int bulletPower = 1;
    private RulesDispatcher dispatcher = new RulesDispatcher("rules-LearningRobot.xml");

    private static List<List<Double>> onBulletHitDataList = new ArrayList();

    private double learnedDistance;
    private double distanceFromEnemy;
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
        this.distanceFromEnemy = e.getDistance();
        this.enemyVelocity = e.getVelocity();
        processEvent(Event.ON_SCANNED_ROBOT, e);
    }


    @Override
    public void onBulletHit(BulletHitEvent e) {
        storeBulletHitEventData(e);
        this.event = Event.ON_BULLET_HIT;
        dispatcher.handleEvent(this, e);
    }

    /**
     * Adds new vector of data to onBulletHitDataList.
     */
    private void storeBulletHitEventData(BulletHitEvent e) {
        List<Double> newHitData = new ArrayList<>();
        newHitData.add(learnedDistance);
        newHitData.add(e.getBullet().getPower());
        newHitData.add(this.distanceFromEnemy);
        newHitData.add(this.enemyVelocity);
        onBulletHitDataList.add(newHitData);
    }

    public void fireAndMove(double power) {
        super.fire(power);
        learnedDistance = findOptimalDistanceUsingML();
        this.ahead(learnedDistance);
    }

    public void fireNoMove(double power) {
        super.fire(power);
    }

    private double findOptimalDistanceUsingML() {

        // not enough data, just randomize something
        if (onBulletHitDataList.size() < 35) {
            return new Random().nextInt(100);
        }

        // create dataset for learning
        Dataset data = new DefaultDataset();
        for (List<Double> list : onBulletHitDataList) {
            double[] values = toPrimitive(list.subList(1, list.size()).toArray(new Double[]{}));
            Instance instance = new DenseInstance(values, list.get(0));
            data.add(instance);
        }

        // build classifier model
        Classifier knn = new KNearestNeighbors(5);
        knn.buildClassifier(data);

        // create new vector of data
        List<Double> newData = new ArrayList<>();
        newData.add((double) this.getBulletPower());
        newData.add(this.distanceFromEnemy);
        newData.add(this.enemyVelocity);
        Instance instanceToClassify = new DenseInstance(toPrimitive(newData.subList(1, newData.size()).toArray(new Double[]{})));

        // return optimal value
        return (Double) knn.classify(instanceToClassify);
    }

    private double[] toPrimitive(Double[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new double[]{};
        }
        final double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].doubleValue();
        }
        return result;
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

    public void setLearnedDistance(double learnedDistance) {
        this.learnedDistance = learnedDistance;
    }

}
