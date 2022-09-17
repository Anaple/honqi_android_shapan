package com.example.myapplication;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.percentlayout.widget.PercentRelativeLayout;
import android.annotation.SuppressLint;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.bean.DramaBean;
import com.example.myapplication.config.UIOperation;
import com.example.myapplication.model.Agreement;
import com.example.myapplication.model.MyServer;
import com.example.myapplication.bean.ConnectedCarBean;
import com.example.myapplication.callback.CallBack;
import com.example.myapplication.callback.NetWorkCallBack;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public PercentRelativeLayout NetworkCheck;
    public PercentRelativeLayout CarCheck;
    public PercentRelativeLayout ExitApp;
    public PercentRelativeLayout BackApp;

    public PercentRelativeLayout MainMenu;
    public PercentRelativeLayout CarMenu;
    public PercentRelativeLayout Test;
    public ListView DramaList;
    public ListView carList;
    public TextView carBattery;
    public TextView carConnect;

    public static ArrayList<ConnectedCarBean> connectedCarArr = new ArrayList<>();
    public static ArrayList<DramaBean> dramaBeans = new ArrayList<>();


    public void initDramaBeans(){

        dramaBeans.add(new DramaBean(R.drawable.drama_a1_icon,"灌装测试床",false,false));
        dramaBeans.add(new DramaBean(R.drawable.drama_a2_icon,"车人测试床",false,false));
        dramaBeans.add(new DramaBean(R.drawable.drama_a3_icon,"车内测试床",false,false));
        dramaBeans.add(new DramaBean(R.drawable.drama_a4_icon,"车际测试床-车路通信",false,false));
        dramaBeans.add(new DramaBean(R.drawable.drama_a5_icon,"车际测试床-车车通信",false,false));
        dramaBeans.add(new DramaBean(R.drawable.drama_a6_icon,"车云测试床",false,false));

    }
    public void initTESTCAR(){
        connectedCarArr.add(new ConnectedCarBean(1,99,false,1,2));
        connectedCarArr.add(new ConnectedCarBean(1,99,false,1,2));
        connectedCarArr.add(new ConnectedCarBean(1,99,false,1,2));
        connectedCarArr.add(new ConnectedCarBean(1,99,false,1,2));


    }
    public void initSocket(){
        MyServer.BeginConnection(netWorkCallBack);
        MyServer.Begin(callBack);
        if(MyServer.MySocket != null){
            try {
                MyServer.MySocket.getOutputStream().write(MyServer.createByte(Agreement.INIT_CARS,false));
                MyServer.MySocket.getOutputStream().write(MyServer.createByte(Agreement.INIT_POINTS,false));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public final static int [] PIC_SRC={R.drawable.deafult_drama,R.drawable.drama_pic_1,R.drawable.drama_pic_2,R.drawable.drama_pic_3,R.drawable.drama_pic_4,R.drawable.drama_pic_5,R.drawable.drama_pic_6};
    public final static int [] STRING_SRC = {R.string.deafult_text,R.string.drama_text_1,R.string.drama_text_2,R.string.drama_text_3,R.string.drama_text_4,R.string.drama_text_5,R.string.drama_text_6};
    @SuppressLint("HandlerLeak")
    public final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            dramaBeans.clear();
            initDramaBeans();
            DramaAdapter.notifyDataSetChanged();
        }
    };

    public final Handler handlerDrama = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            DramaAdapter.notifyDataSetChanged();
        }
    };


    @SuppressLint("HandlerLeak")
    public Handler handler2 = new Handler() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what > 0) {
                //有效车辆
//                carConnectIcon.setTextColor(getResources().getColor(R.color.start));
                carConnect.setText(R.string.connected);
                if (MyServer.MySocket != null) {
                    new Thread(() -> {

                        try {
                            MyServer.MySocket.getOutputStream().write(Agreement.getBattery((byte) 0));

                        } catch (Exception e) {
                            Log.e("CarClick", "SOCKET", e);
                        }
                    }).start();
                }

                Toast.makeText(MainActivity.this, getResources().getString(R.string.car_connected) + "" + msg.what, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.no_car_connected), Toast.LENGTH_SHORT).show();
            }
        }
    };


    @SuppressLint("HandlerLeak")
    public  Handler handler3 = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.cant_use_car) + "", Toast.LENGTH_SHORT).show();
        }
    };

    @SuppressLint("HandlerLeak")
    public  Handler handlerCarItem = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.i("Car","");
           carAdapter.notifyDataSetChanged();

        }
    };

    @SuppressLint("HandlerLeak")
    public  Handler handlerCarStatus = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            carBattery.setText("未连接");
