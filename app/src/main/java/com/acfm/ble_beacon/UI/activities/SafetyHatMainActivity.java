package com.acfm.ble_beacon.UI.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.acfm.ble_beacon.UI.fragments.BleBeaconTypeFragment;
import com.acfm.ble_beacon.UI.fragments.BleScanPageFragment;
import com.acfm.ble_transform.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SafetyHatMainActivity extends AppCompatActivity {
    private String[] titles = {"主页", "一代安全帽", "简版安全帽", "全功能安全帽"};

    private List<Fragment> mFragments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

        mFragments.add(BleBeaconTypeFragment.newInstance(mViewPager, false));
        mFragments.add(BleScanPageFragment.newInstance(6)); //一代安全帽 HELMET
        mFragments.add(BleScanPageFragment.newInstance(7)); // 简版安全帽 LJHMAA
        mFragments.add(BleScanPageFragment.newInstance(8)); // 全功能安全帽 LJHMBA

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
