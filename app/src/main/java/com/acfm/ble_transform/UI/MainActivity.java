package com.acfm.ble_transform.UI;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.acfm.ble_transform.BleActivity;
import com.acfm.ble_transform.MyCountDownTimer;
import com.acfm.ble_transform.R;
import com.acfm.ble_transform.SQLiteUtil.SqliteDao;
import com.acfm.ble_transform.Service.ScanResultsConsumer;
import com.acfm.ble_transform.bluetooth.Scanner;

import org.json.JSONException;

import java.util.ArrayList;



/**
 * @Description: MainActivity类实现打开蓝牙 、 扫描蓝牙
 */
public class MainActivity extends Activity implements OnClickListener, ScanResultsConsumer {
    private Scanner bleScanner;


    private Button scanBtn;
    private Button helpBtn;
    private TextView scannedTime;

    private boolean periodFlag;
    private SqliteDao sqliteDao;

    private String deuuid;
    // 蓝牙适配器
    BluetoothAdapter mBluetoothAdapter;
    // 蓝牙信号强度
    private ArrayList<Integer> deviceRssiList;
    // 自定义Adapter
    LeDeviceListAdapter mleDeviceListAdapter;
    // listview显示扫描到的蓝牙信息
    ListView lv;

    private MyCountDownTimer countDownTimer;

    //
    private boolean ble_scanning;

