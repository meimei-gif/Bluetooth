package com.acfm.ble_transform.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.acfm.ble_transform.R;
import com.acfm.ble_transform.SQLiteUtil.SqliteDao;
import com.acfm.ble_transform.reprater_fra.re_Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SafetyHatInfo extends AppCompatActivity {
        private Bundle hat;
    private Handler myHandler = new Handler()
    {
        // 2.重写消息处理函数
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                // 判断发送的消息
                case 1:
                {
                    // 更新View
                    String hatid = msg.getData().getString("hatId");
                    String high = msg.getData().getString("high");
                    // william
                    if(high == null || high.equals("")){
                        high = "?";
                    }
                    String status = msg.getData().getString("status");
                    // william
                    if(status == null || status.equals("")){
                        status = "?";
                    }
                    String temperate = msg.getData().getString("temperature");
//                    temperate = Hexstr2str(temperate);  //william
                    // william
                    if(temperate == null || temperate.equals("")){
                        temperate = "?";
                    }
                    String humidity = msg.getData().getString("humidity");
                    // william
                    if(humidity == null || humidity.equals("")){
                        humidity = "?";
                    }
                    String power = msg.getData().getString("power");
                    // william
                    if(power == null || power.equals("")){
                        power = "?";
                    }
//                    power = Hexstr2str(power);   // william
                    Long time = msg.getData().getLong("time");
                    String signal = msg.getData().getString("signalPath");
                    String rssi = msg.getData().getString("rssi");
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String sd = sdf.format(new Date(Long.parseLong(String.valueOf(time))));

                    hatid1.setText("安全帽(id:"+hatid+",信道："+signal+")");
                    status1.setText(" 状态:"+status);
                    temperate1.setText(" 温度:"+temperate+"摄氏度");
                    humidity1.setText(" 湿度:"+humidity);
                    power1.setText(" 电量:"+power+"%");
                    time1.setText(" 接收时间:"+sd);
                    high1.setText(" 相对高度:"+high);
                    rssi1.setText("信号强度："+rssi);
                    break;
                }
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };
    private boolean updatehat;
    SqliteDao sqliteDao=new SqliteDao(this);
    private TextView hatid1, mac1,status1,temperate1,humidity1,power1,time1,high1,rssi1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_hat_info);
        updatehat=false;
        // 使用bundle传递安全帽属性数据
        hat=getIntent().getExtras();
        String hatid = hat.getString("hatId");
        final String mac = hat.getString("Mac");
        String status = hat.getString("status");
        // william
        if(status == null || status.equals("")){
            status = "?";
        }
        String temperate = hat.getString("temperature");
        // william
        if(temperate == null || temperate.equals("")){
            temperate = "?";
        }
//        temperate = Hexstr2str(temperate);  //william
        String humidity = hat.getString("humidity");
        // william
        if(humidity == null || humidity.equals("")){
            humidity = "?";
        }
        String power = hat.getString("power");
        // william
        if(power == null || power.equals("")){
            power = "?";
        }
//        power = Hexstr2str(power);   //william
        Long time = hat.getLong("time");
        String signal = hat.getString("signalPath");
        String high = hat.getString("high");
        // william
        if(high == null || high.equals("")){
            high = "?";
        }
        String rssi = hat.getString("rssi");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(time))));

        // 初始化控件
        hatid1 = (TextView)findViewById(R.id.hat_id);
        mac1 = (TextView)findViewById(R.id.hat_mac);
        mac1.setTextIsSelectable(true);
        status1 = (TextView)findViewById(R.id.status);
        temperate1= (TextView)findViewById(R.id.temperate);
        humidity1 = (TextView)findViewById(R.id.humidity);
        power1 = (TextView)findViewById(R.id.power);
        time1 = (TextView)findViewById(R.id.time);
        high1 = (TextView)findViewById(R.id.high);
        rssi1 = findViewById(R.id.tv_rssi);

        hatid1.setText("安全帽(id:"+hatid+",信道："+signal+")");
        mac1.setText(" MAC地址:"+mac);
        status1.setText(" 状态:"+status);
        temperate1.setText(" 温度:"+temperate+"摄氏度");
        humidity1.setText(" 湿度:"+humidity);
        power1.setText(" 电量:"+power+"%");
        time1.setText(" 接收时间:"+sd);
        high1.setText(" 相对高度:"+high);
        rssi1.setText(" 信号强度："+rssi);
        new Thread(new Runnable() {
            public void run() {
                /*      开启一个线程
                 *       当updatehat == false 时
                 *       每十秒从数据库中读取当前mac地址对应的安全帽数据
                 *       然后调用更新函数updateview
                 * */
                while (!updatehat) {
                    try {
                        // 根据mac地址从数据库取值
                        JSONObject jsonObject1=sqliteDao.findByHatMac(mac);
                        updateview(jsonObject1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
        }).start();

    }

//    jsonObject.put("rssi",cursor.getString(cursor.getColumnIndex("rssi")) == null?"":cursor.getString(cursor.getColumnIndex("rssi")));
//            jsonObject.put("temperature",cursor.getString(cursor.getColumnIndex("temperature")) == null?"":cursor.getString(cursor.getColumnIndex("temperature")));
//            jsonObject.put("high",cursor.getString(cursor.getColumnIndex("high")) == null?"":cursor.getString(cursor.getColumnIndex("high")));
//            jsonObject.put("power",cursor.getString(cursor.getColumnIndex("power")) == null?"":cursor.getString(cursor.getColumnIndex("power")));
//            jsonObject.put("time",cursor.getLong(cursor.getColumnIndex("time")));
//            jsonObject.put("status",cursor.getString(cursor.getColumnIndex("status")) == null?"":cursor.getString(cursor.getColumnIndex("status")));
//            jsonObject.put("humidity",cursor.getString(cursor.getColumnIndex("humidity")) == null?"":cursor.getString(cursor.getColumnIndex("humidity")));
//            jsonObject.put("hatId",cursor.getString(cursor.getColumnIndex("hatId")) == null?"":cursor.getString(cursor.getColumnIndex("hatId")));
//            jsonObject.put("signalPath",cursor.getString(cursor.getColumnIndex("signalPath")) == null?"":cursor.getString(cursor.getColumnIndex("signalPath")));
//
//            db.close();
    private void updateview(JSONObject jsonObject) throws JSONException {
        Message msg = new Message();
        msg.what = 1;
        Bundle b = new Bundle();
        b.putString("rssi", jsonObject.getString("rssi"));
        b.putString("hatId", jsonObject.getString("hatId"));
        b.putString("signalPath", jsonObject.getString("signalPath"));
        b.putString("humidity", jsonObject.getString("humidity"));
        b.putString("status", jsonObject.getString("status"));
        b.putLong("time", jsonObject.getLong("time"));
        b.putString("power", jsonObject.getString("power"));
        b.putString("high", jsonObject.getString("high"));
        b.putString("temperature", jsonObject.getString("temperature"));
        msg.setData(b);
        //将连接状态更新的UI的textview上
        myHandler.sendMessage(msg);
    }

    public static String Hexstr2str(String str){
        int value = Integer.valueOf(str,16);
        String ans = String.valueOf(value);
        return ans;
    }

    @Override
    protected void onDestroy()
    {
        updatehat=true;
        super.onDestroy();
        //解除广播接收器

    }
}