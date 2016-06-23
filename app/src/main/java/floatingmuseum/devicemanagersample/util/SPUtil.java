package floatingmuseum.devicemanagersample.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;


public class SPUtil {

    public static String getString(Context context, String key,
                                   String defaultValue) {
        return context.getSharedPreferences("config", Context.MODE_PRIVATE)
                .getString(key, defaultValue);
    }


    public static void editString(Context context, String key, String value) {
        Editor edit = context.getSharedPreferences("config",
                Context.MODE_PRIVATE).edit();
        edit.putString(key, value);
        edit.commit();
    }


    public static boolean getBoolean(Context context, String key,
                                     boolean defaultValue) {
        return context.getSharedPreferences("config", Context.MODE_PRIVATE)
                .getBoolean(key, defaultValue);
    }

    public static void editBoolean(Context context, String key, boolean value) {
        Editor edit = context.getSharedPreferences("config",
                Context.MODE_PRIVATE).edit();
        edit.putBoolean(key, value);
        edit.commit();
    }


    public static int getInt(Context context,String key,int defaultValue){
        int number = PreferenceManager.getDefaultSharedPreferences(context).getInt(key,defaultValue);
        return number;
//        return context.getSharedPreferences("config", Context.MODE_PRIVATE)
//                .getInt(key, defaultValue);
    }

    public static void editInt(Context context, String key, int value) {
        SharedPreferences.Editor edit = context.getSharedPreferences("config",
                Context.MODE_PRIVATE).edit();
        edit.putInt(key, value);
        edit.commit();
    }
}
