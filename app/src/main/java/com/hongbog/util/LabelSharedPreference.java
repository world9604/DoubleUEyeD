package com.hongbog.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by taein on 2018-08-29.
 */

public class LabelSharedPreference {

    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public interface PreferenceConstant{
        String PREF_NAME = "LABEL_PREF";
        String PREF_KEY = "enrolledLabel";
        int DEF_INT_VALUE = -99999;
        int GARBAGE_VALUE = -1;
    }

    public LabelSharedPreference(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PreferenceConstant.PREF_NAME, context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public int getInt(String KeyName){
        return preferences.getInt(KeyName, PreferenceConstant.DEF_INT_VALUE);
    }

    public boolean putInt(int value){
        editor.putInt(PreferenceConstant.PREF_KEY, value);
        return editor.commit();
    }

    public boolean removeKey(String keyName){
        editor.remove(keyName);
        return editor.commit();
    }

    public boolean clear(){
        editor.clear();
        return editor.commit();
    }
}
