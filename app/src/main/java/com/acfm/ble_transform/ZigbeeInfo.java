package com.acfm.ble_transform;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.acfm.ble_transform.SQLiteUtil.SqliteDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ZigbeeInfo extends AppCompatActivity {
    private Bundle zigbee;
    SqliteDao sqliteDao=new SqliteDao(this);
    private TextView zigbeeId, zigbee_signalPath,zigbee_time,zigbee_seq;
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

                    String signalPath = msg.getData().getString("signalPath");
                    Long time = msg.getData().getLong("time");
                    String seq = msg.getData().getString("seq");
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String sd = sdf.format(new Date(Long.parseLong(String.valueOf(time))));
                    zigbee_signalPath.setText(" 信号:"+signalPath);
                    zigbee_time.setText(" 接收时间:"+sd);
                    zigbee_seq.setText(" 序列号:"+seq);
                    break;
                }
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };
    private boolean updaterepeater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zigbeeinfo);
        updaterepeater=false;
        // 获取传输过来的信息
        zigbee = getIntent().getExtras();
        // 根据映射取值
        Log.d(Constants.TAG,"Zigbeeinfo -> onCreate ");
        final String zigbee_id = zigbee.getString("zigbeeId");
        final String signalPath = zigbee.getString("signalPath");
        String seq = zigbee.getString("seq");
        Long time = zigbee.getLong("time");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(time))));
        // 获取控件id
        zigbeeId = (TextView)findViewById(R.id.zigbeeId);
        zigbee_signalPath= (TextView)findViewById(R.id.zigbee_signalPath);
        zigbee_time = (TextView)findViewById(R.id.zigbee_time);
        zigbee_seq = (TextView)findViewById(R.id.zigbee_seq);

        zigbeeId.setText("协调器 "+zigbee_id);
        zigbee_signalPath.setText(" 信号:"+signalPath);
        zigbee_time.setText(" 接收时间:"+sd);
        zigbee_seq.setText(" 序列号:"+seq);

        new Thread(new Runnable() {
            public void run() {
                /*
                 *   开启一个线程
                 *   每十秒更新一次中继器指定设备详细数据信息，即更新页面
                 * */
                while (!updaterepeater) {
                    try {
                        JSONObject jsonObject1=sqliteDao.findZigbeeSignal(signalPath);
                        updaterepeaterview(jsonObject1);
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

    private void updaterepeaterview(JSONObject jsonObject) throws JSONException {
        Message msg = new Message();
        msg.what = 1;
        Bundle b = new Bundle();// 利用bundle存储从数据库获取的数据

        b.putString("signalPath", jsonObject.getString("signalPath"));
        b.putLong("time", jsonObject.getLong("time"));
        b.putString("seq", jsonObject.getString("seq"));
        msg.setData(b);
        //将连接状态更新的UI的textview上
        myHandler.sendMessage(msg); // 通知handle更新数据
    }
    @Override
    protected void onDestroy()
    {
        updaterepeater=true;
        super.onDestroy();
        //解除广播接收器

    }
}
