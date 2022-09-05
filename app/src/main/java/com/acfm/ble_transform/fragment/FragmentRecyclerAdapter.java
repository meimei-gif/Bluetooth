package com.acfm.ble_transform.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acfm.ble_transform.R;
import com.acfm.ble_transform.SQLiteUtil.SqliteDao;
import com.acfm.ble_transform.UI.SafetyHatInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FragmentRecyclerAdapter extends RecyclerView.Adapter<FragmentRecyclerAdapter.MyViewHolder> {

    SqliteDao sqliteDao;
    List<JSONObject> list;

    private Integer id;
    private Context context;
    private int signal;


    public FragmentRecyclerAdapter(Context context,Integer id,int signalss){
        this.context = context;
        this.id = id;
        this.signal=signalss;
        sqliteDao = new SqliteDao(context);
        try {
            if(signal==0) {
                list = sqliteDao.findAllHat();
            }
            else
            {
                list=sqliteDao.findByHatSignal(String.valueOf(signal));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        holder.imageButton.setImageResource(R.drawable.safety_hat);

        final Integer positionId = id + position;
        //传mac及数据
        if(list == null){
            jsonObject = null;
        }
        else{
            if(positionId < list.size())
                jsonObject = list.get(positionId);
            else jsonObject = null;
        }

        if(jsonObject == null){
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
                if((currentTime - time) > 10){
                    holder.imageButton.setBackgroundColor(Color.parseColor("#DF013A"));
                }else{
                    holder.imageButton.setBackgroundColor(Color.parseColor("#4DB376"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        holder.imageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context,v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_hatdelete,popupMenu.getMenu());

                //弹出式菜单的菜单项点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        try {
                            if(list == null) {
                                Toast.makeText(context, "无数据，无法删除", Toast.LENGTH_SHORT).show();
                            }else if(positionId >= list.size()){
                                Toast.makeText(context, "无数据，无法删除", Toast.LENGTH_SHORT).show();
                            }else{
                                sqliteDao.deleteByMac(list.get(positionId).getString("Mac"));    //  根据时间值删除数据库中的值
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });
                popupMenu.show();


                return false;
            }
        });
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> s  = new ArrayList<>();
                if(list == null){
                    Toast.makeText(context, "无数据", Toast.LENGTH_SHORT).show();
                }else{
                    if(positionId >= list.size())
                    {
                        Toast.makeText(context, "无数据", Toast.LENGTH_SHORT).show();
                    }
                    /*else if(list.get(positionId) == null){
                        Toast.makeText(context, "无数据"+positionId, Toast.LENGTH_SHORT).show();
                    }*/else{


                        try {
                            Intent intent = new Intent(context, SafetyHatInfo.class);
                            JSONObject jsonObject2 = list.get(positionId);
                            intent.putExtra("temperature",jsonObject2.getString("temperature"));
                            intent.putExtra("rssi",jsonObject2.getString("rssi"));
                            intent.putExtra("signalPath",jsonObject2.getString("signalPath"));

                            intent.putExtra("high",jsonObject2.getString("high"));
                            intent.putExtra("power",jsonObject2.getString("power"));

                            intent.putExtra("time",jsonObject2.getLong("time"));
                            intent.putExtra("status",jsonObject2.getString("status") );

                            intent.putExtra("humidity",jsonObject2.getString("humidity"));

                            intent.putExtra("Mac",jsonObject2.getString("Mac"));
                            intent.putExtra("hatId",jsonObject2.getString("hatId"));
                            context.startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }

        });

        try {
            if(jsonObject!=null)
            holder.textView.setText(list.get(positionId).getString("Mac"));
            else
                holder.textView.setText("");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //设置mac
    }

    @Override
    public int getItemCount() {
        return 64;
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
        public void OnItemClick(View view,Integer id);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
