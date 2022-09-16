package com.example.myapplication.bean;

public class ConnectedCarBean {

    private int carIndex;
    private boolean canUse;
    private int carBattery;
    private boolean isOnclick;


    public ConnectedCarBean(int carIndex, boolean canUse, int carBattery, boolean isOnclick) {
        this.carIndex = carIndex;
        this.canUse = canUse;
        this.carBattery = carBattery;
        this.isOnclick = isOnclick;
    }

    public ConnectedCarBean() {
    }

    public int getCarIndex() {
        return carIndex;
    }

    public void setCarIndex(int carIndex) {
        this.carIndex = carIndex;
    }

    public boolean isCanUse() {
        return canUse;
    }

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
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
}
