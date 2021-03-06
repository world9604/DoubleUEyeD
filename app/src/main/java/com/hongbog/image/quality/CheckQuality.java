package com.hongbog.image.quality;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import com.hongbog.image.quality.EAR;
import com.hongbog.image.quality.Glace;
import com.hongbog.image.quality.VoL;
import com.tzutalin.dlib.VisionDetRet;
import com.tzutalin.quality.R;

import java.util.ArrayList;

import static java.lang.Math.abs;
import static org.opencv.core.Core.compare;

/***
 * Created by jslee on 2018-07-19.
 * */

public class CheckQuality {

    private static final String TAG = "CheckQuality";

    private static final int THRESHOLD_OF_BLURRY_IMAGE = 10;
    private static final double THRESHOLD_OF_EAR = 0.23;
    private static final int THRESHOLD_OF_SIDE_GLANCE = 25;
    private static final int THRESHOLE_OF_TILT = 15;


    private String STATE_TEXT_CHECK_NEARNESS;
    private String STATE_TEXT_CHECK_FARNESS;
    private String STATE_TEXT_CHECK_BLURRY;
    private String STATE_TEXT_CHECK_EAR;
    private String STATE_TEXT_CHECK_GLANCE;
    private String STATE_TEXT_CHECK_TILT;

    private VisionDetRet mRet;
    private Bitmap mBitmap;
    private Bitmap mbitCrop_eye;
    private Bitmap mbitCrop_L;
    private Bitmap mbitCrop_R;

    public int mBlur_all;
    public int mBlur_eye;
    public int mBlur_R;
    public int mBlur_L;

    public double mEar;
    public double mRotat;

    private boolean Scope = true;
    private boolean blur = true;
    private boolean ear = true;
    private boolean glace = true;
    private boolean rotate = true;

