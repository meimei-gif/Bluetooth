
package com.acfm.ble_beacon.UI.fragments;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



import com.acfm.ble_beacon.config.ConfigElement;
import com.acfm.ble_beacon.config.ConfigEvent;
import com.acfm.ble_beacon.config.ConfigSpec;
import com.acfm.ble_beacon.config.ConfigUi;
import com.acfm.ble_beacon.config.ConfigurationManager;
import com.acfm.ble_transform.R;

import com.mikepenz.iconics.view.IconicsImageButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

public class DeviceFragment extends Fragment {
    private final static String TAG = "DeviceFragment";

    private ConfigurationManager manager;
    private BluetoothDevice device;
    private TextView deviceName;
    private TextView deviceAddress;
    private TextView version;
    private TextView status;
    private TextView settingsPlaceholder;
    private ScrollView settingsScroll;
    private LinearLayout settingsList;
    private ProgressBar progress;

//    private Button apply;

    //private IconicsImageButton refresh, restore;
//    private Button refresh, restore;
    private Button refresh;
    private Button restore;
    private String versionString;


    private static final int TIMEREFLESH = 0;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TIMEREFLESH) {
                manager.readAllElements();
                updateStatus();
            }
        }
    };

    public DeviceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        EventBus.getDefault().unregister(this);
        manager = null;
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

//        deviceName = view.findViewById(R.id.deviceName);
//        deviceAddress = view.findViewById(R.id.deviceAddress);
//        version = view.findViewById(R.id.versionText);
//        status = view.findViewById(R.id.statusText);

        settingsPlaceholder = view.findViewById(R.id.settingsPlaceholder);
        settingsScroll = view.findViewById(R.id.settingsScroll);
        settingsList = view.findViewById(R.id.settingsList);
        progress = view.findViewById(R.id.progress);
//        apply = view.findViewById(R.id.applyButton);
        restore = view.findViewById(R.id.confrimButton);
        refresh = view.findViewById(R.id.refreshButton);
//        restore = view.findViewById(R.id.restoreButton);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        manager = ((ConfigurationManager.Holder) getActivity()).getConfigurationManager();
        device = manager.getDevice();


//        if (device.getName() != null)
//            deviceName.setText(device.getName());
//        deviceAddress.setText(device.getAddress());


        settingsList.setEnabled(false);

//        apply.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "Applying settings");
//                manager.writeModifiedElements();
//                updateStatus();
//            }
//        });

        refresh.setEnabled(false);
        restore.setEnabled(false);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Refresh settings");
                manager.readAllElements();
                updateStatus();
            }
        });

