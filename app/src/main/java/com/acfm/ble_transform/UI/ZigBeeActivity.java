package com.acfm.ble_transform.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.acfm.ble_transform.R;
import com.acfm.ble_transform.SQLiteUtil.SqliteDao;
import com.acfm.ble_transform.reprater_fra.re_Fragment;
import com.acfm.ble_transform.zigbee_fra.ZigFragment;

public class ZigBeeActivity extends Activity {
    private ZigFragment fragment;
    private boolean boolflag;
    private TextView clearsignal;
    SqliteDao sqliteDao=new SqliteDao(this);
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zig_bee);

        clearsignal=(TextView)findViewById(R.id.clearsignal);

        boolflag=false;

        fragment = new ZigFragment();
        FragmentManager fm = getFragmentManager();
        final FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.zigbee_fragment,fragment,"a").commitAllowingStateLoss();
        new Thread(new Runnable() {
            public void run() {
                /*
                 *   开启一个线程
                 *   每十秒更新一次协调器数据信息，即更新页面
                 * */
                while(!boolflag)
                {
                    FragmentManager fm = getFragmentManager();
                    // 开启Fragment事务
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.zigbee_fragment,new ZigFragment(),"a").commitAllowingStateLoss();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
        }).start();

        /*
         *   清空信号点击事件
         * */
        clearsignal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ZigBeeActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage(getString(R.string.clearsignal))
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //删除数据库
                                sqliteDao.clearFeedTable("zigBeeSignal");
                                FragmentManager fm = getFragmentManager();
                                // 开启Fragment事务
                                FragmentTransaction transaction = fm.beginTransaction();
                                transaction.replace(R.id.zigbee_fragment, new ZigFragment(), "a").commitAllowingStateLoss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }).create().show();
            }
        });
    }
    @Override
    protected void onDestroy()
    {
        boolflag=true;
        super.onDestroy();
        //解除广播接收器

    }


}

