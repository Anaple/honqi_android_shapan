package com.example.myapplication.model;
// socket 通讯操作封装
public class Agreement {
    //初始化车辆
    public static String INIT_CARS = "{" +
            "\"type\":\"cars_request\"" +
            "}";
    //初始化点位
    public static String INIT_POINTS = "{" +
            "\"type\":\"scence_request\"" +
            "}";

    //终点发送
    public static String NAV_END(int planNode,int carId){
        return "{" +
                "\"type\":\"nav_end\"," +
                "\"nav_end\":{" +
                "\"plan_node\":"+planNode+"," +
                "\"car_id\":"+carId+"" +
                "}" +
                "}";
    }

    //情景发送
    public static String DRAMA(int dramaId){
        return "{" +
                "\"type\":\"drama\"," +
                "\"drama\":"+dramaId+"" +
                "}";
    }
    //U3D视角
    public static String U3D_VIEW(int carId,int view){
        return "{" +
                "\"type\":\"u3d_view\"," +
                "\"u3d_view\":{" +
                "\"view\":"+view+"," +
                "\"car_id\":" +carId+
                "}" +
                "}";

    };



}