    private Context mContext;



    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("opencv_java3");
    }

    public CheckQuality(VisionDetRet ret, Bitmap bitmap,  Bitmap bitCrop_eye, Bitmap bitCrop_L, Bitmap bitCrop_R, Context context ) {
        super();

        this.mRet = ret;
        this.mBitmap = bitmap;
        this.mbitCrop_eye = bitCrop_eye;
        this.mbitCrop_L = bitCrop_L;
        this.mbitCrop_R = bitCrop_R;

        mContext = context ;
        STATE_TEXT_CHECK_NEARNESS = mContext.getString(R.string.state_test_check_nearness);
        STATE_TEXT_CHECK_FARNESS = mContext.getString(R.string.state_test_check_farness);
        STATE_TEXT_CHECK_BLURRY = mContext.getString(R.string.state_test_check_blurry);
        STATE_TEXT_CHECK_EAR = mContext.getString(R.string.state_test_check_ear);
        STATE_TEXT_CHECK_GLANCE = mContext.getString(R.string.state_test_check_glance);
        STATE_TEXT_CHECK_TILT = mContext.getString(R.string.state_test_check_tilt);

    }


    public String acceptScope() {

        String string = "";

        this.Scope = false;
        // 얼굴 크기와 눈 크기가 일정 범위 내에 있는지 체크
        Rect bounds = new Rect();

        bounds.left = mRet.getLeft();
        bounds.top = mRet.getTop();
        bounds.right = mRet.getRight();
        bounds.bottom = mRet.getBottom();
        int faceWidth = (bounds.right - bounds.left);
        //Log.i(TAG,"Face width:" + String.valueOf(faceWidth));

        int width_L = mRet.mStartLeftX + mRet.mWidthLeft;
        int width_R = mRet.mStartRightX + mRet.mWidthRight;
        int height_L = mRet.mStartLeftY + mRet.mHightLeft;
        int height_R = mRet.mStartRightY + mRet.mHightRight;
        width_L = mRet.getWidthLeft();
        width_R = mRet.getWidthRight();
        height_L = mRet.getHightLeft();
        height_R = mRet.getHightRight();


        // 얼굴 크기가 너무 작거나 큰것 제외시킨다.
        //if ((faceWidth > 200) && (faceWidth < 450)) {
        // 눈의 좌표가 이미지 사이즈 넘어가지 않도록 한다.
        if ((0 < width_L) && (width_L < mBitmap.getWidth())) {
            if ((0 < width_R) && (width_R < mBitmap.getWidth())) {

                if ((0 < height_L) && (height_L < mBitmap.getHeight())) {
                    if ((0 < height_R) && (height_R < mBitmap.getHeight())) {

                        // 눈 크기가 너무 작은것 제외시킨다.
                        if ((100 < width_L) && (100 < width_R)) {
                            // 눈 크기가 너무 작은것 제외시킨다.
                            if ((200 > width_L) && (200 > width_R)) {
                                this.Scope = true;
                            }
                            else {
                                string = STATE_TEXT_CHECK_NEARNESS;;
                                Log.i(TAG, "Left size(" + String.valueOf(width_L) + ", " + String.valueOf(height_L) + ")" + " / Right size(" + String.valueOf(width_R) + ", " + String.valueOf(height_R) + ")");
                            }

                        }
                        else{
                            string = STATE_TEXT_CHECK_FARNESS;;
                            Log.i(TAG, "Left size(" + String.valueOf(width_L) + ", " + String.valueOf(height_L) + ")" + " / Right size(" + String.valueOf(width_R) + ", " + String.valueOf(height_R) + ")");
                        }
                    }
                    else{
                        string = STATE_TEXT_CHECK_NEARNESS;;
                        Log.i(TAG, "Left size(" + String.valueOf(width_L) + ", " + String.valueOf(height_L) + ")" + " / Right size(" + String.valueOf(width_R) + ", " + String.valueOf(height_R) + ")");
                    }


                }
            }
        }
        //}
        return string;
    }


    public String  acceptBlur() {

        this.blur = false;
        String string = "";


        // 이미지가 스무딩 되었는지 체크
        VoL blur = new VoL();

        //this.mBlur_all = blur.calculateBlur(this.mBitmap);
        //Log.i(TAG, "Blur all:" + String.valueOf(this.mBlur_all));

        this.mBlur_eye = blur.calculateBlur(this.mbitCrop_eye);
        // Log.i(TAG, "Blur all:" + String.valueOf(this.mBlur_eye));

        this.mBlur_L = blur.calculateBlur(this.mbitCrop_L);
        this.mBlur_R = blur.calculateBlur(this.mbitCrop_R);

        Log.i(TAG, "Blur Left:" + String.valueOf(this.mBlur_L) + " / Blur Right:" + String.valueOf(this.mBlur_R));
        if (mBlur_L > THRESHOLD_OF_BLURRY_IMAGE && mBlur_R > THRESHOLD_OF_BLURRY_IMAGE) {
            this.blur = true;
        }
        else{
            string = STATE_TEXT_CHECK_BLURRY;;
            Log.i(TAG, "Blur Left:" + String.valueOf(this.mBlur_L) + " / Blur Right:" + String.valueOf(this.mBlur_R));
        }

        return string;

    }



    public String acceptEar(){

        this.ear = false;
        String string = "";

        // 눈이 감겼는제 체크
        EAR myEar = new EAR(this.mRet);

        myEar.calculateEarLeft();
        myEar.calculateEarRight();
        // Log.i(TAG,String.format("EAR: left(%f) /  right(%f)", myEar.getLeft_ear(), myEar.getRight_ear()));


        if((myEar.getLeft_ear() > THRESHOLD_OF_EAR) && (myEar.getRight_ear() > THRESHOLD_OF_EAR)){
            this.ear = true;
        }
        else{
            string = STATE_TEXT_CHECK_EAR;;
            Log.i(TAG,String.format("EAR: left(%f) /  right(%f)", myEar.getLeft_ear(), myEar.getRight_ear()));
        }
        return string;

    }

    public String acceptGlace(){

        this.glace = false;
        String string = "";

        // 옆으로 흘겨보는지 체크
        Glace glace = new Glace(this.mRet, this.mBitmap);

        int diff = glace.calculateSideGlace();
        //Log.i(TAG, String.format("Difference of side of face : %d)", diff));


        if(diff < THRESHOLD_OF_SIDE_GLANCE){
            this.glace = true;
        }
        else{

            string = STATE_TEXT_CHECK_GLANCE;;
            Log.i(TAG, String.format("Difference of side of face : %d)", diff));
        }
        return string;

    }

    public String acceptTilt(){

        this.rotate = false;
        String string = "";

        // 고개를 기울였는지 체크
        ArrayList<Point> landmark = mRet.getFaceLandmarks();

        int rotat = abs(landmark.get(36).y - landmark.get(45).y);
        Log.i(TAG, String.format("Rotation of face : %d)", rotat));


        if (rotat < THRESHOLE_OF_TILT){
            this.rotate = true;
        }
        else{
            string = STATE_TEXT_CHECK_TILT;;
            Log.i(TAG, String.format("Rotation of face : %d)", rotat));
        }
        return string;


    }



    public boolean isAccept() {
        boolean mAccept = false;

        if ((this.Scope == true) && (this.blur == true) && (this.ear == true) && (this.glace == true) && (this.rotate == true)) {
            mAccept = true;
        }


        return mAccept;

    }
}