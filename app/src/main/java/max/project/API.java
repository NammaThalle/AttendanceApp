package max.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import max.project.Activity.AttendanceActivity;
import max.project.Activity.SavedAttendanceActivity;

import static max.project.Data.Utility.ATT_URL;
import static max.project.Data.Utility.COURSELIST_URL;
import static max.project.Data.Utility.SEMLIST_URL;

/**
 * Created by maner on 03-02-2018.
 */

public class API {

    private StringRequest subjects;
    private StringRequest sem;
    static API mInstance = null;
    private Context context;

    private static final String message = "Couldn't connect to server, Manually update after connection has established";

    public static BooVariable flag = new BooVariable();

    public static synchronized API getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new API();
        }

        mInstance.context = context;
        return mInstance;

    }

    public void setSubjects() {

        final ArrayList<String> courseList = new ArrayList<>();

        subjects = new StringRequest(Request.Method.POST,
                COURSELIST_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray array = jsonObject.getJSONArray("output");

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject o = array.getJSONObject(i);
                                courseList.add(o.getString("SHORTNAME"));
                                SharedPrefManager.getInstance(context).setCourseList(courseList);
                                SharedPrefManager.getInstance(context).setSavedAttendance(new ArrayList<Map<String, String>>());


                            }
                            mInstance.setSemList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("UA_NO", SharedPrefManager.getUaNo());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                //setting the headers
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/x-www-form-urlencoded");

                return headers;
            }
        };

        RequestHandler.getInstance(context).addToRequestQueue(subjects);

    }

    private void setSemList() {

        final ArrayList<String> semList = new ArrayList<>();

        sem = new StringRequest(Request.Method.POST,
                SEMLIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray array = jsonObject.getJSONArray("output");

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject o = array.getJSONObject(i);
                                semList.add(o.getString("SEMESTERNO"));
                                SharedPrefManager.getInstance(context).setSemList(semList);

                                flag.setBoo(true);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("UA_NO", SharedPrefManager.getUaNo());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                //setting the headers
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/x-www-form-urlencoded");

                return headers;
            }
        };

        RequestHandler.getInstance(context).addToRequestQueue(sem);

    }

    public void setSubmitAttendance(final Map<String, String> params, final Map<String, String> headers) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ATT_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONObject(response).getJSONArray("output");
                            if (jsonArray.getJSONObject(0).getInt("success") == 1) {

                                Toast.makeText(context, "Attendance Submitted Successfully", Toast.LENGTH_LONG).show();

                                ArrayList<Map<String, String>> savedAttendanceList = SharedPrefManager.getInstance(context).getSavedAttendance();
                                savedAttendanceList.remove(params);
                                SharedPrefManager.getInstance(context).setSavedAttendance(savedAttendanceList);


                            } else {

                                Toast.makeText(context, "Error! Could not submit", Toast.LENGTH_LONG).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (AttendanceActivity.progressDialog != null && AttendanceActivity.progressDialog.isShowing()) {
                            AttendanceActivity.progressDialog.dismiss();
                        }
                        if (SavedAttendanceActivity.progressDialog != null && SavedAttendanceActivity.progressDialog.isShowing()) {
                            SavedAttendanceActivity.progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (AttendanceActivity.progressDialog != null && AttendanceActivity.progressDialog.isShowing()) {
                            AttendanceActivity.progressDialog.dismiss();
                        }
                        if (SavedAttendanceActivity.progressDialog != null && SavedAttendanceActivity.progressDialog.isShowing()) {
                            SavedAttendanceActivity.progressDialog.dismiss();
                        }


                        //Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                        if (error instanceof NoConnectionError) {

                            ArrayList<Map<String, String>> savedAttendanceList = SharedPrefManager.getInstance(context).getSavedAttendance();
                            if (!savedAttendanceList.contains(params)) {
                                savedAttendanceList.add(params);
                            }
                            SharedPrefManager.getInstance(context).setSavedAttendance(savedAttendanceList);

                            AttendanceActivity.showSnackBar(message);
                        }


                    }
                }) {


            @SuppressLint("SimpleDateFormat")
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return headers;
            }
        };

        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

    }

    public void callEverything() {

        this.setSubjects();

    }

    public static class BooVariable {
        private boolean boo = false;
        private ChangeListener listener;

        public boolean isBoo() {
            return boo;
        }

        public void setBoo(boolean boo) {
            this.boo = boo;
            if (listener != null) listener.onChange();
        }

        public void setListener(ChangeListener listener) {
            this.listener = listener;
        }

        public interface ChangeListener {
            void onChange();
        }
    }


}
