package com.acfm.ble_beacon.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;

import com.acfm.ble_beacon.entity.BleDevice;
import com.acfm.ble_transform.R;

import java.util.ArrayList;
import java.util.List;

public class BleScanHelper {
    public static synchronized BleScanHelper getInstance(Context context){
        if(bleScanHelper!=null){
            return bleScanHelper;
        }
        bleScanHelper = new BleScanHelper(context);
        return bleScanHelper;
    }
    private static final int CAPACITY = 10;

    private static BleScanHelper bleScanHelper;

    private Runnable stopScanTask = new Runnable() {
        @Override
        public void run() {
            for(int i=0;i<mListeners.size();i++){
                mListeners.get(i).onFinish();
            }
//                    mListener.onFinish();
        }
    };

    Context context;
    //"LJ-SS-EEA-01", "LJ-SS-EEA-02", "LJ-SS-EEA-03", "LJ-SS-EEA-04", "LJ-SS-EEA-05", "疑似废弃信标"
    // public static List<BleDevice> mBleDeviceList;

    public static List<List<BleDevice>> mBleDeviceLists = new ArrayList<>(CAPACITY);

    public static int scanType=0; //0表示按照名称搜索，1表示按照mac地址搜索 2022/4/10

    public static List<String> mFilterWordList = new ArrayList<>(CAPACITY); //过滤词

    // 标记当前是否在扫描
    private boolean mScanning = false;
    //工作子线程
    private Handler mHandler;
    //主线程 HANDLER
    private Handler mMainHandler;
    //android5.0 扫描对象
    private BluetoothLeScanner mBleScanner;
    //5.0 以下扫描回调对象
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    //5.0及其以上扫描回调对象
    private ScanCallback mScanCallback;
    //5.0扫描配置对象
    private ScanSettings mScanSettings;
    //扫描过滤器列表
    private ArrayList<ScanFilter> mScanFilterList;
    //蓝牙设配器
    private BluetoothAdapter mBluetoothAdapter;
    //统一地扫描回调对象
    private List<onScanListener> mListeners;

    private BleScanHelper(Context context) {
        this.context = context;
        //init
        BluetoothManager manager = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();

        for(int i=0;i<CAPACITY;i++){
            mBleDeviceLists.add(new ArrayList<BleDevice>());
        }
        for(int i=0;i<CAPACITY;i++){
            mFilterWordList.add(null);
        }
        mListeners = new ArrayList<>();
        //初始化Handler
        initHandler(context);
        //初始化蓝牙回调
        initBluetoothCallBack();
        //初始化蓝牙扫描配置
        initmScanSettings();
        //初始化蓝牙过滤器
        initScanFilter();
    }

    public interface onScanListener {
        void onNext(BleDevice device);

        void onFinish();
    }

    public void setOnScanListener(onScanListener listener) {
        mListeners.add(listener);
    }



    /**
     * 初始化Handler
     */
    private void initHandler(Context context){
        //初始化工作线程handler
        HandlerThread mHandlerThread = new HandlerThread("ScanThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        //初始化主线程Handler
        mMainHandler = new Handler(context.getMainLooper());

    }

    /**
     * 初始化蓝牙回调
     */
    private void initBluetoothCallBack(){
        //5.0及其以上扫描回调
        
        mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, final ScanResult result) {
                super.onScanResult(callbackType, result);
                //post出去 尽快结束回调
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        BleDevice mBleDevice;
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                            mBleDevice = new BleDevice(result.getDevice(), result.getRssi(),
                                    result.getScanRecord().getBytes(),
                                    result.isConnectable(),
                                    result.getScanRecord()
                                    );
                        }else{
                            mBleDevice = new BleDevice(result.getDevice(), result.getRssi(),
                                    result.getScanRecord().getBytes(),
                                    true,
                                    result.getScanRecord()
                                    );
                        }
                        for(int i=0;i<mListeners.size();i++){
                            mListeners.get(i).onNext(mBleDevice);
                        }

                    }
                });
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                switch (errorCode){
                    case ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                        Log.e("BleScanHelper", "扫描太频繁");
                }
            }
        };
        //5.0以下扫描回调
        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(device!=null && scanRecord!=null){
                            //扫描回调
                            BleDevice mBleDevice = new BleDevice(device, rssi, scanRecord);
                            for(int i=0;i<mListeners.size();i++){
                                mListeners.get(i).onNext(mBleDevice);
                            }
                            //mListener.onNext(mBleDevice);
                        }
                    }
                });
            }
        };
    }

