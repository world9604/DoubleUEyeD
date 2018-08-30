package com.hongbog.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import com.hongbog.util.Dlog;
import com.hongbog.util.PreferenceUtil;
import com.tzutalin.quality.R;
import static com.hongbog.view.ResultActivity.LABEL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private String NOT_ENROLLED_USER_TEXT;
    private String DELETE_ENROLLED_DATA_TEXT;
    private String SUCCESS_ENROLL_TEXT;
    private ImageButton verifyBtn;
    private ImageButton enrollBtn;

    interface ActivityConst{
        String ACTIVITY_FLOW_EXTRA = "ACTIVITY_FLOW_EXTRA";
        String VERIFY_EXTRA = "VERIFY_EXTRA";
        String ENROLL_EXTRA = "ENROLL_EXTRA";
        String DEVELOP_MODE_EXTRA = "DEVELOP_MODE_EXTRA";
        int OVERLAY_PERMISSION_REQ_CODE = 1;
        int REQUEST_CODE = 2;
        int SUCCESS_ENROLL_RESULT_CODE = 99;
    }

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
        verifyBtn = (ImageButton)findViewById(R.id.verify_btn);
        enrollBtn = (ImageButton)findViewById(R.id.enroll_btn);

        verifyBtn.setOnClickListener(this);
        enrollBtn.setOnClickListener(this);

        NOT_ENROLLED_USER_TEXT = this.getString(R.string.not_enrolled_user);
        DELETE_ENROLLED_DATA_TEXT = this.getString(R.string.delete_enrolled_data);
        SUCCESS_ENROLL_TEXT = this.getString(R.string.success_enroll);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        Intent intent = new Intent();

        switch (id){
            case R.id.verify_btn:
                Dlog.d("verify_btn");

                intent.setClass(this, CameraActivity.class);
                intent.putExtra(ActivityConst.ACTIVITY_FLOW_EXTRA, ActivityConst.VERIFY_EXTRA);

                boolean isEnroll = PreferenceUtil.getInstance(this).contains();

                Dlog.d("isEnroll : " + isEnroll );

                if(isEnroll == false){
                    Snackbar.make(v, NOT_ENROLLED_USER_TEXT, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                break;
            case R.id.enroll_btn:
                Dlog.d("enroll_btn");
                intent.setClass(this, CameraActivity.class);
                intent.putExtra(ActivityConst.ACTIVITY_FLOW_EXTRA, ActivityConst.ENROLL_EXTRA);
                break;
        }
        startActivityForResult(intent, ActivityConst.REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        Dlog.d("requestCode : " + requestCode);
        Dlog.d("resultCode : " + resultCode);

        if(requestCode == ActivityConst.REQUEST_CODE){
            if(resultCode == ActivityConst.SUCCESS_ENROLL_RESULT_CODE){
                String label = intent.getStringExtra(LABEL);
                Snackbar.make(verifyBtn, SUCCESS_ENROLL_TEXT
                        + " (" + label + ")", Snackbar.LENGTH_SHORT).show();
            }
        }
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
            /*case R.id.develop_mode :
                intent.setClass(this, CameraActivity.class);
                intent.putExtra(ACTIVITY_FLOW_EXTRA, DEVELOP_MODE_EXTRA);
                startActivity(intent);
                break;
            case R.id.delete_enrolled_data:
                PreferenceUtil.getInstance(this).clear();
                Snackbar.make(verifyBtn, DELETE_ENROLLED_DATA_TEXT, Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.setting_mode:
                intent.setClass(this, SettingActivity.class);
//                intent.putExtra(ACTIVITY_FLOW_EXTRA, DEVELOP_MODE_EXTRA);
                startActivity(intent);
                break;*/
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