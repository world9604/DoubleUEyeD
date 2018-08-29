package com.hongbog.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hongbog.util.Dlog;
import com.hongbog.util.PreferenceUtil;
import com.tzutalin.dlibtest.ImageUtils;
import com.tzutalin.quality.R;
import com.hongbog.dto.ResultProb;
import com.hongbog.dto.ResultProbList;
import com.hongbog.tensorflow.TensorFlowClassifier;
import com.victor.loading.rotate.RotateLoading;
import java.util.ArrayList;

import static com.hongbog.tensorflow.TensorFlowClassifier.HEIGHTS;
import static com.hongbog.tensorflow.TensorFlowClassifier.WIDTHS;
import static com.hongbog.view.MainActivity.ACTIVITY_FLOW_EXTRA;
import static com.hongbog.view.MainActivity.ENROLL_EXTRA;
import static com.hongbog.view.MainActivity.VERIFY_EXTRA;

public class ResultActivity extends AppCompatActivity {

    private TextView textView;
    private ImageView checkIdentifyImageView;
    private TensorFlowClassifier classifier;
    private RelativeLayout loadingLayout;
    private RotateLoading rotateLoading;
    private String SUCCESS_TEXT;
    private String FAIL_TEXT;
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        initView();

        Intent intent = getIntent();
        Bundle bitmapPathbundle = intent.getExtras();

        try {
            new ClassficationAsyncTask(this).execute(bitmapPathbundle);
        } catch (Exception e) {
            Dlog.e("Exception Message : " + e.getMessage());
            finish();
        }
    }


    private void setIdentifySuccess(){
        textView.setText(SUCCESS_TEXT);
        checkIdentifyImageView.setImageResource(R.drawable.identify_success);
    }


    private void setIdentifyFail(){
        textView.setText(FAIL_TEXT);
        checkIdentifyImageView.setImageResource(R.drawable.identify_fail);
    }


    public void startLoadingAnimation() {

        Dlog.d("startLoadingAnimation");
        actionBar.hide();
        rotateLoading.start();
        loadingLayout.setVisibility(View.VISIBLE);
    }


    public void stopLoadingAnimation() {

        Dlog.d("stopLoadingAnimation");
        actionBar.show();
        loadingLayout.setVisibility(View.GONE);
        rotateLoading.stop();
    }


    private class ClassficationAsyncTask extends AsyncTask<Bundle, Void, Integer> {

        private Context mContext;

        public ClassficationAsyncTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected void onPreExecute() {
            startLoadingAnimation();
        }

        @Override
        protected Integer doInBackground(Bundle... bundles) {

            String[] bitmap_left = bundles[0].getStringArray("bitmap_left");
            String[] bitmap_right = bundles[0].getStringArray("bitmap_right");

            Bundle bundle = getBundleByPath(bitmap_left, bitmap_right);

            classifier = TensorFlowClassifier.getInstance();

            ResultProbList resultProbList = classifier.Verification(bundle);

            return getLabelFromLogit(resultProbList);
        }

        @Override
        protected void onPostExecute(Integer label) {

            String mode = getIntent().getStringExtra(ACTIVITY_FLOW_EXTRA);

            if (mode != null && !"".equals(mode)) {

                if(VERIFY_EXTRA.equals(mode)){

                    int enrolledLabel = PreferenceUtil.getInstance(mContext).getIntExtra();
                    Dlog.d("VERIFY_EXTRA enrolledLabel : " +  enrolledLabel);
                    Dlog.d("VERIFY_EXTRA label : " +  label);

                    if (enrolledLabel == label) {
                        setIdentifySuccess();
                    }else{
                        setIdentifyFail();
                    }

                }else if(ENROLL_EXTRA.equals(mode)){

                    Dlog.d("ENROLL_EXTRA label : " + label);
                    PreferenceUtil.getInstance(mContext).putIntExtra(label);
                    finish();
                    return;
                }
            }

            stopLoadingAnimation();
        }
    }


    private Bundle getBundleByPath(String[] bitmapLeftPath, String[] bitmapRightPath){

        if(bitmapLeftPath == null || bitmapRightPath == null) return null;

        Bundle parcelBitmapBundle = new Bundle();
        ArrayList<Bitmap> leftParcBitmapList = new ArrayList<>();
        ArrayList<Bitmap> rightParcBitmapList = new ArrayList<>();

        for(String path : bitmapLeftPath){
            Bitmap bitmap = ImageUtils.extractBitmapFromDirName(path);
            leftParcBitmapList.add(bitmap);
            ImageUtils.deleteBitmapFromFileName(path);
        }

        for(String path : bitmapRightPath){
            Bitmap bitmap = ImageUtils.extractBitmapFromDirName(path);
            rightParcBitmapList.add(bitmap);
            ImageUtils.deleteBitmapFromFileName(path);
        }

        parcelBitmapBundle.putParcelableArrayList("LeftEyeList", leftParcBitmapList);
        parcelBitmapBundle.putParcelableArrayList("RightEyeList", rightParcBitmapList);

        return parcelBitmapBundle;
    }



    private void initView(){
        actionBar = getSupportActionBar();
        textView = (TextView)findViewById(R.id.label_textview);
        checkIdentifyImageView = (ImageView)findViewById(R.id.check_identify_image);
        loadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);
        rotateLoading = (RotateLoading) findViewById(R.id.rotateloading);
        SUCCESS_TEXT = this.getString(R.string.verification_success);
        FAIL_TEXT = this.getString(R.string.verification_fail);
    }


    private int getLabelFromLogit(ResultProbList resultProbList){

        if(resultProbList == null) return PreferenceUtil.PreferenceConstant.GARBAGE_VALUE;

        for (ResultProb resultProb : resultProbList) {

            Bitmap leftBitmap = resultProb.getLeftBitmap();
            Bitmap rightBitmap = resultProb.getRightBitmap();

            Bitmap tmpLeftBitmap = Bitmap.createScaledBitmap(leftBitmap, WIDTHS[0], HEIGHTS[0], false);
            Bitmap tmpRightBitmap = Bitmap.createScaledBitmap(rightBitmap, WIDTHS[0], HEIGHTS[0], false);

            if(tmpLeftBitmap == null || tmpRightBitmap == null) return PreferenceUtil.PreferenceConstant.GARBAGE_VALUE;

            float[] tempResult = resultProb.getProbResult();

            // 확률이 가장 큰 클래스 고르기
            float maxValue = PreferenceUtil.PreferenceConstant.GARBAGE_VALUE;
            int result = PreferenceUtil.PreferenceConstant.GARBAGE_VALUE;

            for (int i = 0; i < tempResult.length; i++) {
                if (maxValue < tempResult[i]) {
                    maxValue = tempResult[i];
                    result = i;
                }
            }
            return result;
        }

        return PreferenceUtil.PreferenceConstant.GARBAGE_VALUE;
    }
}