//        restore.setEnabled(false);
//        restore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new AlertDialog.Builder(getActivity())
//                        .setTitle(R.string.restore_dialog_title)
//                        .setMessage(R.string.restore_dialog_message)
//                        .setCancelable(true)
//                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Log.d(TAG, "Restore settings");
//                                for (ConfigElement element : manager.getElements()) {
//                                    element.resetWriteData();
//                                    element.getUi().updateView(manager, settingsList.findViewWithTag(element));
//                                }
//                                checkModified();
//                            }
//                        })
//                        .setNegativeButton(android.R.string.cancel, null)
//                        .show();
//            }
//        });

        if (manager.isReady() || manager.servicesDiscovered() && manager.configNotSupported()) {
            initializeSettingsList();
            checkModified();
            if (versionString == null) {
                if (manager.hasDeviceInfo(ConfigSpec.CHARACTERISTIC_SOFTWARE_REVISION_STRING)) {
                    manager.readDeviceInfo(ConfigSpec.CHARACTERISTIC_SOFTWARE_REVISION_STRING);
                } else {
                    if (manager.getVersion() != null)
                        versionString = manager.getVersion();
                    else
                        manager.readVersion();
                }
            }
        }

        if (versionString != null)
            version.setText(versionString);

        updateStatus();

        /**
         * 开启一个线程定时刷新当前页面
         */
        Timer myTimer = new Timer();
        myTimer.schedule(new MyTimerTask(),15*1000L);

    }

    /**
     * 内部匿名类：MyTimerTask
     * 作用：启动定时任务
     */
    public class MyTimerTask extends java.util.TimerTask{

        @Override
        public void run() {
            Log.d("William", "up time to Refresh");
            Message msg = new Message();
            msg.what = TIMEREFLESH;
            mHandler.sendMessage(msg);
        }
    }

    //初始化参数显示的函数
    private void initializeSettingsList() {
        if (manager.getElements().isEmpty()) {
            settingsPlaceholder.setVisibility(View.VISIBLE);
            settingsScroll.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);
            settingsPlaceholder.setText(R.string.no_elements_found);
            return;
        }
        settingsPlaceholder.setVisibility(View.GONE);
        settingsScroll.setVisibility(View.VISIBLE);
        ConfigUi.initializeSettingsList(manager, settingsList, getLayoutInflater(), getFragmentManager());
    }

    /*
    * 更新按钮、视图的显示状态
    * */
    private void updateStatus() {
        int statusText = R.string.status_connecting;
        switch (manager.getState()) {
            case ConfigurationManager.DISCONNECTED:
                statusText = R.string.status_disconnected;
                progress.setVisibility(View.GONE);
                settingsSetEnabled(false);
                restore.setEnabled(false);
                refresh.setEnabled(false);
                break;

            case ConfigurationManager.CONNECTING:
                statusText = R.string.status_connecting;
                progress.setVisibility(View.VISIBLE);
                break;

            case ConfigurationManager.CONNECTED:
                statusText = R.string.status_connected;
                break;

            case ConfigurationManager.SERVICE_DISCOVERY:
                statusText = R.string.status_service_discovery;
                break;

            case ConfigurationManager.ELEMENT_DISCOVERY:
                statusText = R.string.status_element_discovery;
                progress.setVisibility(View.VISIBLE);
//                apply.setEnabled(false);
                restore.setEnabled(false);
                refresh.setEnabled(false);
//                restore.setEnabled(false);
                break;

            case ConfigurationManager.READY:
                boolean pending = manager.isReadPending() || manager.isWritePending();
                if (manager.isReadPending())
                    statusText = R.string.status_reading;
                else if (manager.isWritePending())
                    statusText = R.string.status_applying;
                else
                    statusText = R.string.status_ready;
                progress.setVisibility(pending ? View.VISIBLE : View.GONE);
                settingsSetEnabled(!pending);
                refresh.setEnabled(!pending);
                restore.setEnabled(!pending);
                break;
        }

        //status.setText(statusText);

    }

    private void checkModified() {
        boolean modified = false;
        for (ConfigElement element : manager.getElements()) {
            if (element.modified()) {
                modified = true;
                break;
            }
        }
//        apply.setEnabled(modified);
//        restore.setEnabled(modified);
    }

    //设置参数的显示函数
    private void settingsSetEnabled(boolean enabled) {
        settingsList.setEnabled(enabled);
        for (int i = 0; i < settingsList.getChildCount(); ++i) {
            View view = settingsList.getChildAt(i);
            ConfigElement element = (ConfigElement) view.getTag(R.id.element);
            if (element == null)
                continue;
            view.setEnabled(enabled);
            element.getUi().updateView(manager, view);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onConnection(ConfigEvent.Connection event) {
        if (manager != event.manager)
            return;
        updateStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onServiceDiscovery(ConfigEvent.ServiceDiscovery event) {
        if (manager != event.manager)
            return;
        updateStatus();
        if (event.complete) {
            if (manager.hasDeviceInfo(ConfigSpec.CHARACTERISTIC_SOFTWARE_REVISION_STRING))
                manager.readDeviceInfo(ConfigSpec.CHARACTERISTIC_SOFTWARE_REVISION_STRING);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onElementDiscovery(ConfigEvent.ElementDiscovery event) {
        if (manager != event.manager)
            return;
        if (!event.complete) {
            updateStatus();
            settingsList.removeAllViews();
            settingsPlaceholder.setVisibility(View.VISIBLE);

            //settingsPlaceholder.setText(R.string.waiting_for_elements);
            return;
        }
        initializeSettingsList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onDeviceReady(ConfigEvent.Ready event) {
        if (manager != event.manager)
            return;
        Log.d(TAG, "Device ready");
        updateStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onNotSupported(ConfigEvent.NotSupported event) {
        if (manager != event.manager)
            return;
        Log.d(TAG, "Configuration service not supported");
        updateStatus();
        initializeSettingsList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onDeviceInfo(ConfigEvent.DeviceInfo event) {
        if (manager != event.manager)
            return;
        versionString = event.info;

        //version.setText(versionString);

    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onVersion(ConfigEvent.Version event) {
        if (manager != event.manager)
            return;
        if (versionString == null && !manager.hasDeviceInfo(ConfigSpec.CHARACTERISTIC_SOFTWARE_REVISION_STRING)) {
            versionString = manager.getVersion();
            version.setText(versionString);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onElementRead(ConfigEvent.Read event) {
        if (manager != event.manager)
            return;
        updateStatus();
        if (event.failed()) {
            Toast.makeText(getActivity(), R.string.read_failed_message, Toast.LENGTH_SHORT).show();
        } else {
            event.element.getUi().updateView(manager, settingsList.findViewWithTag(event.element));
        }
        checkModified();
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onElementWrite(ConfigEvent.Write event) {
        if (manager != event.manager)
            return;
        updateStatus();
        if (event.failed()) {
            Toast.makeText(getActivity(), R.string.write_failed_message, Toast.LENGTH_SHORT).show();
        } else {
            for (ConfigElement element : event.elements) {
                element.getUi().updateView(manager, settingsList.findViewWithTag(element));
            }
        }
        checkModified();
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onError(ConfigEvent.Error event) {
        if (manager != event.manager)
            return;
        updateStatus();
        switch (event.error) {
            case ConfigEvent.ERROR_INIT_CONFIGURATION_SERVICE:
                Log.e(TAG, "Configuration service initialization failed");
                progress.setVisibility(View.GONE);
                settingsPlaceholder.setText(R.string.no_elements_found);
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.config_init_failed, Toast.LENGTH_LONG).show();
                break;

            case ConfigEvent.ERROR_INIT_READ:
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.read_failed_message, Toast.LENGTH_SHORT).show();
                break;

            case ConfigEvent.ERROR_INIT_WRITE:
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.write_failed_message, Toast.LENGTH_SHORT).show();
                break;
        }
        checkModified();
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onDialogConfirm(ConfigUi.DialogConfirm event) {
        if (manager != event.manager)
            return;
        updateStatus();
        event.element.getUi().updateView(manager, settingsList.findViewWithTag(event.element));
        checkModified();
    }
}