    int REQUEST_ENABLE_BT = 1;
    // 蓝牙扫描时间
    private static final long SCAN_PERIOD = 20000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        bleScanner = new Scanner(this.getApplicationContext());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        );
        // 初始化控件
        initWidget();
        // 初始化蓝牙
        initBle();

        sqliteDao = new SqliteDao(this);
        long lastTime = sqliteDao.findLastTime();
        if (lastTime != 0) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime > 24 * 60 * 60 * 1000) {
                sqliteDao.clearFeedTable("historySafetyHat");
            }
        }

        /* listview点击函数 */
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                    long id) {

                final BluetoothDevice device = mleDeviceListAdapter
                        .getDevice(position);

                if (device == null)
                    return;
                if (bleScanner.isHelmet(device.getName())) {
                    // Helmet安全帽
                    final Intent intent = new Intent(MainActivity.this, BoardHatInfo.class);
                    try {
                        intent.putExtra(
                                BleActivity.EXTRAS_DEVICE_NAME,
                                bleScanner.getHelmetMap().getString(device.getAddress())
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        // 启动BoardHatInfoActivity
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                        // TODO: handle exception
                    }
                } else { //MONITOR

                    final Intent intent = new Intent(MainActivity.this,
                            BleActivity.class);
//                    intent.putExtra(BleActivity.EXTRAS_DEVICE_NAME,
//                            device.getName());
                    intent.putExtra(BleActivity.EXTRAS_DEVICE_ADDRESS,
                            device.getAddress());
//                    intent.putExtra(BleActivity.EXTRAS_DEVICE_RSSI,
//                            deviceRssiList.get(position).toString());
                    try {
                        // 启动Ble_Activity
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // TODO: handle exception
                    }
                }
//                if (mScanning) {
//                    /* 停止扫描设备 */
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    mScanning = false;
//                }
            }
        });

    }

    /*

    程序正常启动时：onCreate()->onStart()->onResume();
    一个Activity启动另一个Activity: onPause()->onStop(),再返回：onRestart()->onStart()->onResume()
    程序按back 退出： onPause()->onStop()->onDestory(),再进入：onCreate()->onStart()->onResume();
    程序按home 退出： onPause()->onStop(),再进入：onRestart()->onStart()->onResume();

     */
    @Override
    public void onStart() {
        super.onStart();
        bleScanner.startTimedScanning(this, SCAN_PERIOD);
    }

    public void onStop() {
        super.onStop();
        mleDeviceListAdapter.notifyDataSetChanged();
        new Thread(new Runnable() {
            @Override
            public void run() {

                mleDeviceListAdapter.clear();


            }
        }).start();
        bleScanner.stopTimedScanning();
    }

    /**
     * @Title: initWidget
     * @Description: 初始化UI控件
     */
    private void initWidget() {
        scannedTime = (TextView) findViewById(R.id.periodscan);

        scanBtn = this.findViewById(R.id.scan_dev_btn);
        scanBtn.setOnClickListener(this);

        lv = (ListView) this.findViewById(R.id.lv);
        // 自定义适配器
        mleDeviceListAdapter = new LeDeviceListAdapter();
        lv.setAdapter(mleDeviceListAdapter);

        helpBtn = (Button) findViewById(R.id.help);
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * @Title: initBle
     * @Description: 初始化蓝牙
     */
    private void initBle() {
        // 手机硬件支持蓝牙
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "不支持BLE", Toast.LENGTH_SHORT).show();
            finish();
        }
        // Initializes Bluetooth adapter.


        // 获取手机本地的蓝牙适配器
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // 打开蓝牙权限
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        //打开定位权限
        LocationManager lm = (LocationManager) MainActivity.this.getSystemService(MainActivity.this.LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//开了定位服务
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e("BRG", "没有权限");
                // 没有权限，申请权限。
                // 申请授权。
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//                        Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();

            } else {

                // 有权限了
//                        Toast.makeText(getActivity(), "有权限", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("BRG", "系统检测到未开启GPS定位服务");
            Toast.makeText(MainActivity.this, "系统检测到未开启GPS定位服务", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1315);
        }
    }


    /*
     * 按钮响应事件
     */
    @Override
    public void onClick(View v) {

        if (ble_scanning) {
            bleScanner.stopScanning();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mleDeviceListAdapter.clear();
                    mleDeviceListAdapter.notifyDataSetChanged();
                }
            });

            bleScanner.startScanning(MainActivity.this, SCAN_PERIOD);
        }
    }

    @Override
    public void candidateDevice(final BluetoothDevice device, byte[] scan_record, final int rssi) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mleDeviceListAdapter.addDevice(device, rssi);
                mleDeviceListAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void scanningStarted() {
        setScanState(true);
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
        countDownTimer = new MyCountDownTimer(scannedTime, SCAN_PERIOD, 1000);
        countDownTimer.start();
    }

    @Override
    public void scanningStopped() {
        setScanState(false);
        countDownTimer.cancel();
        scannedTime.setText("20s");
    }

    private void setScanState(boolean flag) {
        ble_scanning = flag;
        scanBtn.setText(ble_scanning ? getResources().getString(R.string.STOP_SCANNING) : getResources().getString(R.string.FIND));
    }

    /**
     * @Description: 自定义适配器Adapter, 作为listview的适配器
     */
    private class LeDeviceListAdapter extends BaseAdapter {

        private ArrayList<BluetoothDevice> mLeDevices;

        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            deviceRssiList = new ArrayList<Integer>();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device, int rssi) {
            if (!mLeDevices.contains(device)) {

                mLeDevices.add(device);
                deviceRssiList.add(rssi);

//                device.fetchUuidsWithSdp(); //异步的
//                Intent intent = new Intent();
//                Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
//                if (uuidExtra != null) {
//                    for (Parcelable parcelable : uuidExtra) {
//                        deuuid = parcelable.toString();
//                    }
//                }
//                System.out.println("deuuid" + deuuid);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
            deviceRssiList.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        /**
         * 重写getView
         */
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = view.findViewById(R.id.tv_deviceAddr);
                viewHolder.deviceName = view.findViewById(R.id.tv_deviceName);
                viewHolder.rssi = view.findViewById(R.id.tv_rssi);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            String deviceAddress = device.getAddress();
            String deviceName = device.getName();
            String rssi = deviceRssiList.get(i) + "";

            viewHolder.deviceAddress.setText(deviceAddress);
            viewHolder.deviceName.setText(deviceName);
            viewHolder.rssi.setText(rssi);

            return view;
        }
    }

    static class ViewHolder {

        public TextView deviceAddress;
        public TextView deviceName;
        public TextView rssi;
    }
}
