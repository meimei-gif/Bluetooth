package com.acfm.ble_beacon.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.acfm.ble_beacon.config.ConfigElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BluetoothUtils {

    public static final int OpenBluetooth_Request_Code = 10086;


    public static void sortCollections(ArrayList<ConfigElement> list) {
        // 0802 0809 0806 0807 0100 0101 0404 0805 0406 080A 0300 0301 0302 0303 0201 0400 0401
        // 0402 0403 0405 0800 0801 0803 0804 0808 080B
        List<ConfigElement> tmp = new ArrayList<>();
        tmp.addAll(list);
        if(list.size()!=27){
            Log.e("Error when sorting :", "deficiency of ID, required 27");
        }

        for (ConfigElement configElement : tmp) {
            switch (configElement.getId()) {
                case 0x0100:
                    list.set(4, configElement);
                    break;
                case 0x0101:
                    list.set(5, configElement);
                    break;
                case 0x0102:
                    list.set(12, configElement);
                    break;
                case 0x0201:
                    list.set(15, configElement);
                    break;
                case 0x0300:
                    list.set(10, configElement);
                    break;
                case 0x0301:
                    list.set(11, configElement);
                    break;
                case 0x0302:
                    list.set(13, configElement);
                    break;
                case 0x0303:
                    list.set(14, configElement);
                    break;
                case 0x0400:
                    list.set(16, configElement);
                    break;
                case 0x0401:
                    list.set(17, configElement);
                    break;
                case 0x0402:
                    list.set(18, configElement);
                    break;
                case 0x0403:
                    list.set(19, configElement);
                    break;
                case 0x0404:
                    list.set(6, configElement);
                    break;
                case 0x0405:
                    list.set(20, configElement);
                    break;
                case 0x0406:
                    list.set(8, configElement);
                    break;
                case 0x0800:
                    list.set(21, configElement);
                    break;
                case 0x0801:
                    list.set(22, configElement);
                    break;
                case 0x0802:
                    list.set(0, configElement);
                    break;
                case 0x0803:
                    list.set(23, configElement);
                    break;
                case 0x0804:
                    list.set(24, configElement);
                    break;
                case 0x0805:
                    list.set(7, configElement);
                    break;
                case 0x0806:
                    list.set(2, configElement);
                    break;
                case 0x0807:
                    list.set(3, configElement);
                    break;
                case 0x0808:
                    list.set(25, configElement);
                    break;
                case 0x0809:
                    list.set(1, configElement);
                    break;
                case 0x080A:
                    list.set(9, configElement);
                    break;
                case 0x080B:
                    list.set(26, configElement);
                    break;
                default:
                    Log.e("Error When Sorting:", "no match ID");
            }
        }

    }

    /**
     * 字节数组转十六进制字符串
     */
    public static String bytesToHexString(byte[] src) {
        if (src == null || src.length == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }

        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hex = Integer.toHexString(v);
            if (hex.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hex);
        }
        return stringBuilder.toString();
    }

    /**
     * 将字符串转成字节数组
     */
    public static byte[] hexStringToBytes(String str) {
        byte abyte0[] = new byte[str.length() / 2];
        byte[] s11 = str.getBytes();
        for (int i1 = 0; i1 < s11.length / 2; i1++) {
            byte byte1 = s11[i1 * 2 + 1];
            byte byte0 = s11[i1 * 2];
            String s2;
            abyte0[i1] = (byte) (
                    (byte0 = (byte) (Byte.decode((new StringBuilder(String.valueOf(s2 = "0x")))
                            .append(new String(new byte[]{byte0})).toString())
                            .byteValue() << 4)) ^
                            (byte1 = Byte.decode((new StringBuilder(String.valueOf(s2)))
                                    .append(new String(new byte[]{byte1})).toString()).byteValue()));
        }
        return abyte0;
    }

    /**
     * 权限列表判断
     */
    public static boolean hasPermissions(Context context, String[] permissionList) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permissionStr : permissionList) {
            if (ContextCompat.checkSelfPermission(context, permissionStr) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 刷新缓存
     */
    public boolean refreshDeviceCache(BluetoothGatt bluetoothGatt) {
        if (bluetoothGatt != null) {
            try {
                boolean paramBoolean = ((Boolean) bluetoothGatt.getClass()
                        .getMethod("refresh", new Class[0])
                        .invoke(bluetoothGatt, new Object[0]))
                        .booleanValue();
                return paramBoolean;
            } catch (Exception localException) {
            }
        }
        return false;
    }

    /**
     * 处理广播数据, 确定信标类型
     * 2022.3.18
     */
    public static int handleScanResult(byte[] scanRecord) {
        /*
        --------------------------------------------------------------------------------
        | 0     1     2     3     4     5     6    7     8     9-24  25-26  27-28 29   |
        |                                                                              |
        |0x02  0x01  0x06  0x1A  0xFF  0x00  0x00 0x02  0x15   UUID  Major  Minor RSSI |
        --------------------------------------------------------------------------------
         */
        int startIndex = 0;
        boolean patternFound = false;
        //System.out.println(bytesToHexString(scanRecord));
        //0201061aff00000215fda50693a4e24fb1afcfc6eb0764000072000003ba0a0944616e6765725461671116031880eccace20527200000302019064000000
        if(scanRecord.length <= 30){
            return -1;
        }

        while (startIndex <= 7) {
            //System.out.println(scanRecord[startIndex] & 0xff);
            if (((scanRecord[startIndex] & 0xff) == 0x02)
                    && ((scanRecord[startIndex+1] & 0xff) == 0x15)
            ) {
                patternFound = true;
                startIndex += 2;
                break;
            }
            startIndex++;
        }
        String[] types_of_beacons = new String[]{"LJ-SS-EEA-01 工作面大信标", "LJ-SS-EEA-02 语音信标", "LJ-SS-EEA-03 雷达信标", "LJ-SS-EEA-04 巡检信标", "LJ-SS-EEA-05 辅助定位信标", "废弃信标"};


        if (patternFound) {
            String UUID = bytesToHexString(Arrays.copyOfRange(scanRecord, startIndex, startIndex + 16));
            //System.out.println("UUID:" + UUID);
            /*
            LJ-SS-EEA-01   工作面大信标  Major/Minor  72:00:00:00-72:00:00:FF
            LJ-SS-EEA-02   语音信标      Major/Minor  10:00:00:00-6F:FF:FF:FF
            LJ-SS-EEA-03   雷达信标      Major/Minor  8F:FF:00:00-8F:FF:FF:FF
            LJ-SS-EEA-04   巡检信标      Major/Minor  90:00:00:00-9F:FF:FF:FF
            LJ-SS-EEA-05   辅助定位信标  Major/Minor  75:00:00:00-8F:FE:FF:FF

            疑似废弃信标   UUID         FDA50693A4E24FB1AFCFC6EB076400FF
             */
            startIndex += 16;
            byte[] major = Arrays.copyOfRange(scanRecord, startIndex, startIndex + 2);
            byte[] minor = Arrays.copyOfRange(scanRecord, startIndex + 2, startIndex + 4);

            if ((major[0] & 0xff) == 0x72 && major[1] == 0 && minor[0] == 0) { //01
                return 0;
            } else if ((major[0] & 0xff) >= 0x10 && (major[0] & 0xff) <= 0x6F) { //02
                return 1;
            } else if ((major[0] & 0xff) == 0x8F && (major[1] & 0xff) == 0xFF) { //03
                return 2;
            } else if ((major[0] & 0xff) >= 0x90 && (major[0] & 0xff) <= 0x9F) {  //04
                return 3;
            } else if((major[0] & 0xff)>=0x75 &&(major[0]&0xff)<=0x8f && (major[1]&0xff)==0xFE){ //05
                return 4;
            } else if(UUID.equalsIgnoreCase("FDA50693A4E24FB1AFCFC6EB076400FF")){ //废弃
                return 5;
            }else{
                Log.w("In pattern matching ", "no match  major:"+bytesToHexString(major)+"  minor:"+bytesToHexString(minor));
                Log.w("In pattern matching ", "no match  UUID:"+UUID);
                return 9;
            }
        }
        return -1;
    }
}