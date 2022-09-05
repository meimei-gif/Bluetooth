package com.acfm.ble_transform.SQLiteUtil;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.acfm.ble_transform.Constants;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SqliteDao {
    private final SQLiteHelper mHelper;

    public void clearFeedTable(String tablename) {
        String sql = "DELETE FROM " + tablename;
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    public SqliteDao(Context context) {
        mHelper = new SQLiteHelper(context);
    }

    //repeaterId text,high text,temperate text,power text,worktime text
    public void insertRepeater(String repeaterId, String high, String temperature, String worktime, long time) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sql = "insert into repeater (repeaterId,high,temperature,worktime,time) values(?,?,?,?,?)";
        db.execSQL(sql, new Object[]{repeaterId, high, temperature, worktime, time});
        db.close();
    }

    public void updateByRepeaterId(String repeaterId, String high, String temperature, String worktime, long time) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sql = "update repeater set high = ?,temperature = ?,worktime = ?,time = ? where repeaterId = ?";

        db.execSQL(sql,new Object[]{high,temperature,worktime,time,repeaterId});

        db.close();
    }

    public JSONObject findByRepeaterId(String repeaterId) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select * from repeater where repeaterId = '" + repeaterId + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            jsonObject.put("high", cursor.getString(cursor.getColumnIndex("high")) == null ? "" : cursor.getString(cursor.getColumnIndex("high")));
            jsonObject.put("temperature", cursor.getString(cursor.getColumnIndex("temperature")) == null ? "" : cursor.getString(cursor.getColumnIndex("temperature")));
            jsonObject.put("worktime", cursor.getString(cursor.getColumnIndex("worktime")) == null ? "" : cursor.getString(cursor.getColumnIndex("worktime")));
            jsonObject.put("time", cursor.getLong(cursor.getColumnIndex("time")) == 0 ? "" : cursor.getString(cursor.getColumnIndex("time")));
            cursor.close();
            return jsonObject;
        } else {
            cursor.close();
            db.close();
            return null;
        }

    }

    public JSONObject findByRepeater(int id) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        SQLiteDatabase db = mHelper.getReadableDatabase();
//        String sql = "select * from repeater where id = "+id;
        String sql = "select * from repeater where repeaterId = "+id;
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor.getCount() != 0){

            cursor.moveToFirst();
            jsonObject.put("repeaterId", cursor.getString(cursor.getColumnIndex("repeaterId")));
            jsonObject.put("high", cursor.getString(cursor.getColumnIndex("high")) == null ? "" : cursor.getString(cursor.getColumnIndex("high")));
            jsonObject.put("temperature", cursor.getString(cursor.getColumnIndex("temperature")) == null ? "" : cursor.getString(cursor.getColumnIndex("temperature")));
            jsonObject.put("worktime", cursor.getString(cursor.getColumnIndex("worktime")) == null ? "" : cursor.getString(cursor.getColumnIndex("worktime")));
            jsonObject.put("time", cursor.getLong(cursor.getColumnIndex("time")));
            cursor.close();
            return jsonObject;
        } else {
            cursor.close();
            db.close();
            return null;
        }

    }

    //协调器
    public void insertZigbee(String signalPath, long time, String seq) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sql = "insert into zigBeeSignal (signalPath,time,seq) values(?,?,?)";
        db.execSQL(sql, new Object[]{signalPath, time, seq});
        db.close();
    }

    public JSONObject findZigbeeSignal(String signalPath) {
        JSONObject jsonObject = new JSONObject();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select * from zigBeeSignal where signalPath = " + "'" + signalPath + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                jsonObject.put("signalPath", cursor.getString(cursor.getColumnIndex("signalPath")) == null ? "" : cursor.getString(cursor.getColumnIndex("signalPath")));
                jsonObject.put("time", cursor.getLong(cursor.getColumnIndex("time")));
                jsonObject.put("seq", cursor.getString(cursor.getColumnIndex("seq")) == null ? "" : cursor.getString(cursor.getColumnIndex("seq")));
                db.close();
                return jsonObject;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        cursor.close();
        db.close();
        return null;
    }

    public void updateSignal(String signalPath, long time, String seq) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sql = "update zigBeeSignal set time = ?,seq = ? where signalPath = ?";
        db.execSQL(sql, new Object[]{time, seq, signalPath});
        db.close();
        return;
    }


    //安全帽
    public void insertHatSafety(String hatId, String signalPath, String rssi, String MAC, String temperature, String high, String power, long time, String status, String humidity) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sql = "insert into safetyHat (hatId,signalPath,rssi,MAC,temperature,high,power,time,status,humidity) values(?,?,?,?,?,?,?,?,?,?)";
        db.execSQL(sql, new Object[]{hatId, signalPath, rssi, MAC, temperature, high, power, time, status, humidity});
        db.close();
    }


    public void updateHatByMac(String hatId, String signalPath, String rssi, String MAC, String temperature, String high, String power, long time, String status, String humidity) {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        if("0".equals(hatId)){

            String sql = "update safetyHat set time = ?where MAC = ?";

            db.execSQL(sql, new Object[]{time, MAC});
            db.close();
            return;
        }
        if (status == null) {
            String sql = "update safetyHat set hatId = ?,signalPath = ?,rssi = ?,temperature = ?,high = ?,power = ?,time = ?,humidity = ? where MAC = ?";
            db.execSQL(sql, new Object[]{hatId, signalPath, rssi, temperature, high, power, time, humidity, MAC});

        } else {
            String sql = "update safetyHat set hatId = ?,signalPath = ?,rssi = ?,temperature = ?,high = ?,power = ?,time = ?,status = ?,humidity = ? where MAC = ?";
            db.execSQL(sql, new Object[]{hatId, signalPath, rssi, temperature, high, power, time, status, humidity, MAC});
        }
        db.close();
//        Log.d(Constants.TAG,"SqliteDao -> updateHatByMac: 更新数据结束 ");
    }

    public void updateByHatId(String hatId, String signalPath, String rssi, String temperature, String high, String power, long time, String status, String humidity) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sql = "update safetyHat set rssi = ?, temperature = ?,high = ?,power = ?,time = ?,status = ?,humidity = ? where hatId = ? and signalPath = ?";
        db.execSQL(sql, new Object[]{rssi, temperature, high, power, time, status, humidity, hatId, signalPath});
        db.close();
