package com.acfm.ble_beacon.UI.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.acfm.ble_beacon.UI.fragments.BleBeaconTypeFragment;
import com.acfm.ble_beacon.UI.fragments.BleScanPageFragment;
import com.acfm.ble_beacon.bluetooth.BleScanHelper;
import com.acfm.ble_transform.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class BeaconMainActivity extends AppCompatActivity {
    private String[] titles = {"主页","EEA-01","EEA-02","EEA-03","EEA-04","EEA-05","EEA-06"};

    private List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_main);
        initView();
    }

    private void initView(){
        mFragments = new ArrayList<>();

        final ViewPager mViewPager = findViewById(R.id.mViewPager);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int i) {
                return mFragments.get(i);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }

        });

        mFragments.add(BleBeaconTypeFragment.newInstance(mViewPager, true));
        mFragments.add(BleScanPageFragment.newInstance(0)); //1
        mFragments.add(BleScanPageFragment.newInstance(1)); //2
        mFragments.add(BleScanPageFragment.newInstance(2)); //3
        mFragments.add(BleScanPageFragment.newInstance(3)); //4
        mFragments.add(BleScanPageFragment.newInstance(4)); //5
        mFragments.add(BleScanPageFragment.newInstance(5)); //6


        mViewPager.getAdapter().notifyDataSetChanged();

        TabLayout mTabLayout = findViewById(R.id.mTabLayout);
        for(int i=0;i<titles.length;i++){
            mTabLayout.addTab(mTabLayout.newTab());
        }
        mTabLayout.setupWithViewPager(mViewPager, false);
        for(int i=0;i<titles.length;i++){
            if(mTabLayout.getTabAt(i)!=null){
                mTabLayout.getTabAt(i).setText(titles[i]);
            }
        }
    }

}