package com.acfm.ble_beacon.UI.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acfm.ble_beacon.adapter.BleBeaconTypeAdapter;
import com.acfm.ble_beacon.adapter.BleScanAdapter;
import com.acfm.ble_transform.R;


public class BleBeaconTypeFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private BleBeaconTypeAdapter mAdapter;

    private ViewPager viewPager;

    private final boolean isBeacon;

    public BleBeaconTypeFragment(ViewPager viewPager, boolean isBeacon) {
        // Required empty public constructor
        this.viewPager = viewPager;
        this.isBeacon = isBeacon;
    }

    public static BleBeaconTypeFragment newInstance(ViewPager viewPager, boolean isBeacon) {
       return new BleBeaconTypeFragment(viewPager, isBeacon);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_ble_beacon_type, container, false);
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.fragment_ble_beacon_type, container, false
        );
        mRecyclerView = view.findViewById(R.id.mRecyclerView_BeaconType);
        return view;
    }

    private void initView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); //使用线性布局管理器
        mAdapter = new BleBeaconTypeAdapter(getActivity(), viewPager, isBeacon);
        mRecyclerView.setAdapter(mAdapter);
    }
}