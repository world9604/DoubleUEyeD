/*
 * Copyright 2016-present Tzutalin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hongbog.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hongbog.util.Dlog;
import com.hongbog.dto.ParcelBitmap;
import com.tzutalin.quality.R;
import com.victor.loading.rotate.RotateLoading;
import com.hongbog.view.MainActivity.ActivityConst;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_FORWARD_RESULT;


/**
 * Created by darrenl on 2016/5/20.
 */
public class CameraActivity extends Activity {

    private long startTime;
    private RelativeLayout loadingLayout;
    private RotateLoading rotateLoading;
    private FrameLayout gforceFrameLayout;

    //Using the Accelometer & Gyroscoper
    private SensorManager mSensorManager;
    private SensorEventListener mSensorLis;
    private Sensor mGgyroSensor;
    private Sensor mLightSensor;
    private HandlerThread sensorThread;
    private Handler sensorHandler;


    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        Dlog.d("onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_camera);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this.getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ActivityConst.OVERLAY_PERMISSION_REQ_CODE);
            }
        }

        startTime = System.currentTimeMillis();

        mSensorLis = new SensorListener();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGgyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorLis = new SensorListener();

        if (null == savedInstanceState) {
            getFragmentManager()
                .beginTransaction()
                .replace(R.id.detect_container, CameraConnectionFragment.newInstance())
                .replace(R.id.gforce_container, GforceFragment.newInstance())
                .commit();
        }

        loadingLayout = findViewById(R.id.loading_layout);
        rotateLoading = findViewById(R.id.rotateloading);
        gforceFrameLayout = findViewById(R.id.gforce_container);

        startLoadingAnimation();
    }


    public void goActivityWithBitmapPathBundle(Bundle bitmapPathsBundle) {

        if (bitmapPathsBundle == null){
            Dlog.e("bitmapPathsBundle is Null, So Finish this Activity");
            finish();
            return;
        }

        // 리스트에 이미지 넣기
        Intent intent = getIntent();
        String mode = intent.getStringExtra(ActivityConst.ACTIVITY_FLOW_EXTRA);

        if(mode == null) {
            Dlog.e("intent.getStringExtra(ACTIVITY_FLOW_EXTRA) is Null");
            finish();
            return;
        }

        if (ActivityConst.DEVELOP_MODE_EXTRA.equals(mode)) {
            Dlog.d("CameraActivity ACTIVITY_FLOW_EXTRA : " + mode);
            intent.setClass(this, ResultTestActivity.class);
        }else if(ActivityConst.VERIFY_EXTRA.equals(mode)){
            Dlog.d("CameraActivity ACTIVITY_FLOW_EXTRA : " + mode);
            intent.setClass(this, ResultActivity.class);
        }else if(ActivityConst.ENROLL_EXTRA.equals(mode)){
            Dlog.d("CameraActivity ACTIVITY_FLOW_EXTRA : " + mode);
            intent.setClass(this, ResultActivity.class);
            intent.setFlags(FLAG_ACTIVITY_FORWARD_RESULT);
        }else{
            return;
        }

        intent.putExtras(bitmapPathsBundle);

        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        Dlog.d("onStart");
        super.onStart();
    }


    @Override
    protected void onStop() {
        Dlog.d("onStop");
        super.onStop();
    }


    @Override
    protected void onResume() {
        Dlog.d("onResume");
        registerSensorManager();
        super.onResume();
    }


    @Override
    protected void onPause() {
        Dlog.d("onPause");
        unregisterSensorManager();
        super.onPause();
    }


    private void registerSensorManager(){
        sensorThread = new HandlerThread("SensorThread");
        sensorThread.start();
        sensorHandler = new Handler(sensorThread.getLooper());

        mSensorManager.registerListener(mSensorLis, mGgyroSensor, SensorManager.SENSOR_DELAY_UI, sensorHandler);
        mSensorManager.registerListener(mSensorLis, mLightSensor, SensorManager.SENSOR_DELAY_UI, sensorHandler);
    }


    private void unregisterSensorManager(){
        sensorThread.quitSafely();

        try {
            sensorThread.join();
            sensorThread = null;
            sensorHandler = null;
        } catch (InterruptedException e) {
            Dlog.d("error : " + e.getMessage());
        }

        mSensorManager.unregisterListener(mSensorLis, mGgyroSensor);
        mSensorManager.unregisterListener(mSensorLis, mLightSensor);
    }


    @Override
    protected void onDestroy() {
        Dlog.d("onDestroy");
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ActivityConst.OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this.getApplicationContext())) {
                    Dlog.d("CameraActivity onActivityResult");
                    Toast.makeText(CameraActivity.this, "CameraActivity\", \"SYSTEM_ALERT_WINDOW, permission not granted...", Toast.LENGTH_SHORT).show();
                } else {
                    Dlog.d("Settings.canDrawOverlays!!!");
                    //Toast.makeText(CameraActivity.this, "Restart CameraActivity", Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    public void startLoadingAnimation() {
        Dlog.d("startLoadingAnimation");

        gforceFrameLayout.setVisibility(View.GONE);
        rotateLoading.start();
        loadingLayout.setVisibility(View.VISIBLE);
    }

    public void stopLoadingAnimation() {
        Dlog.d("stopLoadingAnimation");

        loadingLayout.setVisibility(View.GONE);
        rotateLoading.stop();
        gforceFrameLayout.setVisibility(View.VISIBLE);
    }
}