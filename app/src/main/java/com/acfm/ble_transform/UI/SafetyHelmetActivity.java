package com.acfm.ble_transform.UI;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.acfm.ble_transform.R;
import com.acfm.ble_transform.SQLiteUtil.SqliteDao;
import com.acfm.ble_transform.fragment.Fragment1;
import com.acfm.ble_transform.fragment.Fragment2;
import com.acfm.ble_transform.fragment.Fragment3;
import com.acfm.ble_transform.fragment.Fragment4;

public class SafetyHelmetActivity extends Activity {

    SqliteDao sqliteDao=new SqliteDao(this);
    private Fragment1 fragment1;
    private Fragment2 fragment2;
    private Fragment3 fragment3;
    private Fragment4 fragment4;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioButton radioButton4;
    private RadioGroup  radioGroup;
    private SearchView searchView;
    private int boolhat;
    private int whichsignal=0;
    private TextView clearhat;
    private ImageView choosesignal;

    public SafetyHelmetActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_helmet);
        clearhat=(TextView)findViewById(R.id.clearhat);
        choosesignal=(ImageView)findViewById(R.id.signalpath);

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment4 = new Fragment4();
        boolhat = 1;// 设置初始安全帽页面为第一个页面
        FragmentManager fm = getFragmentManager();
        final FragmentTransaction transaction = fm.beginTransaction();

        radioGroup = findViewById(R.id.rg_home);

        radioButton1 = findViewById(R.id.rb1);
        radioButton2 = findViewById(R.id.rb2);
        radioButton3 = findViewById(R.id.rb3);
        radioButton4 = findViewById(R.id.rb4);
        // 初始化 默认信号频道为0 即全部信号频道
        Bundle a=new Bundle();
        a.putInt("signal",0);
        fragment1.setArguments(a);
        transaction.add(R.id.fragment, fragment1, "a").commitAllowingStateLoss();
        new Thread(new Runnable() {
            public void run() {
                /*
                 *   开启一个线程
                 *   每十秒更新一次安全帽数据信息，即更新页面
                 * */
                while (boolhat == 1) {
                    FragmentManager fm = getFragmentManager();
                    // 开启Fragment事务
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.fragment, new Fragment1(), "a").commitAllowingStateLoss();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
        }).start();

        // 频道选择点击事件
        choosesignal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String [] s ={"全部信道","信道1","信道2","信道3","信道4","信道5","信道6","信道7","信道8","信道9","信道10","信道11","信道12","信道13","信道14","信道15","信道16"};
                AlertDialog.Builder items = new AlertDialog.Builder(SafetyHelmetActivity.this);
//        items.setMessage("列表对话框");
                items.setTitle("根据信道筛选安全帽");
                items.setItems(s, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        whichsignal=which;
                        switch(boolhat)
                        {
                            case 1:
                                FragmentManager fm1 = getFragmentManager();
                                // 开启Fragment事务
                                FragmentTransaction transaction1 = fm1.beginTransaction();
                                Fragment1 fragment11=new Fragment1();
                                Bundle b1=new Bundle();
                                b1.putInt("signal",which);
                                fragment11.setArguments(b1);
                                transaction1.replace(R.id.fragment, fragment11, "a").commitAllowingStateLoss();
                                break;
                            case 2:
                                FragmentManager fm2 = getFragmentManager();
                                // 开启Fragment事务
                                FragmentTransaction transaction2 = fm2.beginTransaction();
                                Fragment2 fragment22=new Fragment2();
                                Bundle b2=new Bundle();
                                b2.putInt("signal",which);
                                fragment22.setArguments(b2);
                                transaction2.replace(R.id.fragment, fragment22, "a").commitAllowingStateLoss();
                             break;
                            case 3:
                                FragmentManager fm3 = getFragmentManager();
                                // 开启Fragment事务
                                FragmentTransaction transaction3 = fm3.beginTransaction();
                                Fragment3 fragment33=new Fragment3();
                                Bundle b3=new Bundle();
                                b3.putInt("signal",which);
                                fragment33.setArguments(b3);
                                transaction3.replace(R.id.fragment, fragment33, "a").commitAllowingStateLoss();
                           break;
                            case 4:
                                FragmentManager fm4 = getFragmentManager();
                                // 开启Fragment事务
                                FragmentTransaction transaction4 = fm4.beginTransaction();
                                Fragment4 fragment44=new Fragment4();
                                Bundle b4=new Bundle();
                                b4.putInt("signal",which);
                                fragment44.setArguments(b4);
                                transaction4.replace(R.id.fragment, fragment44, "a").commitAllowingStateLoss();
                                break;
                        }
                    }
                });
                items.create().show();
            }
        });

        clearhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SafetyHelmetActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage(getString(R.string.clearhat))
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //删除数据库
                                sqliteDao.clearFeedTable("safetyHat");
                                FragmentManager fm = getFragmentManager();
                                // 开启Fragment事务
                                FragmentTransaction transaction = fm.beginTransaction();
                                transaction.replace(R.id.fragment, new Fragment1(), "a").commitAllowingStateLoss();
                                //boolhat=1;**********************************
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }).create().show();
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                FragmentManager fm = getFragmentManager();
                // 开启Fragment事务
                FragmentTransaction transaction = fm.beginTransaction();
                switch (i) {
                    case R.id.rb1:
                        boolhat = 1;
                        Bundle c1=new Bundle();
                        c1.putInt("signal",whichsignal);
                        fragment1.setArguments(c1);
                        transaction.replace(R.id.fragment, fragment1);
                        break;
                    case R.id.rb2:
                        boolhat=2;
                        Bundle c2=new Bundle();
                        c2.putInt("signal",whichsignal);
                        fragment2.setArguments(c2);
                        transaction.replace(R.id.fragment, fragment2);
                        new Thread(new Runnable() {
                            public void run() {
                                while (boolhat == 2) {
                                    FragmentManager fm = getFragmentManager();
                                    // 开启Fragment事务
                                    FragmentTransaction transaction = fm.beginTransaction();
                                    transaction.replace(R.id.fragment, new Fragment2(), "a").commitAllowingStateLoss();
                                    try {
                                        Thread.sleep(10000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                return;
                            }
                        }).start();
                        break;
                    case R.id.rb3:
                        boolhat=3;
                        Bundle c3=new Bundle();
                        c3.putInt("signal",whichsignal);
                        fragment3.setArguments(c3);
                        transaction.replace(R.id.fragment, fragment3);
                        new Thread(new Runnable() {
                            public void run() {
                                while (boolhat == 3) {
                                    FragmentManager fm = getFragmentManager();
                                    // 开启Fragment事务
                                    FragmentTransaction transaction = fm.beginTransaction();
                                    transaction.replace(R.id.fragment, new Fragment3(), "a").commitAllowingStateLoss();
                                    try {
                                        Thread.sleep(10000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                return;
                            }
                        }).start();
                        break;
                    case R.id.rb4:
                        boolhat=4;
                        Bundle c4=new Bundle();
                        c4.putInt("signal",whichsignal);
                        fragment4.setArguments(c4);
                        transaction.replace(R.id.fragment, fragment4);
                        new Thread(new Runnable() {
                            public void run() {
                                while (boolhat == 4) {
                                    FragmentManager fm = getFragmentManager();
                                    // 开启Fragment事务
                                    FragmentTransaction transaction = fm.beginTransaction();
                                    transaction.replace(R.id.fragment, new Fragment4(), "a").commitAllowingStateLoss();
                                    try {
                                        Thread.sleep(10000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                return;
                            }
                        }).start();
                        break;
                }
                transaction.commit();
            }
        });


    }

    @Override
    protected void onDestroy()
    {
        boolhat=0;
        super.onDestroy();
        //解除广播接收器

    }


}