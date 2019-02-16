package max.project;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by max on 29/4/17.
 * <p>
 * TO STORE DATA OFFLINE IN SHARED PREFERENCES
 */

public class SharedPrefManager {

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private ArrayList<String> courseList;
    private ArrayList<Map<String,String>> savedAttendanceList;
    private ArrayList<String> semList;

    public static String getUaNo() {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_NO, null);
    }

    public static String getUaFullname() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_FULLNAME, null);
    }


    private static final String SHARED_PREF_NAME = "mysharedpref";
    private static final String KEY_NO = "UA_NO";
    private static final String KEY_FULLNAME = "UA_FULLNAME";

    private SharedPrefManager(Context context) {

        mCtx = context;

    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public boolean loginUser(String UA_NO, String UA_FULLNAME) {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NO, UA_NO);
        editor.putString(KEY_FULLNAME, UA_FULLNAME);

        editor.apply();

        return true;

    }

    public boolean isLoggedIn() {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(KEY_FULLNAME, null) != null) {

            return true;
        } else {

            return false;
        }
    }

    public boolean logout() {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }

    public void setCourseList(ArrayList<String> courseList) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(courseList);

        editor.putString("courseList", json);
        editor.apply();
    }

    public ArrayList<String> getCourseList() {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("courseList", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        courseList = gson.fromJson(json, type);
        return courseList;

    }

    public void setSavedAttendance(ArrayList<Map<String,String>> savedAttendance){

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(savedAttendance);

        editor.putString("savedAttendance", json);
        editor.apply();
    }

    public ArrayList<Map<String,String>> getSavedAttendance(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("savedAttendance", null);
        Type type = new TypeToken<ArrayList<Map<String,String>>>() {
        }.getType();

        savedAttendanceList = gson.fromJson(json,type);
        return savedAttendanceList;
    }

    public void setSemList(ArrayList<String> semList){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(semList);

        editor.putString("semList", json);
        editor.apply();
    }

    public ArrayList<String> getSemList(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("semList", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();

        semList= gson.fromJson(json,type);
        return semList;
    }

}