//        Log.d(Constants.TAG,"SqliteDao -> updateByHatId: 更新数据结束 ");
    }

    public JSONObject findById(int id) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select * from safetyHat where id = " + id;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            jsonObject.put("temperature", cursor.getString(cursor.getColumnIndex("temperature")) == null ? "" : cursor.getString(cursor.getColumnIndex("temperature")));
            jsonObject.put("signalPath", cursor.getString(cursor.getColumnIndex("signalPath")) == null ? "" : cursor.getString(cursor.getColumnIndex("signalPath")));
            jsonObject.put("rssi", cursor.getString(cursor.getColumnIndex("rssi")) == null ? "" : cursor.getString(cursor.getColumnIndex("rssi")));
            jsonObject.put("high", cursor.getString(cursor.getColumnIndex("high")) == null ? "" : cursor.getString(cursor.getColumnIndex("high")));
            jsonObject.put("power", cursor.getString(cursor.getColumnIndex("power")) == null ? "" : cursor.getString(cursor.getColumnIndex("power")));
            jsonObject.put("time", cursor.getLong(cursor.getColumnIndex("time")));
            jsonObject.put("status", cursor.getString(cursor.getColumnIndex("status")) == null ? "" : cursor.getString(cursor.getColumnIndex("status")));
            jsonObject.put("humidity", cursor.getString(cursor.getColumnIndex("humidity")) == null ? "" : cursor.getString(cursor.getColumnIndex("humidity")));
            jsonObject.put("Mac", cursor.getString(cursor.getColumnIndex("MAC")));
            jsonObject.put("hatId", cursor.getString(cursor.getColumnIndex("hatId")) == null ? "" : cursor.getString(cursor.getColumnIndex("hatId")));
            cursor.close();
            db.close();
            return jsonObject;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

    public List<JSONObject> findAllHat() throws JSONException {
        List<JSONObject> list = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select * from safetyHat";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() != 0) {

            while (cursor.moveToNext()) {
                //cursor.moveToFirst();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("temperature", cursor.getString(cursor.getColumnIndex("temperature")) == null ? "" : cursor.getString(cursor.getColumnIndex("temperature")));
                jsonObject.put("signalPath", cursor.getString(cursor.getColumnIndex("signalPath")) == null ? "" : cursor.getString(cursor.getColumnIndex("signalPath")));
                jsonObject.put("rssi", cursor.getString(cursor.getColumnIndex("rssi")) == null ? "" : cursor.getString(cursor.getColumnIndex("rssi")));
                jsonObject.put("high", cursor.getString(cursor.getColumnIndex("high")) == null ? "" : cursor.getString(cursor.getColumnIndex("high")));
                jsonObject.put("power", cursor.getString(cursor.getColumnIndex("power")) == null ? "" : cursor.getString(cursor.getColumnIndex("power")));
                jsonObject.put("time", cursor.getLong(cursor.getColumnIndex("time")));
                jsonObject.put("status", cursor.getString(cursor.getColumnIndex("status")) == null ? "" : cursor.getString(cursor.getColumnIndex("status")));
                jsonObject.put("humidity", cursor.getString(cursor.getColumnIndex("humidity")) == null ? "" : cursor.getString(cursor.getColumnIndex("humidity")));
                jsonObject.put("Mac", cursor.getString(cursor.getColumnIndex("MAC")));
                jsonObject.put("hatId", cursor.getString(cursor.getColumnIndex("hatId")) == null ? "" : cursor.getString(cursor.getColumnIndex("hatId")));
                list.add(jsonObject);
            }
            cursor.close();
            db.close();
            return list;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

    public JSONObject findByHatId(String hatId, String signalPath) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select * from safetyHat where hatId = '" + hatId + "' and signalPath = " + "'" + signalPath + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            jsonObject.put("rssi", cursor.getString(cursor.getColumnIndex("rssi")) == null ? "" : cursor.getString(cursor.getColumnIndex("rssi")));
            jsonObject.put("temperature", cursor.getString(cursor.getColumnIndex("temperature")) == null ? "" : cursor.getString(cursor.getColumnIndex("temperature")));
            jsonObject.put("high", cursor.getString(cursor.getColumnIndex("high")) == null ? "" : cursor.getString(cursor.getColumnIndex("high")));
            jsonObject.put("power", cursor.getString(cursor.getColumnIndex("power")) == null ? "" : cursor.getString(cursor.getColumnIndex("power")));
            jsonObject.put("time", cursor.getLong(cursor.getColumnIndex("time")));
            jsonObject.put("status", cursor.getString(cursor.getColumnIndex("status")) == null ? "" : cursor.getString(cursor.getColumnIndex("status")));
            jsonObject.put("humidity", cursor.getString(cursor.getColumnIndex("humidity")) == null ? "" : cursor.getString(cursor.getColumnIndex("humidity")));
            jsonObject.put("Mac", cursor.getString(cursor.getColumnIndex("MAC")));
            cursor.close();
            db.close();
            return jsonObject;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

    public List<JSONObject> findByHatSignal(String signalPath) throws JSONException {
        List<JSONObject> list = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select * from safetyHat where signalPath = " + "'" + signalPath + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                //cursor.moveToFirst();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("temperature", cursor.getString(cursor.getColumnIndex("temperature")) == null ? "" : cursor.getString(cursor.getColumnIndex("temperature")));
                jsonObject.put("signalPath", cursor.getString(cursor.getColumnIndex("signalPath")) == null ? "" : cursor.getString(cursor.getColumnIndex("signalPath")));
                jsonObject.put("rssi", cursor.getString(cursor.getColumnIndex("rssi")) == null ? "" : cursor.getString(cursor.getColumnIndex("rssi")));
                jsonObject.put("high", cursor.getString(cursor.getColumnIndex("high")) == null ? "" : cursor.getString(cursor.getColumnIndex("high")));
                jsonObject.put("power", cursor.getString(cursor.getColumnIndex("power")) == null ? "" : cursor.getString(cursor.getColumnIndex("power")));
                jsonObject.put("time", cursor.getLong(cursor.getColumnIndex("time")));
                jsonObject.put("status", cursor.getString(cursor.getColumnIndex("status")) == null ? "" : cursor.getString(cursor.getColumnIndex("status")));
                jsonObject.put("humidity", cursor.getString(cursor.getColumnIndex("humidity")) == null ? "" : cursor.getString(cursor.getColumnIndex("humidity")));
                jsonObject.put("Mac", cursor.getString(cursor.getColumnIndex("MAC")));
                jsonObject.put("hatId", cursor.getString(cursor.getColumnIndex("hatId")) == null ? "" : cursor.getString(cursor.getColumnIndex("hatId")));
                list.add(jsonObject);
            }
            cursor.close();
            db.close();
            return list;

        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

    public JSONObject findByHatMac(String Mac) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select * from safetyHat where MAC = '" + Mac + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            jsonObject.put("rssi", cursor.getString(cursor.getColumnIndex("rssi")) == null ? "" : cursor.getString(cursor.getColumnIndex("rssi")));
            jsonObject.put("temperature", cursor.getString(cursor.getColumnIndex("temperature")) == null ? "" : cursor.getString(cursor.getColumnIndex("temperature")));
            jsonObject.put("high", cursor.getString(cursor.getColumnIndex("high")) == null ? "" : cursor.getString(cursor.getColumnIndex("high")));
            jsonObject.put("power", cursor.getString(cursor.getColumnIndex("power")) == null ? "" : cursor.getString(cursor.getColumnIndex("power")));
            jsonObject.put("time", cursor.getLong(cursor.getColumnIndex("time")));
            jsonObject.put("status", cursor.getString(cursor.getColumnIndex("status")) == null ? "" : cursor.getString(cursor.getColumnIndex("status")));
            jsonObject.put("humidity", cursor.getString(cursor.getColumnIndex("humidity")) == null ? "" : cursor.getString(cursor.getColumnIndex("humidity")));
            jsonObject.put("hatId", cursor.getString(cursor.getColumnIndex("hatId")) == null ? "" : cursor.getString(cursor.getColumnIndex("hatId")));
            jsonObject.put("signalPath", cursor.getString(cursor.getColumnIndex("signalPath")) == null ? "" : cursor.getString(cursor.getColumnIndex("signalPath")));

            cursor.close();
            db.close();
            return jsonObject;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

    public void deleteByMac(String mac)
    {


        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sql = "delete from safetyHat where MAC=" + "'" + mac + "'";

        db.execSQL(sql);
        db.close();
    }


    /*
     * 安全帽历史数据插入
     * */
    public void insertHistoryHatSafety(String hatId, String signalPath, String MAC, String seq, String cmd, String temperature, String high, String power, long time) {

        if (MAC == null) {
            MAC = findMacByIdandSingnal(hatId, signalPath);
        }
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sql = "insert into historySafetyHat (hatId,signalPath,MAC,seq,cmd,temperature,high,power,time) values(?,?,?,?,?,?,?,?,?)";

        db.execSQL(sql, new Object[]{hatId, signalPath, MAC, seq, cmd, temperature, high, power, time});
        db.close();
    }

    /*
     * 安全帽历史数据删除
     * */
    public void deleteHistoryHatSafety(long time){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sql="delete from historySafetyHat where time <= ?";
        db.execSQL(sql,new Object[]{time});
        db.close();
    }

    //查询最近时间的数据帧
    public long findLastTime() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select time from historySafetyHat order by time desc";
        Cursor cursor = db.rawQuery(sql, null);
        long lastTime = 0;
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            lastTime = cursor.getLong(cursor.getColumnIndex("time")) == -1 ? 0 : cursor.getLong(cursor.getColumnIndex("time"));
        }
        cursor.close();
        db.close();
        return lastTime;
    }

    public List<JSONObject> findHistoryByMac(String MAC) {
        List<JSONObject> list = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String sql = "select * from historySafetyHat where MAC = " + "'" + MAC + "' order by time desc";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() != 0){
            while(cursor.moveToNext()){
                //cursor.moveToFirst();
                JSONObject jsonObject = new JSONObject();
                try {
                    //hatId text,signalPath text,MAC text primary key,seq text,cmd text, temperature text,high text,power text,time integer
                    jsonObject.put("hatId", cursor.getString(cursor.getColumnIndex("hatId")) == null ? "" : cursor.getString(cursor.getColumnIndex("hatId")));
                    jsonObject.put("signalPath", cursor.getString(cursor.getColumnIndex("signalPath")) == null ? "" : cursor.getString(cursor.getColumnIndex("signalPath")));
                    jsonObject.put("seq", cursor.getString(cursor.getColumnIndex("seq")) == null ? "" : cursor.getString(cursor.getColumnIndex("seq")));
                    jsonObject.put("cmd", cursor.getString(cursor.getColumnIndex("cmd")) == null ? "" : cursor.getString(cursor.getColumnIndex("cmd")));
                    jsonObject.put("time", cursor.getLong(cursor.getColumnIndex("time")));
                    jsonObject.put("MAC", cursor.getString(cursor.getColumnIndex("MAC")));
                    jsonObject.put("temperature", cursor.getString(cursor.getColumnIndex("temperature")) == null ? "" : cursor.getString(cursor.getColumnIndex("temperature")));
                    jsonObject.put("high", cursor.getString(cursor.getColumnIndex("high")) == null ? "" : cursor.getString(cursor.getColumnIndex("high")));
                    jsonObject.put("power", cursor.getString(cursor.getColumnIndex("power")) == null ? "" : cursor.getString(cursor.getColumnIndex("power")));
                    list.add(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            cursor.close();
            db.close();
            return list;

        } else {
            cursor.close();
            db.close();
            return null;
        }

    }

    public String findMacByIdandSingnal(String hatId, String signalPath) {
        String ans = "";
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String sql = "select MAC from safetyHat  where hatId='" + hatId + "'" + " and signalPath='" + signalPath + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            ans = cursor.getString(cursor.getColumnIndex("MAC")) == null ? "" : cursor.getString(cursor.getColumnIndex("MAC"));
        }
        cursor.close();
        db.close();
        return ans;
    }


}


