package com.example.myapplication.model;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.example.myapplication.callback.CallBack;
import com.example.myapplication.callback.NetWorkCallBack;

import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该类被用于建立socket通信及解析socket通讯内容
 *
 *
 */

public class MyServer {

    public static String IP = "192.168.0.106";
    public static int PORT = 9000;

    public static volatile Socket MySocket = null;
    /**
     * 以下常量为协议包说明，禁止修改
     */
    public static String SCENCE_REPLY = "scence_reply";
    public static String CAR_USING = "cars_using";
    private static final String DEV_STATUS = "dev_status";
    private static final String DRAMA_FINISH = "drama_finish";
    private static final String NAV_UNITY = "nav_unity";
    private static final String SEEKER_POS = "seeker_pos";


    public static void BeginConnection(NetWorkCallBack netWorkCallBack) {
        new Thread(() -> {
            try {
                Socket socket = new Socket(IP, PORT);
                netWorkCallBack.success(socket);
            } catch (Exception e) {
                System.out.println("日志消息:" + e.getMessage());
            }
        }).start();
    }
    /**
     * 用于解析内容，检测连接状态，守护连接
     */
    public static void Begin(CallBack callBack ,NetWorkCallBack netWorkCallBack) {
        new Thread(() -> {
            try {
                while (MySocket == null) ;
                InputStream inputStream = MySocket.getInputStream();
                while (MySocket != null) {
                    try {
                        MySocket.sendUrgentData(0xFF);
                    }catch (Exception ex){
                        MySocket = null;
                        netWorkCallBack.error();
                        break;
                    }

                    byte[] data = new byte[40960];
                    try {
                        inputStream.read(data);
                    }catch (SocketException socketException){
                        MySocket = null;
                        Log.e("SOCKET", String.valueOf(socketException));
                        netWorkCallBack.error();
                        break;
                    }

                    Map dataMaps = socketRead(data);
                    String type = (String) dataMaps.get("type");

                    if (type.equals(CAR_USING)) {
                        Map objMap = (Map) dataMaps.get(CAR_USING);
                        JSONArray carsId = (JSONArray) objMap.get("cars_id");
                        List<Integer> listCarsId = carsId.toJavaList(Integer.class);
                        callBack.createCar(listCarsId);
                    }
                    if (type.equals(SCENCE_REPLY)) {
                        Map map = (Map) dataMaps.get(SCENCE_REPLY);
                        JSONArray scenceId = (JSONArray) map.get("scence_id");
                        List<Integer> listScenceId = scenceId.toJavaList(Integer.class);
                        Map scencePoints = (Map) map.get("scence_points");
                        Map scenceWH = (Map) map.get("map_size");
                        callBack.createPoint(listScenceId, scencePoints, scenceWH);

                    }
                    if (type.equals(DEV_STATUS)) {
                        Map devStatus = (Map) dataMaps.get(DEV_STATUS);
                        int carId = (int) dataMaps.get("car_id");
                        callBack.setCarStatus(carId, devStatus);

                    }
                    if (type.equals(DRAMA_FINISH)) {
                        int dramaId = (int) dataMaps.get("drama");
                        callBack.dramaFinish(dramaId);


                    }
                    if (type.equals(SEEKER_POS)) {
                        Map map = (Map) dataMaps.get(SEEKER_POS);
                        int carId = (int) map.get("car_id");
                        int x = Integer.parseInt((String) map.get("x"));
                        int y = Integer.parseInt((String) map.get("y"));
                        callBack.carPointSet(x, y, carId);


                    }
                    if (type.equals(NAV_UNITY)) {
                        Map map = (Map) dataMaps.get(NAV_UNITY);
                        int pointsCount = (int) map.get("points_count");
                        Map navPoints = (Map) map.get("nav_points");
                        Map navWH = (Map) map.get("map_size");
                        callBack.navPointSet(navPoints, navWH, pointsCount);

                    } else {
                        Log.i("Maps", dataMaps.toString() + type);
                    }

                }
            } catch (Exception e) {
                Log.e("ERROR", "SOCKET", e);
            }
        }).start();
    }
    /**
     *  接收解析方法
     */
    public static Map socketRead(byte[] data) {
        //包头[FF AA]
        //长度 第三 第四位
        Map maps = new HashMap();
        if (data[0] == (byte) 0xff) {
            if (data[1] == (byte) 0xaa) {
                //获取长度
                String lenStr = Integer.toHexString(data[2] & 0xff) + Integer.toHexString(data[3] & 0xff);
                int len = Integer.parseInt(lenStr, 16);
                //截取数据
                byte[] jsonByte = new byte[len];
                Log.i("Len",len+":"+lenStr);
                for (int i = 4, q = 0; q <= len - 1; i++, q++) {
                    jsonByte[q] = data[i];
                }
                //转换为JsonMap

                String jsonStr = new String(jsonByte);
                try {
                    maps = (Map) JSON.parse(jsonStr);
                    Log.i("SOCKET_MAP", maps.toString());
                    return maps;

                } catch (com.alibaba.fastjson.JSONException e) {
                    Log.i("SOCKET_MAP_E", jsonStr);
                    Log.i("SOCKET_Byte", Arrays.toString(jsonByte));
                    maps.put("type", "error");
                    return maps;
                }

            }
        }
        maps.put("type", "null");
        return maps;

    }
    /**
     *  发送打包方法
     */
    public static byte[] createByte(String jsonStr, boolean isU3d) {
        byte[] jsonByte = jsonStr.getBytes(StandardCharsets.UTF_8);
        int jsonByteLen = jsonByte.length;
        byte[] data = new byte[jsonByteLen + 4];
        data[0] = (byte) 0xff;
        data[1] = (byte) 0xaa;
        if (isU3d) {
            data[1] = (byte) 0xbb;
        }
        if (jsonByteLen <= 255) {
            data[2] = (byte) 0x00;
            data[3] = (byte) jsonByteLen;
        } else {
            String hexString = Integer.toHexString(jsonByteLen);


            if (hexString.length() == 4) {
                data[2] = (byte) Integer.parseInt(hexString.split("")[0] + hexString.split("")[1], 16);
                data[3] = (byte) Integer.parseInt(hexString.split("")[2] + hexString.split("")[3], 16);
            } else {
                data[2] = (byte) Integer.parseInt(hexString.split("")[0], 16);
                data[3] = (byte) Integer.parseInt(hexString.split("")[1] + hexString.split("")[2], 16);

            }


        }

        for (int i = 4; i < jsonByteLen + 4; i++) {
            data[i] = jsonByte[i - 4];
        }
        return data;

    }


}