//            carConnectIcon.setTextColor(getResources().getColor(R.color.gray));
            carConnect.setText("未连接");

        }
    };


    //网络检查

    public NetWorkCallBack netWorkCallBack = new NetWorkCallBack() {
        @Override
        public void success(Socket socket) {

            Log.i("SOCKET", getResources().getString(R.string.network_success));
            MyServer.MySocket = socket;
            try {

            } catch (Exception e) {
                Log.e("CarClick", "SOCKET", e);
            }
        }
        @Override
        public void error() {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            carBattery.setText(getResources().getString(R.string.network_error));
        }
    };

    //Socket通讯回调
    public CallBack callBack = new CallBack() {

        @Override
        public void createCar(List<Integer> carsId) {

            if(connectedCarArr.isEmpty()){
                for (int carId:
                     carsId) {
                    connectedCarArr.add(new ConnectedCarBean(carId));
                    handlerCarItem.sendMessage(new Message());

                }
            }else {

                for (int carId: carsId) {
                    int count =0;
                    for(int i =0; i<connectedCarArr.size();i++){
                        if(connectedCarArr.get(i).getCarIndex() ==carId){
                            count++;
                        }
                    }
                    if(count == connectedCarArr.size()){
                        connectedCarArr.add(new ConnectedCarBean(carId));
                    }
                }
            }

        }

        @Override
        public void createPoint(int[] pointsId, Map pointsPosition) {

        }

        @Override
        public void setCarStatus(int carId, Map carStatus) {
            for(ConnectedCarBean car:connectedCarArr){
                if(car.getCarIndex() == carId){
                    car.setCarBattery((Integer) carStatus.get("electricity"));
                    car.setAngle((Integer) carStatus.get("angle"));
                    car.setSpeed((Integer) carStatus.get("speed"));

                }
            }
            handlerCarItem.sendMessage(new Message());

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UIOperation.SetFullScreen(this);
        initView();
        initOnClickListener();
        initDramaBeans();
        initSocket();



    }

    private void initView() {
        DramaList = findViewById(R.id.drama_choose);
        carList = findViewById(R.id.car_choose);
        NetworkCheck = findViewById(R.id.network_check);
        CarCheck = findViewById(R.id.car_check);
        carBattery = findViewById(R.id.car_battery);
        Test = findViewById(R.id.car_info);
        BackApp = findViewById(R.id.back_app);
        MainMenu = findViewById(R.id.main_menu);
        CarMenu = findViewById(R.id.car_menu);
        carConnect =findViewById(R.id.car_connect);
        ExitApp = findViewById(R.id.exit_app);

        DramaList.setAdapter(DramaAdapter);
        carList.setAdapter(carAdapter);



    }

    //系统命令模块按钮
    @SuppressLint("NewApi")
    private void initOnClickListener() {

        NetworkCheck.setOnClickListener(view -> {
            if (MyServer.MySocket != null) {



                Toast.makeText(MainActivity.this, getResources().getString(R.string.network_success), Toast.LENGTH_SHORT).show();
            } else {

                initSocket();

                Toast.makeText(MainActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                connectedCarArr.clear();
            }
        });
        CarCheck.setOnClickListener(view -> {
            if (MyServer.MySocket != null) {
                new Thread(() -> {

                    try {
                        MyServer.MySocket.getOutputStream().write(MyServer.createByte(Agreement.INIT_CARS,false));
                        MyServer.MySocket.getOutputStream().write(MyServer.createByte(Agreement.INIT_POINTS,false));

                    } catch (Exception e) {
                        Log.e("CarClick", "SOCKET", e);
                    }
                }).start();

            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();

            }
        });

        ExitApp.setOnClickListener(view ->{

            super.finishAffinity();
            System.exit(0);

        });
        BackApp.setOnClickListener( view ->{
            MainMenu.setVisibility(View.VISIBLE);
            CarMenu.setVisibility(View.GONE);
        });
        Test.setOnClickListener( view -> {
            MainMenu.setVisibility(View.GONE);
            CarMenu.setVisibility(View.VISIBLE);
        });


    }


    //情景部分
    public  static int CurrentDrama = dramaBeans.size();
    // 情景列表
    public BaseAdapter DramaAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return dramaBeans.size();
        }

        @Override
        public DramaBean getItem(int position) {
            return dramaBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint({"SetTextI18n", "ResourceType"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint("ViewHolder")
            View view = View.inflate(MainActivity.this, R.layout.item_drama, null);
            ImageView img = view.findViewById(R.id.image_ico);
            ImageView OnOff = view.findViewById(R.id.on_off);
            TextView drama = view.findViewById(R.id.drama);

            OnOff.setOnClickListener(v -> {
                if (MyServer.MySocket != null)
                {
                    new Thread(() -> {
                        try {
                            byte[] data = Agreement.getDrama((byte) (position + 1));
                            CurrentDrama = 6;
                            if(!getItem(position).isStop&&!getItem(position).isPending) {
                                for (int i = 0; i < CurrentDrama; i++) {
                                    if (i == position) {
                                        getItem(position).isStop = true;
                                        getItem(position).isPending = false;
                                    } else {
                                        getItem(i).isStop = false;
                                        getItem(i).isPending = true;
                                    }

                                }
                                handlerDrama.sendMessage(new Message());
                                MyServer.MySocket.getOutputStream().write(data);
                            }else {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.drama_wait), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            Log.e("SYSTEM_CLICK", "SOCKET", e);
                        }
                    }).start();
                } else {
                    if(!getItem(position).isStop&&!getItem(position).isPending){
                        CurrentDrama = 6;
                        for (int i=0 ; i<CurrentDrama ;i++){
                            if(i==position){
                                getItem(position).isStop = true;
                                getItem(position).isPending =false;
                            }else {
                                getItem(i).isStop =false;
                                getItem(i).isPending = true;
                            }
                        }
                        DramaAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.drama_wait), Toast.LENGTH_SHORT).show();
                    }else {

                    }

                }
            });

            img.setImageResource(getItem(position).DramaImgId);
            drama.setText(getItem(position).DramaName);
            if(getItem(position).isPending&&!getItem(position).isStop){
                OnOff.setImageResource(R.drawable.pending);
            }
            if(getItem(position).isStop&&!getItem(position).isPending){
                OnOff.setImageResource(R.drawable.stop);
            }else if(!getItem(position).isStop && !getItem(position).isPending) {
                OnOff.setImageResource(R.drawable.start);
            }
            return view;
        }
    };

    public int  clickCarId  = 0;

    public BaseAdapter carAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return connectedCarArr.size();
        }

        @Override
        public  ConnectedCarBean getItem(int position) {
            return connectedCarArr.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint({"SetTextI18n", "ResourceType"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(MainActivity.this, R.layout.item_car, null);
            TextView carTitle = view.findViewById(R.id.car_title);
            view.setOnClickListener( v->{

                clickCarId = position;
                connectedCarArr.get(position).setOnclick(true);

                for (ConnectedCarBean obj:connectedCarArr) {
                    if(obj.getCarIndex() != connectedCarArr.get(position).getCarIndex() ){
                        obj.setOnclick(false);
                    }
                }
                carAdapter.notifyDataSetChanged();
            });
            if(connectedCarArr.get(position).isOnclick()){
                view.setBackgroundResource(R.drawable.button_circle_shape_item_blue);
            }else {
                view.setBackgroundResource(R.drawable.button_circle_shape_item);
            }
            carTitle.setText("车"+connectedCarArr.get(position).getCarIndex()+"    "+"电量剩余:"+connectedCarArr.get(position).getCarBattery()+"%");
            return view;

        }


    };


}