package com.acfm.ble_transform.UI;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.acfm.ble_transform.R;

public class BoardHatInfo extends AppCompatActivity {
    Bundle b = new Bundle();
    public static String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    private TextView hatid1, mac1, status1, temperate1, env1, power1, eva1, high1, rssi1, hujiu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boardhatinfo);
        b = getIntent().getExtras();
        String uuid = b.getString(EXTRAS_DEVICE_NAME);
        // 初始化控件
        mac1 = (TextView) findViewById(R.id.bhat_mac);
        mac1.setTextIsSelectable(true);
        status1 = (TextView) findViewById(R.id.bstatus);
        rssi1 = (TextView) findViewById(R.id.btv_rssi);
        hatid1 = (TextView) findViewById(R.id.bname);
        temperate1 = (TextView) findViewById(R.id.btemperate);
        power1 = (TextView) findViewById(R.id.bpower);
        high1 = (TextView) findViewById(R.id.bhigh);
        env1 = (TextView) findViewById(R.id.benv);
        eva1 = (TextView) findViewById(R.id.beva);
        hujiu = (TextView) findViewById(R.id.bhujiu);

        if (uuid != null) {
            String[] res = uuid.split("!");
            mac1.setText(" MAC:" + res[0]);
            rssi1.setText(" 信号强度:" + res[1] + "dBm");
            hatid1.setText(res[2] + "id:" + res[3]);
            env1.setText(" 工作环境:" + res[4]);
            status1.setText(" 工作状态:" + res[5]);
            eva1.setText(" EVA:" + res[6]);
            hujiu.setText(" 呼救及运动:" + res[7]);
            temperate1.setText(" 温度:" + res[8] + "摄氏度");
            power1.setText(" 电量:" + res[9] + "%");
            high1.setText(" 高度:" + res[10] + "dm");
        }
    }
}
