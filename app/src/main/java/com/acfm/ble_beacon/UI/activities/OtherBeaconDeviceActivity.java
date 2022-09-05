package com.acfm.ble_beacon.UI.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.acfm.ble_beacon.UI.fragments.BleScanPageFragment;
import com.acfm.ble_transform.R;

public class OtherBeaconDeviceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_device_list);
        changeFragment(new BleScanPageFragment(9)); // 9
    }

    private void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_for_other_device, fragment).commit();
    }
}
