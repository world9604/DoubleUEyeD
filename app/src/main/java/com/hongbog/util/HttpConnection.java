package com.hongbog.util;

import com.hongbog.dto.SensorDTO;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by taein on 2018-07-06.
 */
public class HttpConnection {

    private static final String ADDRESS = "http://192.168.0.2:8080";
    private static final String MAPPING_NAME = "/uploadData.do";
    private final String URL = ADDRESS + MAPPING_NAME;

    private OkHttpClient client;
    private static HttpConnection instance = new HttpConnection();
    public static HttpConnection getInstance() {
        return instance;
    }

    private HttpConnection(){ this.client = new OkHttpClient(); }

    public String requestUploadPhoto(byte[] BitmapBytes, String label, SensorDTO sensorVales, Callback callback) {
        try {
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("photo", label + ".png", RequestBody.create(MediaType.parse("image/png"), BitmapBytes))
                    .addFormDataPart("label", label)
                    .addFormDataPart("roll", sensorVales.getRoll())
                    .addFormDataPart("pitch", sensorVales.getPitch())
                    .addFormDataPart("yaw", sensorVales.getYaw())
                    .addFormDataPart("br", sensorVales.getBr())
                    .build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(URL)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(callback);

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }

}
