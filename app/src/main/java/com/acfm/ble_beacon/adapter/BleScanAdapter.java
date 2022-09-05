package com.acfm.ble_beacon.adapter;

import static com.acfm.ble_beacon.bluetooth.BleScanHelper.mBleDeviceLists;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acfm.ble_beacon.UI.activities.DeviceActivity;
import com.acfm.ble_beacon.bluetooth.BluetoothUtils;
import com.acfm.ble_beacon.entity.ADStructure;
import com.acfm.ble_beacon.entity.BleDevice;
import com.acfm.ble_transform.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫描RecyclerView的适配器
 */
public class BleScanAdapter extends RecyclerView.Adapter<BleScanAdapter.BleViewHolder> {
    Context context;

    private final int curBeaconType;

    public BleScanAdapter(Context context, int curBeaconType) {
        this.context = context;
        this.curBeaconType = curBeaconType;
    }

    @NonNull
    @Override
    public BleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.blescan_list_item, viewGroup, false
        );
        return new BleViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull BleViewHolder bleViewHolder, int i) {
        bleViewHolder.bindBleDate(mBleDeviceLists.get(curBeaconType).get(i));
    }

    @Override
    public int getItemCount() {
        return mBleDeviceLists.get(curBeaconType).size();
    }


    class BleViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;

        //MAC地址
        private TextView macAddress;
        private TextView name;
        private TextView rssi;
        private TextView bondState;
        private TextView rawBtn;
        private Button connecBtn;
        private LinearLayout otherLayout;
        private ImageView bleImg;
        private LinearLayout uuid16Layout;
        private TextView uuid16Text;
        private LinearLayout uuid32Layout;
        private TextView uuid32Text;
        private LinearLayout manufacturerDataLayout;
        private TextView manufacturerDataText;
        private LinearLayout serviceDataLayout;
        private TextView serviceDataText;
        private LinearLayout deviceNameLayout;
        private TextView deviceName;
        private LinearLayout deviceTypeLayout;
        private TextView deviceType;
        private TextView TXPower;
        private TextView RSSI_1M;
        private TextView Minor;
        private TextView Major;

        private ArrayList<ADStructure> mADStructureArray = new ArrayList<>();

        public BleViewHolder(@NonNull View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            macAddress = itemView.findViewById(R.id.macAddressText);
            name = itemView.findViewById(R.id.nameText);
            rssi = itemView.findViewById(R.id.rssiText);
            //bondState = itemView.findViewById(R.id.bondStateText);
            rawBtn = itemView.findViewById(R.id.rawDataBtn);
            connecBtn = itemView.findViewById(R.id.connecBtn);
            otherLayout = itemView.findViewById(R.id.otherLayout);
            bleImg = itemView.findViewById(R.id.bleImg);
            uuid16Layout = itemView.findViewById(R.id.uuid16Layout);
            uuid16Text = itemView.findViewById(R.id.uuid16Text);
            uuid32Layout = itemView.findViewById(R.id.uuid32Layout);
            uuid32Text = itemView.findViewById(R.id.uuid32Text);
            manufacturerDataLayout = itemView.findViewById(R.id.manufacturerDataLayout);
            manufacturerDataText = itemView.findViewById(R.id.manufacturerDataText);
            serviceDataLayout = itemView.findViewById(R.id.serviceDataLayout);
            serviceDataText = itemView.findViewById(R.id.serviceDataText);
            deviceNameLayout = itemView.findViewById(R.id.deviceNameLayout);
            deviceName = itemView.findViewById(R.id.deviceNameText);
            deviceTypeLayout = itemView.findViewById(R.id.deviceTypeLayout);
            deviceType = itemView.findViewById(R.id.deviceTypeText);
            //添加显示的，与macaddress相同显示
            Major = itemView.findViewById(R.id.MajorText);
            Minor = itemView.findViewById(R.id.MinorText);
            RSSI_1M = itemView.findViewById(R.id.RSSI_1MText);
            //TXPower = itemView.findViewById(R.id.TXPowerText);
        }

        public void bindBleDate(BleDevice bleDevice) {
            BluetoothDevice rawDevice = bleDevice.getDevice();
            //设置mac地址
            macAddress.setText(rawDevice.getAddress());
            //设置设备名称
            name.setText(rawDevice.getName() != null ? rawDevice.getName() : "N/A");
            deviceName.setText(rawDevice.getName() != null ? rawDevice.getName() : "N/A");
            //设置信号值
            rssi.setText(String.valueOf(bleDevice.getRssi()));
            //绑定状态
//            switch (rawDevice.getBondState()) {
//                case 10:
//                    bondState.setText("Not BOUNDED");
//                case 12:
//                    bondState.setText("BOUNDED");
//                default:
//                    bondState.setText("Not BOUNDED");
//            }

            //判断是否可以连接
            if (!bleDevice.getConnectable()) {
                connecBtn.setVisibility(View.INVISIBLE);
            } else {
                connecBtn.setVisibility(View.VISIBLE);
            }

            //判断厂商类型---只是判断显示什么样的图标
            if (bleDevice.getScanRecord() != null) {
                ScanRecord scanRecord = bleDevice.getScanRecord();
                //判断是否是苹果的厂商ID
                if (scanRecord.getManufacturerSpecificData(0x4C) != null) {
                    bleImg.setImageDrawable(mContext.getDrawable(R.drawable.apple));
                } else if (scanRecord.getManufacturerSpecificData(0x06) != null) {
                    bleImg.setImageDrawable(mContext.getDrawable(R.drawable.windows));
                } else {
                    bleImg.setImageDrawable(mContext.getDrawable(R.drawable.bluetoothon));
                }
            } else {
                bleImg.setImageDrawable(mContext.getDrawable(R.drawable.bluetoothon));
            }

            //解析蓝牙广播数据报文
            String rawData = parseBleADData(bleDevice.getScanRecordBytes());
            Log.d("mei", rawData);
            //初始化隐藏布局
            initOtherLayout(bleDevice);
            //设置监听
            initListener(rawDevice.getAddress(), rawData, rawDevice.getName() == null ? "N/A" : rawDevice.getName(), rawDevice);
        }

        /**
         * 初始化隐藏布局
         */
        private void initOtherLayout(BleDevice bleDevice) {
            //先隐藏，并移除之前的布局
            otherLayout.setVisibility(View.GONE);
            ScanRecord scanRecord = bleDevice.getScanRecord();
            //隐藏
            uuid16Layout.setVisibility(View.GONE);
            uuid32Layout.setVisibility(View.GONE);
            //UUID
            List<ParcelUuid> it = scanRecord.getServiceUuids();
            //添加UUID信息
            for (ADStructure adStructure : mADStructureArray) {
                String dataStr;
                switch (adStructure.getType()) {
                    //完整的16BIT UUID列表
                    case "0x03":
                        uuid16Text.setText("");
                        //除去之前添加的0x
                        dataStr = adStructure.getData().substring(2, adStructure.getData().length());
                        for (int i = 0; i < dataStr.length() / 4; i++) {
                            String uuid = "0x" + dataStr.substring(2 + i * 4, 4 + i * 4) +
                                    dataStr.substring(0 + i * 4, 2 + i * 4);
                            if (uuid16Text.getText().equals("")) {
                                uuid16Text.setText(uuid);
                            } else {
                                uuid16Text.setText(uuid16Text.getText().toString() + "," + uuid);
                            }
                        }
                        uuid16Layout.setVisibility(View.VISIBLE);
                        break;
                    //完整的32BIT UUID列表
                    case "0x05":
                        uuid32Text.setText("");
                        //除去之前添加的0x
                        dataStr = adStructure.getData().substring(2, adStructure.getData().length());
                        for (int i = 0; i < dataStr.length() / 8; i++) {
                            String uuid = "0x" + dataStr.substring(6 + i * 8, 8 + i * 8) +
                                    dataStr.substring(4 + i * 8, 6 + i * 8) +
                                    dataStr.substring(2 + i * 8, 4 + i * 8) +
                                    dataStr.substring(0 + i * 8, 2 + i * 8);
                            if (uuid16Text.getText().equals("")) {
                                uuid16Text.setText(uuid);
                            } else {
                                uuid16Text.setText(uuid16Text.getText().toString() + "," + uuid);
                            }
                        }
                        uuid32Layout.setVisibility(View.VISIBLE);
                }
            }

            //隐藏
            manufacturerDataLayout.setVisibility(View.GONE);
            //厂商数据
            if (scanRecord.getManufacturerSpecificData() != null) {
                //添加厂商数据信息
                for (ADStructure adStructure : mADStructureArray) {
                    if (adStructure.getType().equals("0xFF")) {
                        //除去之前添加的0x
                        String data = adStructure.getData().substring(2, adStructure.getData().length());

                        //获取厂商ID
                        String manufacturerId = data.substring(2, 4) + data.substring(0, 2);
                        //获取真正的厂商数据
                        String manufacturerData = data.substring(4, data.length());
//                        manufacturerDataText.setText("厂商ID: 0x" + manufacturerId + '\n' + "数据：0x" + manufacturerData);
                        String devType = "Device type: LE only\n" + "Advertising type: Legacy\n" + "Flags: GeneralDiscoverable,\nBrEdrNotSupported";
                        deviceType.setText(devType);
                        deviceTypeLayout.setVisibility(View.VISIBLE);
                        manufacturerDataText.setText(
                                parseData(manufacturerId, manufacturerData)
                        );

                        manufacturerDataLayout.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
            //隐藏
            serviceDataLayout.setVisibility(View.GONE);
            //服务数据
            if (scanRecord.getServiceData() != null) {
                serviceDataText.setText("");
                //添加厂商数据信息
                for (ADStructure adStructure : mADStructureArray) {
                    switch (adStructure.getType()) {
                        //16BIT服务数据
                        case "0x16":
                            //除去之前添加的0X
                            String data = adStructure.getData().substring(2, adStructure.getData().length());
                            //获取16BIT的UUID
                            String uuid = "0x" + data.substring(2, 4) + data.substring(0, 2);
                            //获取对应的数据
                            String serviceData = data.substring(4, data.length());
                            serviceDataText.setText("16-bit UUID: " + uuid + '\n' +
                                    "数据：0x" + serviceData
                            );
                            serviceDataLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

        }

        /**
         * 解析广播数据，将数据添加描述
         *
         * @param licensing
         * @param data
         * @return 数据的描述
         */
        private String parseData(String licensing, String data) {
            System.out.println(data);
            Log.d("mei", "data的信息是： "+data);
            String text = "";
            if(data.length()<46){
                text += "Company:Unknown" + '\n';
                text += "Licensing : Unknown" + '\n';
                text += "Type:Beacon : Unknown"  + ">" + '\n';
                text += "Length of data: Unknown"   + '\n';
                text += "UUID: Unknown" + '\n';
                text += "Major: Unknown" + '\n';
                text += "Minor: Unknown" + '\n';
                text += "RSSI at 1m: Unknown\n" ;
                text += "data:0x" +licensing +data;
            }
            else {
                text += "Company:xxxxxxx" + '\n';
                text += "Licensing <0x" + licensing + ">" + '\n';
                text += "Type:Beacon <0x" + data.substring(0, 2) + ">" + '\n';
                text += "Length of data:" + String.valueOf(Integer.parseInt(data.substring(2, 4), 16)) + " bytes" + '\n';
                text += "UUID:" + data.substring(4, 12) + '-' + data.substring(12, 16) + '-' + data.substring(16, 20) + '-'
                        + data.substring(20, 24) + '-' + data.substring(24, 36) + '\n';
//                text += "Major:" + String.valueOf(Integer.parseInt(data.substring(36, 40), 16)) + '\n';
                text += "Major:" + data.substring(36, 40) + '\n';
//                text += "Minor:" + String.valueOf(Integer.parseInt(data.substring(40, 44), 16)) + '\n';
                text += "Minor:" + data.substring(40, 44) + '\n';
                text += "RSSI at 1m: " + (Integer.parseInt(data.substring(44, 46), 16) | 0xFFFFFF00)  + " dBm";
//                text += "data:0x" + data;
                Major.setText("Major:"+data.substring(36, 40));
                Minor.setText("Minor:"+data.substring(40, 44));
                RSSI_1M.setText("RSSI:"+(Integer.parseInt(data.substring(44, 46), 16) | 0xFFFFFF00)+"dBm");

            }
            return text;
        }

        /**
         * 设置点击监听
         */
        private void initListener(final String macAddress, final String rawDataStr, final String name, final BluetoothDevice device) {
            //连接按钮点击监听
            connecBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mContext.startActivity(BleClientPageActivity.newIntent(mContext, macAddress, name));
                    Intent intent = new Intent(mContext, DeviceActivity.class);
                    intent.putExtra("device", device);
                    mContext.startActivity(intent);

                }
            });

            //原始数据按钮点击监听
            rawBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(rawDataStr);
                }
            });

            //列表ITEM点击监听
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (otherLayout.getVisibility() == View.GONE) {
                        otherLayout.setVisibility(View.VISIBLE);
                    } else {
                        otherLayout.setVisibility(View.GONE);
                    }
                }
            });
        }

        /**
         * 统计数据单元，展示dialog
         */
        private void showDialog(String rawDataStr) {
            View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.blescan_dialog, null);
            TableLayout mTableLayout = view.findViewById(R.id.mTableLayout);
            final TextView rawData = view.findViewById(R.id.rawDataText);
            TextView positiveBtn = view.findViewById(R.id.positiveBtn);
            //显示原始数据
            rawData.setText("0x" + rawDataStr.toUpperCase());
            //设置数据单元
            for (ADStructure adStructure : mADStructureArray) {
                setBleADStructureTable(adStructure.getLength(), adStructure.getType(), adStructure.getData(), mTableLayout);
            }
            //展示dialog
            final AlertDialog dialog = new AlertDialog.Builder(mContext)
                    .setView(view)
                    .show();

            //设置点击事件
            positiveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            //原始数据点击事件
            rawData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("BEACON", rawData.getText());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(mContext, "复制成功", Toast.LENGTH_SHORT).show();
                }
            });
        }

        /**
         * 解析蓝牙广播报文， 获取数据单元
         */
        private String parseBleADData(byte[] byteArray) {
            //将字节数组转为16进制字符串
            String rawDataStr = BluetoothUtils.bytesToHexString(byteArray);
            //清空之前解析的数据单元
            mADStructureArray.clear();
            //存储实际数据段
            String dataStr = "";
            while (true) {
                //取长度
                String lengthStr = rawDataStr.substring(0, 2);
                //如果长度为0，则退出
                if (lengthStr.equals("00")) {
                    break;
                }
                //将长度转10进制
                int length = Integer.parseInt(lengthStr, 16);
                //length表示后面多少字节也属于该数据单元，所以整个数据单元的长度 = length+1;
                String data = rawDataStr.substring(0, (length + 1) * 2);
                //存储每个数据单元的值
                dataStr += data;
                //裁剪原始数据，方便后面裁剪数据单元
                rawDataStr = rawDataStr.substring((length + 1) * 2, rawDataStr.length());
                //创建广播数据单元bean， 并存储到数据中
                //第一个字节是长度，第二个字节是类型，再后面才是数据（一个字节用2个HEX表示）
                mADStructureArray.add(new ADStructure
                        (length,
                                "0x" + data.substring(2, 4).toUpperCase(),
                                "0x" + data.substring(4, data.length()).toUpperCase()
                        )
                );
            }
            //返回蓝牙广播数据报文
            return dataStr;
        }

        /**
         * 设置广播数据单元
         */
        private void setBleADStructureTable(Integer length, String type, String data, ViewGroup parent) {
            //创建表格
            TableRow tableRow = new TableRow(mContext);
            // 创建LENGTH视图
            TextView lengthView = new TextView(mContext);
            lengthView.setLayoutParams(new TableRow.LayoutParams(1, ViewGroup.LayoutParams.WRAP_CONTENT));
            int size = (int) dp2px(4f);
            lengthView.setPadding(size, size, size, size);
            lengthView.setText(length.toString());
            lengthView.setGravity(Gravity.CENTER);
            tableRow.addView(lengthView);

            //创建Type视图
            TextView typeView = new TextView(mContext);
            typeView.setLayoutParams(new TableRow.LayoutParams(1, ViewGroup.LayoutParams.WRAP_CONTENT));

            typeView.setPadding(size, size, size, size);
            typeView.setText(type);
            typeView.setGravity(Gravity.CENTER);
            tableRow.addView(typeView);

            //创建Value视图
            TextView valueView = new TextView(mContext);
            TableRow.LayoutParams valueLayoutParams = new TableRow.LayoutParams(1, ViewGroup.LayoutParams.WRAP_CONTENT);
            valueLayoutParams.span = 3;
            valueView.setLayoutParams(valueLayoutParams);
            valueView.setPadding(size, size, size, size);
            valueView.setText(data);
            valueView.setGravity(Gravity.CENTER);
            tableRow.addView(valueView);
            parent.addView(tableRow);
        }

        private float dp2px(float num) {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, num, itemView.getContext().getResources().getDisplayMetrics());
        }
    }
}
