package com.acfm.ble_transform.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.acfm.ble_transform.R;
import com.acfm.ble_transform.SQLiteUtil.SqliteDao;
import com.acfm.ble_transform.reprater_fra.re_Fragment;

public class RepeaterActivity extends Activity {
    private re_Fragment fragment;
    private boolean boolrepeater;
    private TextView clearrepeater;
    SqliteDao sqliteDao=new SqliteDao(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_repeater);
        // 清空中继器
        clearrepeater=(TextView)findViewById(R.id.clearallrepeater);
        boolrepeater=false;
        // 创建一个中继器的Fragment对象
        fragment = new re_Fragment();
        FragmentManager fm = getFragmentManager();
        final FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.repeater_fragment, fragment, "a").commitAllowingStateLoss();
        new Thread(new Runnable() {
            public void run() {
                /*
                 *   开启一个线程
                 *   每十秒更新一次中继器数据信息，即更新页面
                 * */
                while (!boolrepeater) {
                    FragmentManager fm = getFragmentManager();
                    // 开启Fragment事务
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.repeater_fragment, new re_Fragment(), "a").commitAllowingStateLoss();
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
         *   清空中继器点击事件
         * */
        clearrepeater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(RepeaterActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage(getString(R.string.clearrepeater))
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //删除数据库
                                sqliteDao.clearFeedTable("repeater");
                                FragmentManager fm = getFragmentManager();
                                // 开启Fragment事务
                                FragmentTransaction transaction = fm.beginTransaction();
                                transaction.replace(R.id.repeater_fragment, new re_Fragment(), "a").commitAllowingStateLoss();
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
        boolrepeater=true;
        super.onDestroy();
        //解除广播接收器

    }

}
