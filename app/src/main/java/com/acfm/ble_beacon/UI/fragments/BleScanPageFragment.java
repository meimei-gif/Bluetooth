package com.acfm.ble_beacon.UI.fragments;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import android.widget.AdapterView;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.acfm.ble_beacon.adapter.BleScanAdapter;
import com.acfm.ble_beacon.bluetooth.BleScanHelper;
import com.acfm.ble_beacon.bluetooth.BluetoothUtils;
import com.acfm.ble_beacon.entity.BleDevice;
import com.acfm.ble_transform.R;
import static com.acfm.ble_beacon.bluetooth.BleScanHelper.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.acfm.ble_beacon.bluetooth.BluetoothUtils.*;

public class BleScanPageFragment extends Fragment {

    public BleScanPageFragment(int curBeaconType) {
        this.curBeaconType = curBeaconType;
    }

    public static BleScanPageFragment newInstance(int beaconType){
        return new BleScanPageFragment(beaconType);
    }

    public BleScanPageFragment(){
        System.out.println("void Constructor is called");
    }

    private String[] permissionLists = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    private BleScanAdapter mAdapter;

    private BluetoothAdapter mBluetoothAdapter;

    private BleScanHelper mBleScanHelper;

    private int mScanTime = 5000;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerView;

    private int curBeaconType = -1;

    private List<String> list;
    private Set<String> textSet;
    private AutoCompleteTextView queryText;
    private static ArrayAdapter<String> hintAdapter;


    private List<String> spinnerlist = new ArrayList<String>();
    private Spinner spinnertext;
    private static ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //container : ViewPager
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.blescan_page_fragment, container, false
        );

        mSwipeRefreshLayout = view.findViewById(R.id.mSwipeRefreshLayout);
        mRecyclerView = view.findViewById(R.id.mRecyclerView);
//        final EditText queryText=view.findViewById(R.id.edit_queryText);
//        String[] countries = new String[]{"Afghanistan","Albania","Algeria","American Samoa","Andorra"};
        //提示词功能
        list = new ArrayList<String>();
        textSet = new HashSet<>();
        list.add("tag");
        list.add("DangerTag");
        textSet.add("tag");
        textSet.add("DangerTag");
        queryText=(AutoCompleteTextView) view.findViewById(R.id.edit_queryText);
        hintAdapter = new ArrayAdapter<>(getActivity(), R.layout.input_list_item, list);//配置Adaptor
