package com.acfm.ble_transform.reprater_fra;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.acfm.ble_transform.R;
import com.acfm.ble_transform.UI.RepeaterInfo;
import com.acfm.ble_transform.SQLiteUtil.SqliteDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class re_FragmentRecyclerAdapter extends RecyclerView.Adapter<re_FragmentRecyclerAdapter.MyViewHolder> {

    SqliteDao sqliteDao;
    List<JSONObject> list = new ArrayList<>();

    private Integer id;
    private Context context;


    public re_FragmentRecyclerAdapter(Context context, Integer id){
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
        holder.imageButton.setImageResource(R.drawable.repeater);

        final Integer positionId = id + position;
        //传mac及数据
        try {
            jsonObject = sqliteDao.findByRepeater(positionId);
            list.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jsonObject == null){
            // 没有连接数据，设置为灰色
            holder.imageButton.setBackgroundColor(Color.parseColor("#848484"));

        }else{
            try {

                Long time = jsonObject.getLong("time");
//                System.out.println(time);
                time = time/(1000*60);
                Long currentTime = System.currentTimeMillis();
                currentTime = currentTime/(1000*60);
                Date date = new Date();
                date.setTime(time);
//                System.out.println(new SimpleDateFormat().format(date));
//                System.out.println("时间：：：：：：：：：：：：：：：：："+time);

                if((currentTime - time) > 5){


                    // 超过5秒，离线状态，设置背景颜色为红色
                    holder.imageButton.setBackgroundColor(Color.parseColor("#DF013A"));
                }else{
                    // 没有超过5秒，在线状态，设置背景颜色为绿色
                    holder.imageButton.setBackgroundColor(Color.parseColor("#4DB376"));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // positionId 从1开始，而list从0开始
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
                        Intent intent = new Intent(context, RepeaterInfo.class);
                        JSONObject jsonObject2 = list.get((positionId-1)%16);
                        intent.putExtra("temperature",jsonObject2.getString("temperature"));

                        intent.putExtra("high",jsonObject2.getString("high"));


                        intent.putExtra("time",jsonObject2.getLong("time"));


                        intent.putExtra("worktime",jsonObject2.getString("worktime"));
                        intent.putExtra("repeaterId",jsonObject2.getString("repeaterId"));
                       context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        });
        // 根据数据库情况设置中继器名称
        if(jsonObject!=null) {
            try {
                holder.textView.setText("中继器"+jsonObject.getString("repeaterId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            holder.textView.setText("中继器");
        }
        //设置mac
    }

    @Override
    public int getItemCount() {
        return 16;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageButton imageButton;
        private TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.ib_item);
            textView = itemView.findViewById(R.id.tv_item);

        }
    }

    public interface OnItemClickListener{
        public void OnItemClick(View view, Integer id);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
