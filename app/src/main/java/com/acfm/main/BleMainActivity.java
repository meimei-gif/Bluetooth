package com.acfm.main;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.acfm.ble_beacon.UI.activities.BeaconMainActivity;
import com.acfm.ble_beacon.UI.activities.OtherBeaconDeviceActivity;
import com.acfm.ble_beacon.UI.activities.SafetyHatMainActivity;
import com.acfm.ble_transform.R;
import com.acfm.ble_transform.UI.MainActivity;

public class BleMainActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;

    int REQUEST_ENABLE_BT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_main);
        initBle();
        ImageButton button1 = findViewById(R.id.button_1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(BleMainActivity.this, SafetyHatMainActivity.class);
                startActivity(intent);
            }
        });
        ImageButton button2 = findViewById(R.id.button_2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(BleMainActivity.this, BeaconMainActivity.class);
                startActivity(intent);
            }
        });
        ImageButton button3 = findViewById(R.id.button_3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BleMainActivity.this, OtherBeaconDeviceActivity.class);
                startActivity(intent);
            }
        });
        ImageButton button4 = findViewById(R.id.button_4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(BleMainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        ImageButton btn =(ImageButton) findViewById(R.id.about_edition);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BleMainActivity.this);
                builder.setTitle("设备版本号说明");
                builder.setMessage("本系统为1.5版本，修复问题包含有：版本号说明、云端上传、实时刷新等\n时间:2022年8月31日");
                builder.setCancelable(true);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
            builder.show();
            }
        });


    }
    private void initBle() {
        // 手机硬件支持蓝牙
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            //android4.0以上版本的模块判断函数 getPackageManager（）.hasSystemFeature（String string）判断是否含有特定的模块功能
            Toast.makeText(this, "不支持BLE", Toast.LENGTH_SHORT).show();
            finish();
        }
        // Initializes Bluetooth adapter.


        // 获取手机本地的蓝牙适配器
        //对蓝牙的操作主要有：开启和关闭蓝牙、搜索周边设备、能被周边设备所发现、获取配对设备、蓝牙设备间的数据传输
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // 打开蓝牙权限
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //跳转到enableBtIntent，并且requestCode=1
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        //打开定位权限
        LocationManager lm = (LocationManager) BleMainActivity.this.getSystemService(BleMainActivity.this.LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//开了定位服务
            if (ContextCompat.checkSelfPermission(BleMainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e("BRG", "没有权限");
                // 没有权限，申请权限。
                // 申请授权。
                ActivityCompat.requestPermissions(BleMainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//                        Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();

            } else {

                // 有权限了
//                        Toast.makeText(getActivity(), "有权限", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("BRG", "系统检测到未开启GPS定位服务");
            Toast.makeText(BleMainActivity.this, "系统检测到未开启GPS定位服务", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            //启动开启定位的服务
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1315);
        }
    }


}
