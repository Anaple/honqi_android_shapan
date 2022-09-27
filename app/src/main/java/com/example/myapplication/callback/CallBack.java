package com.example.myapplication.callback;

import java.util.List;
import java.util.Map;

public interface CallBack {

    void createCar(List<Integer> carsId);

    void createPoint(List<Integer> pointsId, Map pointsPosition ,Map mapSize);

    void setCarStatus(int carId,Map carStatus);

    void dramaFinish(int dramaId);







}
