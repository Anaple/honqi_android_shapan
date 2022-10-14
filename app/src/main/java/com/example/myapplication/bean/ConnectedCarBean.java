package com.example.myapplication.bean;

public class ConnectedCarBean {

    private int carIndex;
    private int carBattery;
    private boolean isOnclick;
    private int speed;
    private int angle;
    private String road;

    public ConnectedCarBean() {
    }

    public ConnectedCarBean(int carIndex) {
        this.carIndex = carIndex;
    }

    public ConnectedCarBean(int carIndex, int carBattery, boolean isOnclick, int speed, int angle) {
        this.carIndex = carIndex;
        this.carBattery = carBattery;
        this.isOnclick = isOnclick;
        this.speed = speed;
        this.angle = angle;
    }

    public int getCarIndex() {
        return carIndex;
    }

    public void setCarIndex(int carIndex) {
        this.carIndex = carIndex;
    }

    public int getCarBattery() {
        return carBattery;
    }

    public void setCarBattery(int carBattery) {
        this.carBattery = carBattery;
    }

    public boolean isOnclick() {
        return isOnclick;
    }

    public void setOnclick(boolean onclick) {
        isOnclick = onclick;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }
}
