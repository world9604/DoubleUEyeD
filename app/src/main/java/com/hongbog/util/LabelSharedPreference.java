package com.hongbog.util;

import android.content.Context;
import android.content.SharedPreferences;

import static com.hongbog.util.LabelSharedPreference.PreferenceConstant.DEF_INT_VALUE;
import static com.hongbog.util.LabelSharedPreference.PreferenceConstant.PREF_KEY;
import static com.hongbog.util.LabelSharedPreference.PreferenceConstant.PREF_NAME;

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
        int DEF_INT_VALUE = -999;
        int GARBAGE_VALUE = -1;
    }

    public LabelSharedPreference(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public int getInt(String keyName){
        return preferences.getInt(keyName, DEF_INT_VALUE);
    }

    public int getInt(){
        return preferences.getInt(PREF_KEY, DEF_INT_VALUE);
    }

    public boolean contains(String keyName){
        return preferences.contains(keyName);
    }

    public boolean contains(){
        return preferences.contains(PREF_KEY);
    }

    public boolean putInt(int value){
        editor.putInt(PREF_KEY, value);
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
