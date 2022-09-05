package com.acfm.ble_transform;

import android.os.CountDownTimer;
import android.widget.TextView;

public class MyCountDownTimer extends CountDownTimer{
    private TextView tv;
    private long time; //[0~time]

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public MyCountDownTimer(TextView tv, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.tv = tv;
        time = millisInFuture/1000;
    }


    @Override
    public void onTick(long millisUntilFinished) {
        tv.setText((--time) + "s");
    }

    @Override
    public void onFinish() {
        tv.setText("20s");
    }
}
