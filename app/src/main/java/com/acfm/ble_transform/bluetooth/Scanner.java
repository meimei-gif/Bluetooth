package com.acfm.ble_transform.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.acfm.ble_transform.Constants;
import com.acfm.ble_transform.DAO.IBeaconRecord;

import com.acfm.ble_transform.R;
import com.acfm.ble_transform.Service.ScanResultsConsumer;

import com.acfm.ble_transform.SQLiteUtil.SqliteDao;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Scanner {
    private BluetoothLeScanner scanner;
    private BluetoothAdapter bluetoothAdapter;
    private final Handler handler = new Handler();
    private Context context;
    private boolean scanning = false;
    private ScanResultsConsumer scanResultsConsumer;
    private JSONObject helmetMap;
    private SqliteDao sqliteDao ;


    private Runnable threadStopScanning = new Runnable() {
        @Override
        public void run() {
            if (scanning) {
                Log.d(Constants.TAG, "Stopping scanning");
                scanner.stopScan(scanCallback);
                setScanning(false);
            }
        }
    };



    public void setScanning(boolean scanning) {
        this.scanning = scanning;
        if (!scanning) {
            scanResultsConsumer.scanningStopped();
        } else {
            scanResultsConsumer.scanningStarted();
        }
    }

    public Scanner(Context context) {
        this.context = context;
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // 没开蓝牙的话，申请开启蓝牙
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.d(Constants.TAG, "Bluetooth is NOT switched on");
            Intent enable_BT_intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enable_BT_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(enable_BT_intent);
        }
        Log.d(Constants.TAG, "蓝牙开启");

        helmetMap = new JSONObject();


    }

    public void startTimedScanning(final ScanResultsConsumer scanResultsConsumer, final long stopAfterMs){
        stopTimedScanning();
        startScanning(scanResultsConsumer, stopAfterMs);
    }

    public void stopTimedScanning(){
        if(isScanning()){
            stopScanning();
        }
    }


    public void startScanning(final ScanResultsConsumer scanResultsConsumer, long stopAfterMs) {
        if (scanning) {
            Log.d(Constants.TAG, "Already scanning so ignoring startScanning request");
            return;
        }

        if (scanner == null) {
            scanner = bluetoothAdapter.getBluetoothLeScanner();
        }
//        if (handler.hasCallbacks(threadStopScanning)) { //Requires API 29
        handler.removeCallbacks(threadStopScanning); //事实上直接remove也可以，因为参数为空的话直接返回
//        }


        //stopAfterMs后停止查找
        handler.postDelayed(threadStopScanning, stopAfterMs);

        this.scanResultsConsumer = scanResultsConsumer;
        Log.d(Constants.TAG, "Scanning");
        List<ScanFilter> filters = new ArrayList<>();
//        ScanFilter filter = new ScanFilter.Builder().setDeviceAddress("C9:0D:BE:D4:C2:A7").build();
//        filters.add(filter);

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        setScanning(true);
        Log.d(Constants.TAG, "Start Scanning");
        scanner.startScan(filters, settings, scanCallback);
    }

    public void stopScanning() {
        setScanning(false);
        Log.d(Constants.TAG, "Stopping scanning");
        scanner.stopScan(scanCallback);
    }


    private final ScanCallback scanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (!scanning) {
                return;
            }

            // ScanRecord
            BluetoothDevice device = result.getDevice();

            // 满足HELMET|MONITOR条件，让MainActivity更新
            if (isHelmet(device.getName()) || isMonitor(device.getName())) {
                byte[] scanRecordBytes = result.getScanRecord().getBytes();
                int rssi = result.getRssi();

                scanResultsConsumer.candidateDevice(
                        device,
                        scanRecordBytes,
                        rssi
                );

                //保留头盔数据，供MainActivity跳转使用
                if (isHelmet(device.getName())) {
                    sqliteDao = new SqliteDao(context);
                    IBeaconRecord record = new IBeaconRecord();
                    if (fromScanData(scanRecordBytes, record)) {
                        // address rssi name id workenv 佩戴情况 当前模式 EVA 呼救/运动状态 温度 电量 高度
                        String address = device.getAddress();   // 获取Mac地址
                        String name = device.getName();         // 获取设备名称
                        record.address = address;   // Mac地址
                        record.rssi = rssi;    // 场强
                        String uuid = record.uuid;
                        StringBuilder state = new StringBuilder();
                        state.append(address);  // 0. 物理地址
                        state.append('!');
                        state.append(rssi);    // 1. 信号强度
                        state.append('!');
                        state.append(name);    // 2. 帽子名称
                        state.append('!');
                        String id = uuid.substring(12, 14); // 3. id
                        state.append(Integer.valueOf(id, 16)); // 3. id
                        state.append('!');
                        String workenv = uuid.substring(14, 16); // 4. 工作环境
                        state.append(workenv);
                        state.append('!');
                        char peidai = uuid.charAt(16);//佩戴
                        char shebeis = uuid.charAt(17);//设备状态
                        String EVA = uuid.substring(18, 20);
                        String tem = uuid.substring(20, 22);//温度
                        String hujiu = uuid.substring(22, 24);//呼救//运动状态
                        String high = uuid.substring(24, 28);//高度
                        String energy = uuid.substring(28, 30);//电量
                        String status = "";
                        if (peidai == '1') {   // 工作环境
                            state.append("已佩戴");
                            status += "已佩戴 ";
                        } else {
                            state.append("未佩戴");
                            status += "未佩戴 ";
                        }
                        if (shebeis == '0') {  // 工作状态
                            state.append("关机模式");
                            status += "关机模式";
                            state.append('!');
                        } else if (shebeis == '1') {
                            state.append("开机模式");
                            status += "开机模式";
                            state.append('!');
                        } else if (shebeis == '2') {
                            state.append("非工作模式");
                            status += "非工作模式";
                            state.append('!');
                        } else {
                            state.append("工作模式");
                            status += "工作模式";
                            state.append('!');
                        }
                        state.append(EVA);  //  6. 湿度
                        state.append('!');
                        state.append(hujiu);  // 7. 呼救类型
                        state.append('!');
                        state.append(Integer.valueOf(tem, 16));  // 8. 温度
                        state.append('!');
                        state.append(Integer.valueOf(energy, 16));  // 9. 点量
                        state.append('!');
                        state.append(Integer.valueOf(high, 16));    // 10. 高度
                        try {
                            helmetMap.put(address, state.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }


        }
    };

    /**
     * 解析蓝牙信息数据流
     *
     * @param
     * @param record 用来保存解析出来的蓝牙数据
     * @return Boolean
     */
    private boolean fromScanData(byte[] scanData, IBeaconRecord record) {

        int startByte = 2;
        boolean patternFound = false;
        while (startByte <= 5) {
            if (((int) scanData[startByte + 5] & 0xff) == 0x02 && ((int) scanData[startByte + 6] & 0xff) == 0x15) {
                // yes! This is an iBeacon
                patternFound = true;
//                System.out.print("youbeacon");
                break;
            }
            else if (((int) scanData[startByte] & 0xff) == 0x2d
                    && ((int) scanData[startByte + 1] & 0xff) == 0x24
                    && ((int) scanData[startByte + 2] & 0xff) == 0xbf
                    && ((int) scanData[startByte + 3] & 0xff) == 0x16) {

                return false;
            }
            else if (((int) scanData[startByte] & 0xff) == 0xad
                    && ((int) scanData[startByte + 1] & 0xff) == 0x77
                    && ((int) scanData[startByte + 2] & 0xff) == 0x00
                    && ((int) scanData[startByte + 3] & 0xff) == 0xc6) {

                return false;
            }
            startByte++;
        }

        if (patternFound == false) {
            // This is not an iBeacon
            return false;
        }

//        // 获得Major属性
//        record.major = (scanData[startByte + 20] & 0xff) * 0x100
//                + (scanData[startByte + 21] & 0xff);

        record.txPower = (int) scanData[startByte + 24];

//        // 获得Minor属性
//        record.minor = (scanData[startByte + 22] & 0xff) * 0x100
//                + (scanData[startByte + 23] & 0xff);

        // record.tx_power = (int) scanData[startByte + 24]; // this one is


        byte[] proximityUuidBytes = new byte[16];
        System.arraycopy(scanData, startByte + 7, proximityUuidBytes, 0, 16);
        String hexString = bytesToHex(proximityUuidBytes);
        StringBuilder sb = new StringBuilder();
        sb.append(hexString.substring(0, 8));
        sb.append("-");
        sb.append(hexString.substring(8, 12));
        sb.append("-");
        sb.append(hexString.substring(12, 16));
        sb.append("-");
        sb.append(hexString.substring(16, 20));
        sb.append("-");
        sb.append(hexString.substring(20, 32));
//        System.out.print("duuid" + hexString);
        // beacon.put("proximity_uuid", sb.toString());
        // 获得UUID属性
        record.uuid = hexString;
        Log.d(Constants.TAG,"Scanner -> fromScanData -> hexString: "+hexString);

        return true;
    }

    private char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    public boolean isScanning() {
        return scanning;
    }

    public boolean isHelmet(String deviceName) {
        return deviceName != null && deviceName.length() >= 8 && ("HELMET".equals(deviceName.substring(0, 6)));
    }

    public boolean isMonitor(String deviceName) {
        return deviceName != null && deviceName.length() >= 7 && "Monitor".equals(deviceName.substring(0, 7));
    }

    public JSONObject getHelmetMap() {
        return helmetMap;
    }
}
