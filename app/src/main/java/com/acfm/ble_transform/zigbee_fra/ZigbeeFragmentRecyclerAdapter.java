package com.acfm.ble_transform.zigbee_fra;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.acfm.ble_transform.Constants;
import com.acfm.ble_transform.R;
import com.acfm.ble_transform.UI.RepeaterInfo;
import com.acfm.ble_transform.SQLiteUtil.SqliteDao;
import com.acfm.ble_transform.ZigbeeInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ZigbeeFragmentRecyclerAdapter extends RecyclerView.Adapter<ZigbeeFragmentRecyclerAdapter.MyViewHolder> {

    SqliteDao sqliteDao;
    List<JSONObject> list = new ArrayList<>();  // william

    private Integer id;
    private Context context;


    public ZigbeeFragmentRecyclerAdapter(Context context, Integer id){
        this.context = context;
        this.id = id;
        sqliteDao = new SqliteDao(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(context,R.layout.recycler_item,null);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        JSONObject jsonObject = null;
        String seq = "";
        holder.imageButton.setImageResource(R.drawable.zigbee);

        final Integer positionId = id + position;
        //传mac及数据
        jsonObject = sqliteDao.findZigbeeSignal(positionId.toString());
        list.add(jsonObject);

        if(jsonObject == null){

            // 没有返回数据，说明没有设备，设置背景颜色为灰色
            holder.imageButton.setBackgroundColor(Color.parseColor("#848484"));


        }else{
            try {
                seq = jsonObject.getString("seq");
                Long time = jsonObject.getLong("time");
//                System.out.println(time);
                time = time/(1000*60);
                Long currentTime = System.currentTimeMillis();
                currentTime = currentTime/(1000*60);
                Date date = new Date();
                date.setTime(time);
//                System.out.println(new SimpleDateFormat().format(date));
//                System.out.println("时间：：：：：：：：：：：：：：：：："+time);
                if((currentTime - time) > 1){

                    // 超过1秒，离线状态，设置背景颜色为红色
                    holder.imageButton.setBackgroundColor(Color.parseColor("#DF013A"));
                }else{
                    // 没有超过5秒，在线状态，设置背景颜色为绿色
                    holder.imageButton.setBackgroundColor(Color.parseColor("#4DB376"));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 设置协调器信息
        holder.textView.setText("信道"+positionId);
        holder.seqView.setText(",序列号:"+seq);

        // 协调器点击事件 William
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "you clicked the zigbee image!", Toast.LENGTH_SHORT).show();
                // positionId 从1开始，而list从0开始  William
                Log.d(Constants.TAG,"ZigbeeFragmentRecyclerAdapter -> onClick ");
                if(((positionId-1)%16)>(list.size()-1))
                {
                    // 当前一个页面只有16个展示
                    Toast.makeText(context, "未初始化"+positionId, Toast.LENGTH_SHORT).show();
                }
                else if(list.get((positionId-1)%16) == null){
                    Toast.makeText(context, "无数据"+positionId, Toast.LENGTH_SHORT).show();
                }
                else{

                    /*
                    jsonObject.put("temperature",cursor.getString(cursor.getColumnIndex("temperature")));
            jsonObject.put("high",cursor.getString(cursor.getColumnIndex("high")));
            jsonObject.put("power",cursor.getString(cursor.getColumnIndex("power")));
            jsonObject.put("time",cursor.getLong(cursor.getColumnIndex("time")));
            jsonObject.put("status",cursor.getString(cursor.getColumnIndex("status")));
            jsonObject.put("humidity",cursor.getString(cursor.getColumnIndex("humidity")));
            jsonObject.put("Mac",cursor.getString(cursor.getColumnIndex("MAC")));
            jsonObject.put("hatId",cursor.getString(cursor.getColumnIndex("hatId")));

                     */
                    try {
                        Intent intent = new Intent(context, ZigbeeInfo.class);
                        JSONObject jsonObject2 = list.get((positionId-1)%16);
                        if(jsonObject2 == null){
                            Log.d(Constants.TAG,"ZigbeeFragmentRecyclerAdapter -> jsonObject2 == null ");
                        }
                        intent.putExtra("signalPath",jsonObject2.getString("signalPath"));

                        intent.putExtra("time",jsonObject2.getLong("time"));

                        intent.putExtra("seq",jsonObject2.getString("seq"));
                        intent.putExtra("zigbeeId",Integer.toString(positionId));
                        Log.d(Constants.TAG,"ZigbeeFragmentRecyclerAdapter -> startActivity ");
                        context.startActivity(intent);
                        Log.d(Constants.TAG,"ZigbeeFragmentRecyclerAdapter -> startActivityed ");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
        //设置mac
    }

    @Override
    public int getItemCount() {
        return 16;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageButton imageButton;
        private TextView textView;
        private TextView seqView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.ib_item);
            textView = itemView.findViewById(R.id.tv_item);
            seqView = itemView.findViewById(R.id.seq_item);
            // 设置点击事件 william
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(onItemClickListener != null){
//                        String str = String.valueOf(seqView.getText());
//                        Log.d(Constants.TAG,str);  // str=",seq:xx"
//                        String[] temp = str.split(":");
//                        if(temp.length == 2 ){
//                            Integer id = Integer.parseInt(temp[1]);
//                            if(id != null) {
//                                Log.d(Constants.TAG,id.toString());
//                                onItemClickListener.OnItemClick(view, id);
//                            }
//                            else {
//                                Log.d(Constants.TAG,"null");
//                                onItemClickListener.OnItemClick(view, -1);
//                            }
//                        }
//                        else {
//                            Log.d(Constants.TAG,"null");
//                            onItemClickListener.OnItemClick(view, -1);
//                        }
//
//                    }
//                }
//            });
            // 图片点击事件 william
//            imageButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(onItemClickListener != null){
//                        String str = String.valueOf(seqView.getText());
//                        Log.d(Constants.TAG,str);  // str=",seq:xx"
//                        String[] temp = str.split(":");
//                        if(temp.length == 2 ){
//                            Integer id = Integer.parseInt(temp[1]);
//                            if(id != null) {
//                                Log.d(Constants.TAG,id.toString());
//                                onItemClickListener.OnItemClick(view, id);
//                            }
//                            else {
//                                Log.d(Constants.TAG,"null");
//                                onItemClickListener.OnItemClick(view, -1);
//                            }
//                        }
//                        else {
//                            Log.d(Constants.TAG,"null");
//                            onItemClickListener.OnItemClick(view, -1);
//                        }
//
//                    }
//                }
//            });
        }
    }

    /*
     *   在这里设计点击事件？
     *
     * */
    public interface OnItemClickListener{
        public void OnItemClick(View view, Integer id);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
