package com.example.myapplication.model;

import android.util.JsonReader;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.example.myapplication.bean.JsonBean;
import com.example.myapplication.callback.CallBack;
import com.example.myapplication.callback.NetWorkCallBack;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyServer
{

    public static String IP   = "192.168.31.105";
    public static int    PORT = 1235;
    public static volatile Socket MySocket = null;
    private JsonBean JsonBean;
    public static String SCENCE_REPLY = "scence_reply";
    public static String CAR_USING = "cars_using";
    private static String DEV_STATUS = "dev_status";
    private static String DRAMA_FINISH = "drama_finish";






    public static void BeginConnection(NetWorkCallBack netWorkCallBack)
    {
        new Thread(() -> {
            try{
                Socket socket = new Socket(IP,PORT);
                netWorkCallBack.success(socket);
            }catch (Exception e)
            {
                System.out.println("日志消息:"+e.getMessage());
            }
        }).start();
    }
    public static void Begin(CallBack callBack)
    {
        new Thread(() -> {
            try{
                while (MySocket == null);
                InputStream inputStream = MySocket.getInputStream();
                while(true)
                {
                    byte[] data = new byte[1024];
                    inputStream.read(data);
                    Map dataMaps = socketRead(data);
                    String type = (String) dataMaps.get("type");

                    if(type.equals(CAR_USING)){
                        Map objMap = (Map) dataMaps.get(CAR_USING);
                        JSONArray carsId =  (JSONArray)objMap.get("cars_id");
                        List<Integer> listCarsId =  carsId.toJavaList(Integer.class);
                        callBack.createCar(listCarsId);
                    }
                    if(type.equals(SCENCE_REPLY)){
                        Map mapSize = (Map) dataMaps.get(CAR_USING);
                        JSONArray scenceId = (JSONArray) dataMaps.get("scence_id");
                        List<Integer> listScenceId =  scenceId.toJavaList(Integer.class);
                        Map scencePoints = (Map) dataMaps.get("scence_points");
                        callBack.createPoint(listScenceId,scencePoints);

                    }
                    if(type.equals(DEV_STATUS)){
                        Map devStatus = (Map) dataMaps.get(DEV_STATUS);
                        int carId = (int) dataMaps.get("car_id");
                        callBack.setCarStatus(carId,devStatus);

                    }if(type.equals(DRAMA_FINISH)){
                        int dramaId = (int) dataMaps.get("drama");
                        callBack.dramaFinish(dramaId);

                }

                    else {
                        Log.i("Maps",dataMaps.toString()+type);
                    }

                    Log.i("Socket", Arrays.toString(data));
                }
            }catch (Exception e)
            {
                Log.e("ERROR","SOCKET",e);
            }
        }).start();
    }

    public static Map socketRead(byte[] data){
        //包头[FF AA]
        //长度 第三 第四位
        Map maps = new HashMap();
        if(data[0] == (byte) 0xff){
            if(data[1] == (byte) 0xaa){
                //获取长度
                int len =  (data[2]& 0xff) + (data[3]& 0xff);
                //截取数据
                byte[] jsonByte = new byte[len];
                for (int i = 4,q=0; q<=len-1;i++,q++) {
                    jsonByte[q] = data[i];
                }
                //转换为JsonMap

                String jsonStr = new String(jsonByte);
                try {
                     maps = (Map)JSON.parse(jsonStr);
                     Log.i("SOCKET_MAP", maps.toString());
                     return maps;

                }catch (com.alibaba.fastjson.JSONException e){
                    Log.i("SOCKET_MAP_E", jsonStr);
                    Log.i("SOCKET_Byte", Arrays.toString(jsonByte));
                    maps.put("type","error");
                    return maps;
                }

            }
        }
        maps.put("type","null");
        return maps;

    }

    public static byte[] createByte(String jsonStr , boolean isU3d){
        byte [] jsonByte = jsonStr.getBytes(StandardCharsets.UTF_8);
        int jsonByteLen = jsonByte.length;
        byte [] data = new byte[jsonByteLen+4];
        data[0] = (byte) 0xff;
        data[1] = (byte) 0xaa;
        if(isU3d){
            data[1] = (byte) 0xbb;
        }
        if(jsonByteLen <=255) {
            data[2] = (byte) jsonByteLen;
            data[3] = (byte) 0x00;
        }
        else {
            data[2] = (byte) 0xff;
            data[3] = (byte) ((byte) 255-jsonByteLen);
        }

        for(int i =4 ;i<jsonByteLen+4;i++){
            data[i] = jsonByte[i-4];
        }
        return data;

    }


}
