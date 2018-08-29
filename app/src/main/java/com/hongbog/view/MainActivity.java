package com.hongbog.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.hongbog.util.Dlog;
import com.tzutalin.quality.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    public static final String ACTIVITY_FLOW_EXTRA = "ACTIVITY_FLOW_EXTRA";
    public static final String VERIFY_EXTRA = "VERIFY_EXTRA";
    public static final String ENROLL_EXTRA = "ENROLL_EXTRA";
    public static final String DEVELOP_MODE_EXTRA = "DEVELOP_MODE_EXTRA";


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startLoadingActivity();

        initView();
    }


    private void startLoadingActivity() {
        startActivity(new Intent(this, LoadingActivity.class));
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initView(){
        ImageButton verifyBtn = (ImageButton)findViewById(R.id.verify_btn);
        ImageButton enrollBtn = (ImageButton)findViewById(R.id.enroll_btn);

        verifyBtn.setOnClickListener(this);
        enrollBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        Intent intent = new Intent();

        switch (id){
            case R.id.verify_btn:
                Dlog.d("verify_btn");
                intent.setClass(this, CameraActivity.class);
                intent.putExtra(ACTIVITY_FLOW_EXTRA, VERIFY_EXTRA);
                break;
            case R.id.enroll_btn:
                Dlog.d("enroll_btn");
//                intent.setClass(this, InfoActivity.class);
                intent.setClass(this, CameraActivity.class);
                intent.putExtra(ACTIVITY_FLOW_EXTRA, ENROLL_EXTRA);
                break;
        }

        Dlog.d("onClick");
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_list, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedId = item.getItemId();
        Dlog.d("onOptionsItemSelected");
        Intent intent = new Intent();

        switch (selectedId){
            case R.id.develop_mode :
                intent.setClass(this, CameraActivity.class);
                intent.putExtra(ACTIVITY_FLOW_EXTRA, DEVELOP_MODE_EXTRA);
                startActivity(intent);
                break;
            case R.id.setting_mode:
                intent.setClass(this, SettingActivity.class);
//                intent.putExtra(ACTIVITY_FLOW_EXTRA, DEVELOP_MODE_EXTRA);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
//        faceDetJNIdeInitialize();
        super.onBackPressed();
    }


    /*@Override
    protected void onDestroy() {
        faceDetJNIdeInitialize();
        super.onDestroy();
    }


    private void faceDetJNIdeInitialize() {
        Dlog.d("deInitialize");
        FaceDet faceDet = FaceDet.getInstance();

        if (faceDet != null) {
            Dlog.d("mFaceDet.release()");
            faceDet.release();
        }
    }*/
}