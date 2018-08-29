package com.hongbog.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hongbog.util.Dlog;
import com.hongbog.util.LabelSharedPreference;
import com.tzutalin.quality.R;
import com.hongbog.dto.ResultProb;
import com.hongbog.dto.ResultProbList;
import com.hongbog.tensorflow.TensorFlowClassifier;
import com.victor.loading.rotate.RotateLoading;

import java.util.concurrent.ExecutionException;

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
    private LabelSharedPreference labelSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        initView();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        ResultProbList resultProbList = null;

        try {
            resultProbList = new ClassficationAsyncTask().execute(bundle).get();
        } catch (Exception e) {
            Dlog.e("Exception Message : " + e.getMessage());
            finish();
        }

        int label = getLabelFromLogit(resultProbList);
        String mode = intent.getStringExtra(ACTIVITY_FLOW_EXTRA);

        if (mode != null && !"".equals(mode)) {

            labelSharedPref = new LabelSharedPreference(this);

            if(mode != null && VERIFY_EXTRA.equals(mode)){

                int enrolledLabel = labelSharedPref.getInt();
                Dlog.d("VERIFY_EXTRA enrolledLabel : " +  enrolledLabel);
                Dlog.d("VERIFY_EXTRA label : " +  label);

                if (enrolledLabel == label) {
                    setIdentifySuccess();
                }else{
                    setIdentifyFail();
                }

            }else if(mode != null && ENROLL_EXTRA.equals(mode)){

                Dlog.d("ENROLL_EXTRA label : " + label);
                labelSharedPref.putInt(label);
                finish();

            }

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

        rotateLoading.start();
        loadingLayout.setVisibility(View.VISIBLE);
    }


    public void stopLoadingAnimation() {

        Dlog.d("stopLoadingAnimation");

        loadingLayout.setVisibility(View.GONE);
        rotateLoading.stop();
    }


    private class ClassficationAsyncTask extends AsyncTask<Bundle, Void, ResultProbList> {

        @Override
        protected void onPreExecute() {
            startLoadingAnimation();
        }

        @Override
        protected ResultProbList doInBackground(Bundle... bundles) {
            classifier = TensorFlowClassifier.getInstance();
            return classifier.Verification(bundles[0]);
        }

        @Override
        protected void onPostExecute(ResultProbList resultProbs) {
            stopLoadingAnimation();
        }
    }


    private void initView(){
        textView = (TextView)findViewById(R.id.label_textview);
        checkIdentifyImageView = (ImageView)findViewById(R.id.check_identify_image);
        loadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);
        rotateLoading = (RotateLoading) findViewById(R.id.rotateloading);
        SUCCESS_TEXT = this.getString(R.string.verification_success);
        FAIL_TEXT = this.getString(R.string.verification_fail);
    }


   /* @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }*/


    private int getLabelFromLogit(ResultProbList resultProbList){

        if(resultProbList == null) return LabelSharedPreference.PreferenceConstant.GARBAGE_VALUE;

        for (ResultProb resultProb : resultProbList) {

            Bitmap leftBitmap = resultProb.getLeftBitmap();
            Bitmap rightBitmap = resultProb.getRightBitmap();

            Bitmap tmpLeftBitmap = Bitmap.createScaledBitmap(leftBitmap, WIDTHS[0], HEIGHTS[0], false);
            Bitmap tmpRightBitmap = Bitmap.createScaledBitmap(rightBitmap, WIDTHS[0], HEIGHTS[0], false);

            if(tmpLeftBitmap == null || tmpRightBitmap == null) return LabelSharedPreference.PreferenceConstant.GARBAGE_VALUE;

            float[] tempResult = resultProb.getProbResult();

            // 확률이 가장 큰 클래스 고르기
            float maxValue = LabelSharedPreference.PreferenceConstant.GARBAGE_VALUE;
            int result = LabelSharedPreference.PreferenceConstant.GARBAGE_VALUE;

            for (int i = 0; i < tempResult.length; i++) {
                if (maxValue < tempResult[i]) {
                    maxValue = tempResult[i];
                    result = i;
                }
            }
            return result;
        }

        return LabelSharedPreference.PreferenceConstant.GARBAGE_VALUE;
    }
}