package com.hongbog.util;

import android.content.Context;
import android.content.SharedPreferences;

import static com.hongbog.util.PreferenceUtil.PreferenceConstant.DEF_INT_VALUE;
import static com.hongbog.util.PreferenceUtil.PreferenceConstant.PREF_KEY;
import static com.hongbog.util.PreferenceUtil.PreferenceConstant.PREF_NAME;

/**
 * Created by taein on 2018-08-29.
 */
public class PreferenceUtil {

    private static PreferenceUtil preferencemodule = null;
    private static Context mContext;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    public interface PreferenceConstant{
        String PREF_NAME = "LABEL_PREF";
        String PREF_KEY = "enrolledLabel";
        int DEF_INT_VALUE = -999;
        int GARBAGE_VALUE = -1;
    }

    private PreferenceUtil(){}

    public static PreferenceUtil getInstance(Context context) {
        mContext = context;

        if (preferencemodule == null) {
            preferencemodule = new PreferenceUtil();
        }

        if(prefs==null){
            prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = prefs.edit();
        }

        return preferencemodule;
    }

    public void putIntExtra(int value){
        editor.putInt(PREF_KEY, value);
        editor.apply();
    }

    public void putIntExtra(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public void putStringExtra(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public void putLongExtra(String key, long value) {
        editor.putLong(key, value);
        editor.apply();
    }

    public void putBooleanExtra(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public int getIntExtra(String key) {
        return prefs.getInt(key, 0);
    }

    public int getIntExtra(){
        return prefs.getInt(PREF_KEY, DEF_INT_VALUE);
    }

    public String getStringExtra(String key) {
        return prefs.getString(key, "");
    }


    public long getLongExtra(String key) {
        return prefs.getLong(key, 0);
    }


    public boolean getBooleanExtra(String key) {
        return prefs.getBoolean(key, false);
    }

    public void removePreference(String key) {
        editor.remove(key).apply();
    }

    public boolean contains(String key) {
        return prefs.contains(key);
    }

    public boolean contains(){
        return prefs.contains(PREF_KEY);
    }

    public void clear(){
        editor.clear();
        editor.apply();
    }
}
