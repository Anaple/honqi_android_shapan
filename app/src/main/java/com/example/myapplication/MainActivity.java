package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.percentlayout.widget.PercentRelativeLayout;

import android.annotation.SuppressLint;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.bean.DramaBean;
import com.example.myapplication.bean.MapPoint;
import com.example.myapplication.config.UIOperation;
import com.example.myapplication.model.Agreement;
import com.example.myapplication.model.MyServer;
import com.example.myapplication.bean.ConnectedCarBean;
import com.example.myapplication.callback.CallBack;
import com.example.myapplication.callback.NetWorkCallBack;
import com.zwl9517hotmail.joysticklibrary.CircleViewByImage;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public PercentRelativeLayout NetworkCheck;
    public PercentRelativeLayout CarCheck;
    public PercentRelativeLayout ExitApp;
    public PercentRelativeLayout BackApp;
    public PercentRelativeLayout map;
    public PercentRelativeLayout joy;
    public PercentRelativeLayout pointClick;
    public PercentRelativeLayout carListR;
    public PercentRelativeLayout pointStartBtn;

    public PercentRelativeLayout MainMenu;
    public PercentRelativeLayout CarMenu;
    public PercentRelativeLayout Test;
    public ListView DramaList;
    public ListView carList;
    public TextView carConnect;
    public TextView viewCar2;
    public TextView cilckPoint;

    public Button view_deafult_btn;

    public CircleViewByImage circleViewByImage;

    public static ArrayList<ConnectedCarBean> connectedCarArr = new ArrayList<>();
    public static ArrayList<DramaBean> dramaBeans = new ArrayList<>();
    public static ArrayList<MapPoint> mapPointArrayList = new ArrayList<>();
    public static ArrayList<MapPoint> carPointArrayList = new ArrayList<>();
    public static ArrayList<MapPoint> navPointArrayList = new ArrayList<>();


    public int MapH;
    public int MapW;


    public void createMapPoint(int serverW, int serverH, int pointW, int pointH, int id) {
        int w = map.getWidth();
        int h = map.getHeight()+120;
        double dbw = (double) w / (double) serverW ;
        double dbh = (double) h / (double) serverW;

        Button btn1 = new Button(this);
        btn1.setBackground(getDrawable(R.drawable.drama_btn));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(40, 40);
        layoutParams.setMargins((int) (dbw * pointW)-15, (int) (dbh * pointH), 0, 0);//4个参数按顺序分别是左上右下
        btn1.setLayoutParams(layoutParams);
        mapPointArrayList.add(new MapPoint(id, btn1, serverW, serverH));
        map.post(new Runnable() {
            @Override
            public void run() {
                map.addView(btn1);
            }
        });

        if (!mapPointArrayList.isEmpty()) {

            for (MapPoint btn : mapPointArrayList) {
                int Id = btn.getId();
                Button btnView = btn.getButton();
                btnView.setText(Id + "");
                btnView.setOnClickListener(view1 -> {
                    CarMenu.setVisibility(View.VISIBLE);
                    MainMenu.setVisibility(View.GONE);
                    viewCar2.setText("终点控制");
                    cilckPoint.setText("已选择终点" + Id);
                    pointClick.setVisibility(View.VISIBLE);
                    joy.setVisibility(View.GONE);
                    pointIdClick = Id;

                });
            }

        }
    }

    public void createNavPoint(int serverW, int serverH, int pointW, int pointH, int id) {

        int w = map.getWidth();
        int h = map.getHeight()+100+20;
        double dbw = (double) w / (double) serverW ;
        double dbh = (double) h / (double) serverW;

        Button btn1 = new Button(this);
        btn1.setBackground(getDrawable(R.drawable.nav_u3d));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(10, 10);
        layoutParams.setMargins((int) (dbw * pointW), (int) (dbh * pointH)+10, 0, 0);//4个参数按顺序分别是左上右下
        btn1.setLayoutParams(layoutParams);
        navPointArrayList.add(new MapPoint(id, btn1, serverW, serverH));

        map.post(new Runnable() {
            @Override
            public void run() {
                map.addView(btn1);
            }
        });
        Log.i("Points","w:"+w+"h:"+h+"mpw:"+dbw+"mph:"+dbh+"PointH:"+(dbh * pointH)+"pointW:"+(dbw * pointW));

    }

    public void createCarPoint(int serverW, int serverH, int pointW, int pointH, int id) {
        int w = map.getWidth();
        int h = map.getHeight()+120;
        double dbw = (double) w / (double) serverW ;
        double dbh = (double) h / (double) serverW;

        Button btn1 = new Button(this);
        btn1.setBackground(getDrawable(R.drawable.nav_cars));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(30, 30);
        layoutParams.setMargins((int) (dbw * pointW), (int) (dbh * pointH)+10, 0, 0);//4个参数按顺序分别是左上右下
        btn1.setLayoutParams(layoutParams);
        carPointArrayList.add(new MapPoint(id, btn1, serverW, serverH));
        map.post(new Runnable() {
            @Override
            public void run() {
                map.addView(btn1);
            }
        });

    }

    public void changeCarPoint(int serverW, int serverH, int x, int y, int id) {
        for (MapPoint car : carPointArrayList) {
            if (car.getId() == id) {
                int w = map.getWidth();
                int h = map.getHeight()+120;
                double dbw = (double) w / (double) serverW ;
                double dbh = (double) h / (double) serverW;
                car.setHeight(y);
                car.setWidth(x);
                delView(car.getButton());
                Button btn1 = new Button(this);
                btn1.setBackground(getDrawable(R.drawable.nav_cars));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(30, 30);
                layoutParams.setMargins((int) (dbw * x), (int) (dbh * y)+10, 0, 0);//4个参数按顺序分别是左上右下
                btn1.setLayoutParams(layoutParams);
                car.setButton(btn1);
                map.post(new Runnable() {
                    @Override
                    public void run() {
                        map.addView(btn1);
                    }
                });

                break;
            }

        }
    }

    public void delView(Button view) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null && parent instanceof ViewGroup) {

                parent.post(() -> parent.removeView(view));

            }
        }
    }

    public void delPointsAll(ArrayList<MapPoint> mapPoints) {
        for (MapPoint point : mapPoints) {
            delView(point.getButton());
        }
    }


    public void initDramaBeans() {

        dramaBeans.add(new DramaBean(R.drawable.drama2, "绿波车速引导", false, false));
        dramaBeans.add(new DramaBean(R.drawable.drama5, "闯红灯预警 ", false, false));
        dramaBeans.add(new DramaBean(R.drawable.drama3, "上高架预警", false, false));
        dramaBeans.add(new DramaBean(R.drawable.drama12, "支付提醒", false, false));
        dramaBeans.add(new DramaBean(R.drawable.drama4, "弱势交通参与者碰撞预警", false, false));
        dramaBeans.add(new DramaBean(R.drawable.drama6, "限速预警", false, false));
        dramaBeans.add(new DramaBean(R.drawable.drama9, "道路危险状况提示", false, false));
        dramaBeans.add(new DramaBean(R.drawable.drama7, "左转辅助", false, false));
        dramaBeans.add(new DramaBean(R.drawable.drama8, "交叉口碰撞预警", false, false));
        dramaBeans.add(new DramaBean(R.drawable.drama10, "车内标牌", false, false));
        dramaBeans.add(new DramaBean(R.drawable.drama9, "异常车辆提醒", false, false));
        dramaBeans.add(new DramaBean(R.drawable.drama10, "前向碰撞提醒", false, false));
        dramaBeans.add(new DramaBean(R.drawable.drama11, "紧急制动预警", false, false));
        dramaBeans.add(new DramaBean(R.drawable.drama10, "变道提醒", false, false));

    }

    public int pointIdClick = 0;

    public void initTESTCAR() {
        connectedCarArr.add(new ConnectedCarBean(1, 99, false, 1, 2));
        connectedCarArr.add(new ConnectedCarBean(2, 99, false, 1, 2));
        connectedCarArr.add(new ConnectedCarBean(3, 99, false, 1, 2));
        connectedCarArr.add(new ConnectedCarBean(4, 99, false, 1, 2));
        map.setOnClickListener(view -> {
            int w = map.getWidth();
            int h = map.getHeight();
            Log.i("Map", "w:" + w + "h:" + h + "map:" + mapPointArrayList.size());
            int serverW = 100;
            int serverH = 300;

        });
    }

    public void initSocket(SharedPreferences ipSetting) {
        MyServer.BeginConnection(netWorkCallBack,ipSetting);
        MyServer.Begin(callBack, netWorkCallBack);
        if (MyServer.MySocket != null) {
            Thread thread = new Thread(() -> {
                try {

                    MyServer.MySocket.getOutputStream().write(MyServer.createByte(Agreement.INIT_CARS, false));
                    MyServer.MySocket.getOutputStream().write(MyServer.createByte(Agreement.INIT_POINTS, false));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }

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
                carConnect.setText(getResources().getString(R.string.connected) + ":" + connectedCarArr.size());

                Toast.makeText(MainActivity.this, getResources().getString(R.string.car_connected) + "" + msg.what, Toast.LENGTH_SHORT).show();
            } else {
                carConnect.setText(getResources().getString(R.string.no_connect));
                Toast.makeText(MainActivity.this, getResources().getString(R.string.no_car_connected), Toast.LENGTH_SHORT).show();
            }
        }
    };
    @SuppressLint("HandlerLeak")
    public Handler handlerNetWorkErr = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.network_fail), Toast.LENGTH_SHORT).show();
        }
    };


    @SuppressLint("HandlerLeak")
    public Handler handlerCarItem = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.i("Car", "");
            carAdapter.notifyDataSetChanged();

        }
    };

    @SuppressLint("HandlerLeak")
    public Handler handlerCreatePoint = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {

            if (msg.what == 1) {
                Bundle bundle = msg.getData();
                MapH = bundle.getInt("mapHeight");
                MapW = bundle.getInt("mapWidth");
                Log.i("Point", "X:" + bundle.getInt("pointX") + "Y:" + bundle.getInt("pointY"));
                createMapPoint(bundle.getInt("mapWidth"), bundle.getInt("mapHeight"), bundle.getInt("pointX"), bundle.getInt("pointY"), bundle.getInt("id"));
            }

        }
    };
    @SuppressLint("HandlerLeak")
    public Handler handlerCreateCarPoint = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();
            int x = bundle.getInt("x");
            int y = bundle.getInt("y");
            int carId = bundle.getInt("id");
            if (carPointArrayList.isEmpty()) {
                createCarPoint(MapW, MapH, x, y, carId);
            } else {
                int count = 0;
                for (MapPoint carPoint : carPointArrayList) {
                    if (carPoint.getId() == carId) {
                        changeCarPoint(MapW, MapH, x, y, carId);
                        break;
                    } else {
                        count++;
                    }

                }
                if (count == carPointArrayList.size()) {
                    createCarPoint(MapW, MapH, x, y, carId);
                }
            }

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
            Looper.prepare();
            handlerNetWorkErr.handleMessage(new Message());

        }
    };

    //Socket通讯回调
    public CallBack callBack = new CallBack() {

        @Override
        public void createCar(List<Integer> carsId) {
            // int cars = connectedCarArr.size();
            if (connectedCarArr.isEmpty()) {
                for (int carId : carsId) {
                    connectedCarArr.add(new ConnectedCarBean(carId));
                    handlerCarItem.sendMessage(new Message());

                }
            } else {
                connectedCarArr.clear();
                delPointsAll(carPointArrayList);
                carPointArrayList.clear();
                for (int carId : carsId) {
                    connectedCarArr.add(new ConnectedCarBean(carId));
                    handlerCarItem.sendMessage(new Message());
                }
            }
            handler2.sendEmptyMessage(connectedCarArr.size());
        }

        @Override
        public void createPoint(List<Integer> pointsId, Map pointsPosition, Map mapSize) {

            int mapWidth = (int) mapSize.get("w");
            int mapHeight = (int) mapSize.get("h");
            for (Integer pId : pointsId) {
                Map navPointMap = (Map) pointsPosition.get(pId.toString());
                int pointX = (int) navPointMap.get("x");
                int pointY = (int) navPointMap.get("y");
                Message msg = new Message();
                msg.what = 1;
                Bundle bundle = new Bundle();
                bundle.putInt("mapWidth", mapWidth);
                bundle.putInt("mapHeight", mapHeight);
                bundle.putInt("pointX", pointX);
                bundle.putInt("pointY", pointY);
                bundle.putInt("id", pId);
                msg.setData(bundle);
                handlerCreatePoint.handleMessage(msg);
            }
        }

        @Override
        public void setCarStatus(int carId, Map carStatus) {
            for (ConnectedCarBean car : connectedCarArr) {
                if (car.getCarIndex() == carId) {
                    car.setCarBattery((Integer) carStatus.get("electricity"));
                    car.setAngle((Integer) carStatus.get("angle"));
                    car.setSpeed((Integer) carStatus.get("speed"));
                }
            }
            handlerCarItem.sendMessage(new Message());
        }

        @Override
        public void dramaFinish(int dramaId) {
            handler.sendEmptyMessage(dramaId);
        }

        @Override
        public void navPointSet(Map pointsPosition, Map mapSize, int count) {

            MapW = (int) mapSize.get("w");
            MapH = (int) mapSize.get("h");
            if (!navPointArrayList.isEmpty()) {
                delPointsAll(navPointArrayList);
                navPointArrayList.clear();
            }
            Iterator<Map.Entry<String, Map>> entries = pointsPosition.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, Map> entry = entries.next();
                int id = Integer.parseInt(entry.getKey());
                Map xy = entry.getValue();
                int x =  (int)xy.get("x");
                int y =  (int)xy.get("y");
                Log.i("Maps",x+":"+y);
                createNavPoint(MapW, MapH, x, y, id);
            }

            new Thread(() -> {
                try {
                    Log.i("DEL", "StartDel");
                    Thread.sleep(5000);
                    delPointsAll(navPointArrayList);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }).start();
        }

        @Override
        public void carPointSet(int x, int y, int carId) {
            Message msg = new Message();
            msg.what = 1;
            Bundle data = new Bundle();
            data.putInt("x", x);
            data.putInt("y", y);
            data.putInt("id", carId);
            msg.setData(data);
            handlerCreateCarPoint.handleMessage(msg);
        }

        @Override
        public void carRoad(int carId, String roadName) {
            for (ConnectedCarBean car:connectedCarArr
                 ) {
                if(car.getCarIndex() == carId){
                    car.setRoad(roadName);
                    handlerCarItem.sendMessage(new Message());
                    break;
                }
            }
        }

        @Override
        public void carTarget(int carID, String roadTarget) {
            for (ConnectedCarBean car:connectedCarArr
            ) {
                if(car.getCarIndex() == carID){
                    car.setRoadTarget(roadTarget);
                    handlerCarItem.sendMessage(new Message());
                    break;
                }
            }

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
        SharedPreferences.Editor ipSettingEdit = getSharedPreferences("data", 0).edit();
        SharedPreferences ipSetting = getSharedPreferences("data",0);
        initSocket(ipSetting);

        // initTESTCAR(); //测试函数

    }

    private void initView() {
        DramaList = findViewById(R.id.drama_choose);
        carList = findViewById(R.id.car_choose);
        NetworkCheck = findViewById(R.id.network_check);
        CarCheck = findViewById(R.id.car_check);
        Test = findViewById(R.id.car_info);
        BackApp = findViewById(R.id.back_app);
        MainMenu = findViewById(R.id.main_menu);
        CarMenu = findViewById(R.id.car_menu);
        carConnect = findViewById(R.id.car_connect);
        ExitApp = findViewById(R.id.exit_app);
        map = findViewById(R.id.map);
        joy = findViewById(R.id.joy);
        pointClick = findViewById(R.id.point_start);
        viewCar2 = findViewById(R.id.view_car_2);
        cilckPoint = findViewById(R.id.click_point);
        pointStartBtn = findViewById(R.id.point_start_btn);
        carListR = findViewById(R.id.car_list);

        view_deafult_btn = findViewById(R.id.view_change_deafult);
        circleViewByImage = findViewById(R.id.joystick_view);
        DramaList.setAdapter(DramaAdapter);
        carList.setAdapter(carAdapter);
        circleViewByImage.setCallback(callback2);


    }

    //系统命令模块按钮
    @SuppressLint("NewApi")
    private void initOnClickListener() {
        NetworkCheck.setOnClickListener(view -> {
            if (MyServer.MySocket != null) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.network_success), Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences ipSetting = getSharedPreferences("data",0);
                initSocket(ipSetting);
                try {
                    Thread.currentThread().sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (MyServer.MySocket != null) {

                    Toast.makeText(MainActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.network_success), Toast.LENGTH_SHORT).show();
                }
                connectedCarArr.clear();
                handler2.sendEmptyMessage(connectedCarArr.size());
                carAdapter.notifyDataSetChanged();
            }
        });
        CarCheck.setOnClickListener(view -> {
            if (MyServer.MySocket != null) {
                new Thread(() -> {

                    try {
                        MyServer.MySocket.getOutputStream().write(MyServer.createByte(Agreement.INIT_CARS, false));
                        MyServer.MySocket.getOutputStream().write(MyServer.createByte(Agreement.INIT_POINTS, false));

                    } catch (Exception e) {
                        Log.e("CarClick", "SOCKET", e);
                    }
                }).start();

            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();

            }
        });

        ExitApp.setOnClickListener(view -> {

            super.finishAffinity();
            System.exit(0);

        });
        BackApp.setOnClickListener(view -> {
            MainMenu.setVisibility(View.VISIBLE);
            CarMenu.setVisibility(View.GONE);
            joy.setVisibility(View.VISIBLE);
            pointClick.setVisibility(View.GONE);
            viewCar2.setText("视角控制");
        });
        Test.setOnClickListener(view -> {
            if (MyServer.MySocket != null && !connectedCarArr.isEmpty()) {
                MainMenu.setVisibility(View.GONE);
                CarMenu.setVisibility(View.VISIBLE);
                joy.setVisibility(View.VISIBLE);
                pointClick.setVisibility(View.GONE);
                viewCar2.setText("视角控制");
            } else {

                Toast.makeText(MainActivity.this, getResources().getString(R.string.no_car_live), Toast.LENGTH_SHORT).show();

            }
        });

        view_deafult_btn.setOnClickListener(view -> {
            if (MyServer.MySocket != null && clickCarId != 0) {
                new Thread(() -> {
                    try {
                        MyServer.MySocket.getOutputStream().write(MyServer.createByte(Agreement.U3D_VIEW(clickCarId, 0), true));
                    } catch (Exception e) {

                    }
                }

                ).start();
            }
        });
        pointStartBtn.setOnClickListener(view -> {
            if (MyServer.MySocket != null && clickCarId != 0) {
                new Thread(() -> {
                    try {
                        MyServer.MySocket.getOutputStream().write(MyServer.createByte(Agreement.NAV_END(pointIdClick, clickCarId), false));
                    } catch (Exception e) {

                    }
                }

                ).start();
            }
        });


    }


    //情景部分
    public static int CurrentDrama = dramaBeans.size();
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
            @SuppressLint("ViewHolder") View view = View.inflate(MainActivity.this, R.layout.item_drama, null);
            ImageView img = view.findViewById(R.id.image_ico);
            ImageView OnOff = view.findViewById(R.id.on_off);
            TextView drama = view.findViewById(R.id.drama);

            OnOff.setOnClickListener(v -> {
                if (MyServer.MySocket != null) {
                    new Thread(() -> {
                        try {
                            byte[] data = MyServer.createByte(Agreement.DRAMA(position + 1), false);
                            CurrentDrama = 6;
                            if (!getItem(position).isStop && !getItem(position).isPending) {
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
                            } else {
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.drama_wait), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            Log.e("SYSTEM_CLICK", "SOCKET", e);
                        }
                    }).start();
                } else {
                    if (!getItem(position).isStop && !getItem(position).isPending) {
                        CurrentDrama = 6;
                        for (int i = 0; i < CurrentDrama; i++) {

                            getItem(i).isStop = false;
                            getItem(i).isPending = true;

                        }
                        DramaAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.drama_wait), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.drama_wait), Toast.LENGTH_SHORT).show();

                    }
                }
            });

            img.setImageResource(getItem(position).DramaImgId);
            drama.setText(getItem(position).DramaName);
            if (getItem(position).isPending && !getItem(position).isStop) {
                OnOff.setImageResource(R.drawable.pending);
            }
            if (getItem(position).isStop && !getItem(position).isPending) {
                OnOff.setImageResource(R.drawable.stop);
            } else if (!getItem(position).isStop && !getItem(position).isPending) {
                OnOff.setImageResource(R.drawable.start);
            }
            return view;
        }
    };

    public int clickCarId = 0;

    public BaseAdapter carAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return connectedCarArr.size();
        }

        @Override
        public ConnectedCarBean getItem(int position) {
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
            View view1 = view.findViewById(R.id.car_list);
            TextView carTitle = view.findViewById(R.id.car_title);
            TextView carBattery = view.findViewById(R.id.car_battery);
            TextView carSpeed = view.findViewById(R.id.car_speed);
            TextView carNow = view.findViewById(R.id.car_now);
            TextView carWill = view.findViewById(R.id.car_will);

            if(connectedCarArr.get(position).getRoad() != null){
                carNow.setText("位置:"+connectedCarArr.get(position).getRoad());
            }

            if(connectedCarArr.get(position).getRoadTarget() != null){
                carWill.setText("目的:"+connectedCarArr.get(position).getRoadTarget());
            }
            view.setOnClickListener(v -> {

                clickCarId = connectedCarArr.get(position).getCarIndex();
                connectedCarArr.get(position).setOnclick(true);


                for (ConnectedCarBean obj : connectedCarArr) {
                    if (obj.getCarIndex() != connectedCarArr.get(position).getCarIndex()) {
                        obj.setOnclick(false);
                    }
                }
                carAdapter.notifyDataSetChanged();
            });
            if (connectedCarArr.get(position).isOnclick()) {
                view1.setBackgroundResource(R.drawable.item_blue_selected);
            } else {
                view1.setBackgroundResource(R.drawable.button_circle_shape_item);
            }
            carTitle.setText("车" + connectedCarArr.get(position).getCarIndex() + "  " + "  ");
            carBattery.setText("电量:" + connectedCarArr.get(position).getCarBattery() + "%");
            carSpeed.setText("速度:" + connectedCarArr.get(position).getSpeed() + "km/h");
            return view;

        }


    };

    private CircleViewByImage.ActionCallback callback2 = new CircleViewByImage.ActionCallback() {


        @Override
        public void forwardMove() {

            Log.i("MOVE", "up");
            try {
                if (MyServer.MySocket != null && clickCarId != 0) {
                    new Thread(() -> {
                        byte[] data = MyServer.createByte(Agreement.U3D_VIEW(clickCarId, 1), true);
                        try {
                            MyServer.MySocket.getOutputStream().write(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (Exception e) {

            }
        }

        @Override
        public void backMove() {
            Log.i("MOVE", "down");
            try {
                if (MyServer.MySocket != null && clickCarId != 0) {
                    new Thread(() -> {
                        byte[] data = MyServer.createByte(Agreement.U3D_VIEW(clickCarId, 2), true);
                        try {
                            MyServer.MySocket.getOutputStream().write(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (Exception e) {

            }
        }

        @Override
        public void leftMove() {
            Log.i("MOVE", "left");
            try {
                if (MyServer.MySocket != null && clickCarId != 0) {
                    new Thread(() -> {
                        byte[] data = MyServer.createByte(Agreement.U3D_VIEW(clickCarId, 3), true);
                        try {
                            MyServer.MySocket.getOutputStream().write(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (Exception e) {

            }
        }

        @Override
        public void rightMove() {
            Log.i("MOVE", "right");
            try {
                if (MyServer.MySocket != null && clickCarId != 0) {
                    new Thread(() -> {
                        byte[] data = MyServer.createByte(Agreement.U3D_VIEW(clickCarId, 4), true);
                        try {
                            MyServer.MySocket.getOutputStream().write(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (Exception e) {

            }
        }

        @Override
        public void centerMove() {
            Log.i("MOVE", "5");
        }

        @Override
        public void centerClick() {
            Log.i("MOVE", "6");
        }

        @Override
        public void actionUp() {
            Log.i("MOVE", "7");
        }
    };


}