//    //设置名称搜索过滤器
//    public void setQueryByNameFilter(String name) {
//        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//        mScanFilterList.clear();
//        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//        if (name != "" && !name.isEmpty()) {
//            ScanFilter.Builder builder = new ScanFilter.Builder();
//            builder.setDeviceName(name);
//            mScanFilterList.add(builder.build());
//        }
//
//    }

    /**
     * 初始化拦截器实现
     * 扫描回调只会返回符合该拦截器UUID的蓝牙设备
     */
    private void initScanFilter(){
        mScanFilterList = new ArrayList<>();
        ScanFilter.Builder builder = new ScanFilter.Builder();
        builder.setServiceUuid(ParcelUuid.fromString("0000fff1-0000-1000-8000-00805f9b34fb"));
        mScanFilterList.add(builder.build());
    }

    /**
     * 初始化蓝牙扫描配置
     */
    private void initmScanSettings(){
        // 功耗平衡模式
        ScanSettings.Builder builder = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED);

        //android 6.0添加设置回调类型、匹配模式等
        if(android.os.Build.VERSION.SDK_INT >= 23) {
            //定义回调类型
            builder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
            //设置蓝牙LE扫描滤波器硬件匹配的匹配模式
            builder.setMatchMode(ScanSettings.MATCH_MODE_STICKY);
        }
        //芯片组支持批处理芯片上的扫描
        if (mBluetoothAdapter.isOffloadedScanBatchingSupported()) {
            //设置蓝牙LE扫描的报告延迟的时间（以毫秒为单位）
            //设置为0以立即通知结果
            builder.setReportDelay(0L);
        }
        mScanSettings = builder.build();
    }

    /**
     * 开始扫描蓝牙ble
     */
    public void startScanBle(int time){
        if(!mBluetoothAdapter.isEnabled()){
            return;
        }
        if(!mScanning){
            //android 5.0后
            if(android.os.Build.VERSION.SDK_INT >= 21) {
                //标记当前的为扫描状态
                mScanning = true;
                //获取5.0新添的扫描类
                if (mBleScanner == null){
                    //mBLEScanner是5.0新添加的扫描类，通过BluetoothAdapter实例获取。
                    mBleScanner = mBluetoothAdapter.getBluetoothLeScanner();
                }
                //在子线程中扫描
                mHandler.post(
                    new Runnable(){
                        @Override
                        public void run() {
                            //mScanSettings是ScanSettings实例，mScanCallback是ScanCallback实例，后面进行讲解。
                            //过滤器列表传空，则可以扫描周围全部蓝牙设备
                            mBleScanner.startScan(null,mScanSettings,mScanCallback);
                            //使用拦截器

                        }
                    }
                );
            } else {
                //标记当前的为扫描状态
                mScanning = true;
                //5.0以下  开始扫描
                //在子线程中扫描
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //mLeScanCallback是BluetoothAdapter.LeScanCallback实例
                        mBluetoothAdapter.startLeScan(mLeScanCallback);
                    }
                });
            }

            //设置结束扫描
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //关闭ble扫描
                    stopScanBle();
                }
            }, time);
        }

    }

    public void stopScanBle(){
        if(mScanning){
            //移除之前的停止扫描post
            mHandler.removeCallbacks(null);
            //停止扫描设备
            if(android.os.Build.VERSION.SDK_INT >= 21) {
                //标记当前的为未扫描状态
                mScanning = false;
                mBleScanner.stopScan(mScanCallback);
            } else {
                //标记当前的为未扫描状态
                mScanning = false;
                //5.0以下  停止扫描
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
            //主线程回调
            mMainHandler.post(stopScanTask);

        }
    }

    public void onDestroy(){
        stopScanBle();
        mMainHandler.removeCallbacksAndMessages(stopScanTask);
        mHandler.removeCallbacksAndMessages(null);
        //mHandler.getLooper().quit();

    }

}
