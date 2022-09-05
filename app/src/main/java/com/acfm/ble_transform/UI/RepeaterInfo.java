package com.acfm.ble_transform.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.acfm.ble_transform.R;
import com.acfm.ble_transform.SQLiteUtil.SqliteDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RepeaterInfo extends AppCompatActivity {
    private Bundle repeater;
    SqliteDao sqliteDao=new SqliteDao(this);
    private TextView re_peaterid1, re_temperate1,re_time1,re_high1,re_worktime1;
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

                    String high = msg.getData().getString("high");
                    String temperate = msg.getData().getString("temperature");
                    Long time = msg.getData().getLong("time");
                    String worktime = msg.getData().getString("worktime");
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String sd = sdf.format(new Date(Long.parseLong(String.valueOf(time))));
                    re_temperate1.setText(" 温度:"+temperate+"摄氏度");
                    re_worktime1.setText(" 工作时间:"+worktime);
                    re_time1.setText(" 接收时间:"+sd);
                    re_high1.setText(" 相对高度:"+high);
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
        setContentView(R.layout.activity_repeaterinfo);
        updaterepeater=false;
        // 获取传输过来的信息
        repeater=getIntent().getExtras();
        // 根据映射取值
        final String repeaterid = repeater.getString("repeaterId");
        String temperate = repeater.getString("temperature");
        String worktime = repeater.getString("worktime");
        String high = repeater.getString("high");
        Long time = repeater.getLong("time");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(Long.parseLong(String.valueOf(time))));
        // 获取控件id
        re_peaterid1 = (TextView)findViewById(R.id.repeaterId);
        re_temperate1= (TextView)findViewById(R.id.re_temperate);
        re_worktime1 = (TextView)findViewById(R.id.re_worktime);
        re_time1 = (TextView)findViewById(R.id.re_time);
        re_high1 = (TextView)findViewById(R.id.re_high);

        re_peaterid1.setText("中继器id:"+repeaterid);
        re_temperate1.setText(" 温度:"+temperate+"摄氏度");
        re_worktime1.setText(" 工作时间:"+worktime);
        re_time1.setText(" 接收时间:"+sd);
        re_high1.setText(" 相对高度:"+high);
        new Thread(new Runnable() {
            public void run() {
                /*
                 *   开启一个线程
                 *   每十秒更新一次中继器指定设备详细数据信息，即更新页面
                 * */
                while (!updaterepeater) {
                    try {
                        JSONObject jsonObject1=sqliteDao.findByRepeaterId(repeaterid);
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

        b.putString("worktime", jsonObject.getString("worktime"));
        b.putLong("time", jsonObject.getLong("time"));
        b.putString("high", jsonObject.getString("high"));
        b.putString("temperature", jsonObject.getString("temperature"));
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
