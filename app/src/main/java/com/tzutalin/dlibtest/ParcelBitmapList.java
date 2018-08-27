package com.tzutalin.dlibtest;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by jslee on 2018-07-17.
 */

public class ParcelBitmapList extends ArrayList<ParcelBitmap> {


    @Override
    public boolean add(ParcelBitmap object) {
        return super.add(object);
    }


    public boolean add(Bitmap object) {
        ParcelBitmap addData = new ParcelBitmap( object );
        return super.add(addData);
    }


    @Override
    public ParcelBitmap get(int index) {
        return super.get(index);
    }


    public Bitmap getBitmap(int index) {
        ParcelBitmap getData = super.get(index);
        return getData.getBitmap();
    }
}