//        hintAdapter = new ArrayAdapter<>(getActivity(), R.layout.input_list_item, list);//配置Adaptor
        queryText.setAdapter(hintAdapter);

        Button queryButtom=view.findViewById(R.id.queryByNameButton);

        //2022/4/10
        spinnerlist.add("根据设备名称进行过滤");
        spinnerlist.add("根据设备MAC地址进行过滤");
        String[] ctype = new String[]{"根据设备名称进行过滤","根据设备MAC地址进行过滤"};
        //第二步：为下拉列表定义一个适配器
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,ctype);
        //第三步：设置下拉列表下拉时的菜单样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //获取Spinner组件,
        final Spinner spinnertext = view.findViewById(R.id.spinner1);
        //第四步：将适配器添加到下拉列表上
        spinnertext.setAdapter(adapter);

        spinnertext.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private String positions;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                positions = adapter.getItem(position);
                if (positions.equals("根据设备名称进行过滤")){
                    queryText.setHint("请输入待搜索的设备名称");
                    scanType=0;
                }else if (positions.equals("根据设备MAC地址进行过滤")) {
                    queryText.setHint("请输入待搜索的MAC地址");
                    scanType=1;
                }
                parent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setVisibility(View.VISIBLE);
            }


        });

        // 过滤功能
        //按照名称搜索按钮
        queryButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=queryText.getText().toString();
                //提示词添加
                if(name != null && !textSet.contains(name)){
                    List<String> newList = new ArrayList<>();
                    newList.addAll(list);
                    newList.add(name);
                    list.clear();
                    list.addAll(newList);
                    textSet.add(name);
//                    list.add(name);
//                    hintAdapter.add(name);
//                    hintAdapter.notifyDataSetChanged();
                    hintAdapter = new ArrayAdapter<>(getActivity(), R.layout.input_list_item, list);//配置Adaptor
//        hintAdapter = new ArrayAdapter<>(getActivity(), R.layout.input_list_item, list);//配置Adaptor
                    queryText.setAdapter(hintAdapter);
                    Log.d("提示词list：",list.toString());
                }

                if(TextUtils.isEmpty(queryText.getText())){
                    mFilterWordList.set(curBeaconType, null);
                }else{
                    mFilterWordList.set(curBeaconType, name);
                }
                BleScanHelper.mBleDeviceLists.get(curBeaconType).clear(); //清空已搜索到的设备列表
                mAdapter.notifyDataSetChanged();
                startScan(); //重新搜索
            }
        });
        initView();
        initBluetooth();
        startScan();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(){
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary)); //下拉的颜色
        mSwipeRefreshLayout.setProgressViewOffset(true,100,200);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { //下拉时候的操作
            @Override
            public void onRefresh() {
                BleScanHelper.mBleDeviceLists.get(curBeaconType).clear(); //清空已搜索到的设备列表
                mAdapter.notifyDataSetChanged();
                startScan(); //重新搜索
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); //使用线性布局管理器
        mAdapter = new BleScanAdapter(getActivity(), curBeaconType);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initBluetooth(){
        BluetoothManager manager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();
        if(!mBluetoothAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothUtils.OpenBluetooth_Request_Code);
        }

        // 创建扫描辅助类
        //mBleScanHelper = new BleScanHelper(getActivity());
        mBleScanHelper = BleScanHelper.getInstance(getActivity());

        mBleScanHelper.setOnScanListener(new BleScanHelper.onScanListener() {
            @Override
            public void onNext(BleDevice device) {
                refreshBleDeviceList(device);
            }

            @Override
            public void onFinish() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        
    }

    /**
     * 依据扫描结果刷新列表
     */
    private void refreshBleDeviceList(BleDevice mBleDevice){
      //  Log.d("BleScanFragment","name = ${mBleDevice.device.name ?: null} ,address = ${mBleDevice.device.address ?: null}");
        List<BleDevice> mBleDeviceList = mBleDeviceLists.get(curBeaconType);
        int deviceBeaconType = BluetoothUtils.handleScanResult(mBleDevice.getScanRecordBytes()); //确定当前信标的类型
        if(deviceBeaconType==-1){ //非BEACON设备
            return;
        }

        String deviceName = mBleDevice.getDevice().getName();
        if((curBeaconType<6) && (deviceName==null || !deviceName.equals("DangerTag") || deviceBeaconType!=curBeaconType)){ //信标
            return;
        }else if(curBeaconType==6 && (deviceName==null || deviceName.length()<6 || !deviceName.startsWith("HELMET"))){
            return;
        }else if(curBeaconType==7 && (deviceName==null || deviceName.length()<6 || !deviceName.startsWith("LJHMAA"))){
            return;
        }else if(curBeaconType==8 && (deviceName==null || deviceName.length()<6 || !deviceName.startsWith("LJHMBA"))){
            return;
        }else if(curBeaconType==9){
            if(deviceName!=null) {
                if (deviceName.startsWith("HELMET") || deviceName.startsWith("LJHMAA") || deviceName.startsWith("LJHMBA"))
                    return;
                if (deviceBeaconType != 9) {
                    if (deviceName.equals("DangerTag")) return;
                }
            }
        }



       // System.out.println(deviceBeaconType);

        String filterWord = mFilterWordList.get(curBeaconType);
       // System.out.println("BeaconType : " + curBeaconType + " Name: " + mBleDevice.getDevice().getName() + ", filterName = " + filterWord);
        if(filterWord != null && !filterWord.equals("")){
//            String deviceName = mBleDevice.getDevice().getName();
            if(scanType==0) {
//                String str = filterWord.toUpperCase(Locale.ROOT);
                if (deviceName == null) {
                    return;
                } else if (!deviceName.toUpperCase(Locale.ROOT).contains(filterWord.toUpperCase(Locale.ROOT))) {
                    return;
                }
            }else if(scanType==1){
                String deviceMAC=mBleDevice.getDevice().getAddress();
                String str = filterWord.toUpperCase(Locale.ROOT);
                if(deviceMAC ==null){
                    return;
                }else if(!deviceMAC.contains(str)){
                    return;
                }
            }
        }
        else {
            mFilterWordList.set(curBeaconType,"");
        }

        for(int i=0;i<mBleDeviceList.size();i++){
            //替换 刷新
            BleDevice device = mBleDeviceList.get(i);
            if(device.getDevice().getAddress().equals(mBleDevice.getDevice().getAddress())){
                mBleDeviceList.set(i, device);
                mAdapter.notifyItemChanged(i);
                return;
            }
        }
        // 添加 刷新
        mBleDeviceList.add(mBleDevice);
        mAdapter.notifyItemChanged(mBleDeviceList.size()-1);
    }



    /**
     * 请求蓝牙扫描权限并开始扫描
     */
    private void startScan(){
        if(Build.VERSION.SDK_INT >= 23){
            if(hasPermissions(getActivity(), permissionLists)){
                mBleScanHelper.startScanBle(mScanTime);
            }else{
                requestPermissions(permissionLists, 17);
            }
        }else{
            mBleScanHelper.startScanBle(mScanTime);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            mBleScanHelper.startScanBle(mScanTime);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁扫描辅助类
        mBleScanHelper.onDestroy();
    }
}
