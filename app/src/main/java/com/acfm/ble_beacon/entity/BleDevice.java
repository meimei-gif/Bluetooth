package com.acfm.ble_beacon.entity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;

public class BleDevice {
    BluetoothDevice device;
    Integer rssi;
    byte[] scanRecordBytes;
    Boolean isConnectable = true;
    ScanRecord scanRecord;

    public BleDevice(BluetoothDevice device, Integer rssi, byte[] scanRecordBytes, Boolean isConnectable, ScanRecord scanRecord) {
        this.device = device;
        this.rssi = rssi;
        this.scanRecordBytes = scanRecordBytes;
        this.isConnectable = isConnectable;
        this.scanRecord = scanRecord;
    }

    public BleDevice(BluetoothDevice device, Integer rssi, byte[] scanRecordBytes) {
        this.device = device;
        this.rssi = rssi;
        this.scanRecordBytes = scanRecordBytes;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public byte[] getScanRecordBytes() {
        return scanRecordBytes;
    }

    public void setScanRecordBytes(byte[] scanRecordBytes) {
        this.scanRecordBytes = scanRecordBytes;
    }

    public Boolean getConnectable() {
        return isConnectable;
    }

    public void setConnectable(Boolean connectable) {
        isConnectable = connectable;
    }

    public ScanRecord getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(ScanRecord scanRecord) {
        this.scanRecord = scanRecord;
    }
}
