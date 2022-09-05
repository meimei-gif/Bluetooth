package com.acfm.ble_transform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.acfm.ble_transform.SQLiteUtil.SqliteDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistorySafetyHat extends AppCompatActivity {
    private SearchView searchView;
    private SqliteDao sqliteDao;
    private TableLayout tableLayout;
    private TableRow tableRow1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_safety_hat);
        tableRow1=(TableRow)findViewById(R.id.tablehistory);
        tableLayout=(TableLayout)findViewById(R.id.id_tableLayout);
        sqliteDao=new SqliteDao(this);
        searchView=(SearchView)findViewById(R.id.search_history);
        searchView.onActionViewExpanded();
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("输入MAC");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {// 监听 SearchView 中的数据变化

            /*开始搜索listener*/
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                Log.d(Constants.TAG,"HistorySafetyHat -> onQueryTextSubmit -> queryText:-"+queryText+"-");
                tableLayout.removeAllViews();
                tableLayout.addView(tableRow1);
                tableLayout=(TableLayout)findViewById(R.id.id_tableLayout);
                List<JSONObject> list= sqliteDao.findHistoryByMac(queryText);
                //william
//                JSONObject one= null;
//                try {
//                    one = sqliteDao.findByHatMac(queryText);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                List<JSONObject> list = new ArrayList<>();
//                list.add(one);

                if(list!=null) {
                    long maxTime = 0;
                    for (int row = 0; row < list.size(); row++)
                    {
                        JSONObject jsonObject=list.get(row);
                        try {
                            Long retime = jsonObject.getLong("time");
                            long cur = System.currentTimeMillis();
                            if(cur - retime  >= 24*60*60*1000){
                                maxTime = Math.max(maxTime,retime);
                                continue;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        TableRow tableRow=new TableRow(getApplicationContext());
                        // hatId text,signalPath text,MAC text primary key,seq text,cmd text, temperature text,high text,power text,time integer

                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        try {
                            String sd = sdf.format(new Date(Long.parseLong(String.valueOf(jsonObject.getLong("time")))));
                            TextView time=new TextView(getApplication());
                            time.setText(" "+sd);
                            time.setGravity(Gravity.CENTER);
                            tableRow.addView(time);
                            TextView cmd=new TextView(getApplication());
                            cmd.setText(" "+jsonObject.getString("cmd"));
//                            cmd.setText("/");

                            cmd.setGravity(Gravity.CENTER);
                            tableRow.addView(cmd);
                            TextView seq=new TextView(getApplication());
                            seq.setText(jsonObject.getString("seq"));
//                            seq.setText("/");
                            seq.setGravity(Gravity.CENTER);
                            tableRow.addView(seq);
                            TextView id=new TextView(getApplication());
                            id.setText(" "+jsonObject.getString("hatId"));
                            id.setGravity(Gravity.CENTER);
                            tableRow.addView(id);
                            TextView mac=new TextView(getApplication());
                            mac.setText(" "+jsonObject.getString("MAC"));
//                            mac.setText("/");
                            mac.setGravity(Gravity.CENTER);
                            tableRow.addView(mac);
                            TextView signal=new TextView(getApplication());
                            signal.setText(jsonObject.getString("signalPath"));
                            signal.setGravity(Gravity.CENTER);
                            tableRow.addView(signal);
                            TextView tem=new TextView(getApplication());
                            tem.setText(jsonObject.getString("temperature"));
                            tem.setGravity(Gravity.CENTER);
                            tableRow.addView(tem);
                            TextView power=new TextView(getApplication());
                            power.setText(" "+jsonObject.getString("power"));
                            power.setGravity(Gravity.CENTER);
                            tableRow.addView(power);
                            TextView high=new TextView(getApplication());
                            high.setText(jsonObject.getString("high"));
                            high.setGravity(Gravity.CENTER);
                            tableRow.addView(high);
                            tableLayout.addView(tableRow);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    sqliteDao.deleteHistoryHatSafety(maxTime);//根据删除的最大时间值删除历史数据
                }
                else{
                    Toast.makeText(HistorySafetyHat.this, "No result!!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }
}