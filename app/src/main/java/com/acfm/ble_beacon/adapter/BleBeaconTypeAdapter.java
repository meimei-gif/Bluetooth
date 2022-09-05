package com.acfm.ble_beacon.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.acfm.ble_transform.R;

public class BleBeaconTypeAdapter extends RecyclerView.Adapter<BleBeaconTypeAdapter.BeaconTypeViewHolder> {

    /*
           LJ-SS-EEA-01   工作面大信标  Major/Minor  72:00:00:00-72:00:00:FF
           LJ-SS-EEA-02   语音信标      Major/Minor  10:00:00:00-6F:FF:FF:FF
           LJ-SS-EEA-03   雷达信标      Major/Minor  8F:FF:00:00-8F:FF:FF:FF
           LJ-SS-EEA-04   巡检信标      Major/Minor  90:00:00:00-9F:FF:FF:FF
           LJ-SS-EEA-05   辅助定位信标  Major/Minor  75:00:00:00-8F:FE:FF:FF
           疑似废弃信标   UUID         FDA50693A4E24FB1AFCFC6EB076400FF
    */
    private final String[] beaconType = {"LJ-SS-EEA-01", "LJ-SS-EEA-02", "LJ-SS-EEA-03", "LJ-SS-EEA-04", "LJ-SS-EEA-05", "疑似废弃信标"};
    private final String[] beaconDesc = {"工作面大信标", "语音信标", "雷达信标", "巡检信标", "辅助定位信标", " "};

    private final String[] safetyHatType = {"HELMET", "LJHMAA", "LJHMBA"};
    private final String[] safetyHatDesc = {"一代安全帽", "简版安全帽", "全功能安全帽"};

    private final String[] curType;
    private final String[] curDesc;

    private final boolean isBeacon;

    FragmentActivity context;
    ViewPager viewPager;

    public BleBeaconTypeAdapter(FragmentActivity context, ViewPager viewPager, boolean isBeacon) {
        this.context = context;
        this.viewPager = viewPager;
        this.isBeacon = isBeacon;

        if(isBeacon){
            curType = beaconType;
            curDesc = beaconDesc;
        }else{
            curType = safetyHatType;
            curDesc = safetyHatDesc;
        }
    }


    @NonNull
    @Override
    public BeaconTypeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.beacon_type_item, viewGroup, false);

        return new BleBeaconTypeAdapter.BeaconTypeViewHolder(view, viewPager);
    }

    @Override
    public void onBindViewHolder(@NonNull BeaconTypeViewHolder beaconTypeViewHolder, int i) {
        beaconTypeViewHolder.setBeaconType(i);
        beaconTypeViewHolder.bind(curType[i] + " " + curDesc[i]);
    }

    @Override
    public int getItemCount() {
        return curType.length;
    }

    class BeaconTypeViewHolder extends RecyclerView.ViewHolder {
        ImageView beaconTypeImageView;
        TextView beaconTypeTextView;
        View itemView;
        int beaconType;
        ViewPager viewPager;

        public int getBeaconType() {
            return beaconType;
        }

        public void setBeaconType(int beaconType) {
            this.beaconType = beaconType;
        }

        public BeaconTypeViewHolder(@NonNull final View itemView, ViewPager viewPager) {
            super(itemView);
            this.itemView = itemView;
            this.viewPager = viewPager;
            beaconTypeImageView = itemView.findViewById(R.id.beaconTypeImg);
            beaconTypeTextView = itemView.findViewById(R.id.beaconTypeText);

        }


        public void bind(String typeName) {
            beaconTypeTextView.setText(typeName);
            beaconTypeImageView.setImageDrawable(context.getDrawable(R.drawable.bluetoothon));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(beaconType + 1);
                }
            });
        }
    }
}
