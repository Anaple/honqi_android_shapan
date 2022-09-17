package com.example.myapplication.callback;

import java.util.Map;

public interface CallBack {

    void createCar(int [] carsId);

    void createPoint(int[] pointsId, Map pointsPosition);

    void setCarStatus(int carId,Map carStatus);





}
