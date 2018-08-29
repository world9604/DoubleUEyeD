package com.hongbog.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.hongbog.util.Dlog;
import com.hongbog.util.LabelSharedPreference;
import com.tzutalin.quality.R;

import static com.hongbog.util.LabelSharedPreference.PreferenceConstant.PREF_KEY;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    public static final String ACTIVITY_FLOW_EXTRA = "ACTIVITY_FLOW_EXTRA";
    public static final String VERIFY_EXTRA = "VERIFY_EXTRA";
    public static final String ENROLL_EXTRA = "ENROLL_EXTRA";
    public static final String DEVELOP_MODE_EXTRA = "DEVELOP_MODE_EXTRA";
    private String NOT_ENROLLED_USER_TEXT;
    private String DELETE_ENROLLED_DATA_TEXT;
    private LabelSharedPreference labelSharedPreference;
    private ImageButton verifyBtn;
    private ImageButton enrollBtn;

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
        labelSharedPreference = new LabelSharedPreference(this);
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

                boolean isEnroll = labelSharedPreference.contains();
                Dlog.d("isEnroll : " + isEnroll );

                if(isEnroll == false){
                    Snackbar.make(v, NOT_ENROLLED_USER_TEXT, Snackbar.LENGTH_LONG).show();
                    return;
                }

                break;
            case R.id.enroll_btn:
                Dlog.d("enroll_btn");
                intent.setClass(this, CameraActivity.class);
                intent.putExtra(ACTIVITY_FLOW_EXTRA, ENROLL_EXTRA);
                break;
        }
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
            case R.id.delete_enrolled_data:
                labelSharedPreference.clear();
                Snackbar.make(verifyBtn, DELETE_ENROLLED_DATA_TEXT, Snackbar.LENGTH_LONG).show();
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