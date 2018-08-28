package com.hongbog.tensorflow;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.hongbog.dto.ResultProbList;

public abstract class TensorFlowClassifierContract {

    /**
     * 텐서플로우 classifier 초기화 함수
     */
    protected abstract void initTensorFlowAndLoadModel(AssetManager assetManager);

    /**
     * 텐서플로우 classifier 생성 관련 초기화 함수
     * @param assetManager
     * @param modelFilename
     * @param labelFilename
     * @param widths
     * @param heights
     * @param rightInputNames
     * @param leftInputNames
     * @param outputNames
     */
    protected abstract void createClassifier(
            AssetManager assetManager,
            String modelFilename,
            String labelFilename,
            int[] widths,
            int[] heights,
            String[] rightInputNames,
            String[] leftInputNames,
            String[] outputNames);


    /**
     * 오른쪽/왼쪽 눈에 대한 verification 을 수행하는 함수
     * @return
     */
    protected abstract float[] verificationEye();


    protected abstract ResultProbList Verification(Bundle bundle);


    protected abstract float[] grayScaleAndNorm(Bitmap bitmap);


    /**
     * 추후 신경망 모델 업데이트시 Equlization 추가
     * @param bitmap
     * @return
     */
    protected abstract float[] grayScale_Equalization_Norm(Bitmap bitmap);
}