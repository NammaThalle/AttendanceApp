package max.project.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import max.project.API;
import max.project.Adapter.AttendanceCardAdapter;
import max.project.Adapter.SwipeAdapter;
import max.project.Data.AttendanceCard;
import max.project.Fragment.ClassFilterFragment;
import max.project.R;
import max.project.RequestHandler;
import max.project.SharedPrefManager;

import static max.project.Data.Utility.STUDENTLIST_URL;

public class AttendanceActivity extends AppCompatActivity {

    private static boolean isDataLoaded = false;

    private boolean doublePressBackToExit = false;
    private boolean submitPressed = false;

    private static CoordinatorLayout attendanceLayout;

    public static String[] IDS;
    public static int[] ATT;

    private static String date;

    private static Context context;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;

    public ProgressDialog pd;
    public static ProgressDialog progressDialog;

    private List<AttendanceCard> attendanceCards;

    private ViewPager viewPager;

    private int dotsCount;
    private ImageView[] dots;

    private Menu menu;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        if (SharedPrefManager.getInstance(getApplicationContext()).getCourseList() == null) {

            API.getInstance(getApplicationContext()).setSubjects();

        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.qr);
        getSupportActionBar().setTitle(SharedPrefManager.getUaFullname());

        viewPager = findViewById(R.id.viewpager);
        SwipeAdapter swipeAdapter = new SwipeAdapter(getSupportFragmentManager());
        viewPager.setAdapter(swipeAdapter);

        context = getApplicationContext();

        //checking if the user is logged in or not
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {

            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(loginIntent);
            return;

        }

        date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());

        attendanceLayout = findViewById(R.id.attendanceLayout);

        pd = new ProgressDialog(this);
        progressDialog = new ProgressDialog(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        attendanceCards = new ArrayList<>();

        final Switch defaultpresent = findViewById(R.id.defaultpresent);

        defaultpresent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                attendanceCards.clear();
                if (isChecked)
                    loadRecyclerViewData(true);
                else
                    loadRecyclerViewData(false);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (doublePressBackToExit) {
            AlertDialog alert = new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null).create();
            alert.show();
        }

        if (submitPressed && !doublePressBackToExit) {
            Intent intent = new Intent(getApplicationContext(), AttendanceActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        this.doublePressBackToExit = true;


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                doublePressBackToExit = false;
            }
        }, 200);
    }

    //recyclerview populator
    public void loadRecyclerViewData(final Boolean flag) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                STUDENTLIST_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray array = jsonObject.getJSONArray("output");
                            /*int sub,sub_total;
                            float percent;*/
                            IDS = new String[array.length()];
                            ATT = new int[array.length()];


                            for (int i = 0; i < array.length(); i++) {

                                JSONObject o = array.getJSONObject(i);

                                AttendanceCard item = new AttendanceCard(o.getString("IDNO"), o.getString("REGNO"), o.getString("STUDNAME")/*,sub+"/"+sub_total,percent+"%"*/, flag);
                                IDS[i] = o.getString("IDNO");
                                ATT[i] = flag ? 1 : 0;
                                attendanceCards.add(item);
                            }

                            pd.dismiss();

                            recyclerViewAdapter = new AttendanceCardAdapter(attendanceCards, getApplicationContext());
                            recyclerView.setAdapter(recyclerViewAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error instanceof NoConnectionError)
                            AttendanceActivity.showSnackBar("Network is Unreachable");
                        pd.dismiss();

                    }
                }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                //passing parameters to php script
                params.put("BATCHNO", ClassFilterFragment.getCbatch());
                params.put("SESSIONNO", "74");
                params.put("UA_NO", SharedPrefManager.getUaNo());
                params.put("SUBTYPE", ClassFilterFragment.getCtype().equals("Tutorial+Practical") ? "Tutorial" : ClassFilterFragment.getCtype());
                params.put("SHORTNAME", ClassFilterFragment.getCsub());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();

                //setting the headers
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/x-www-form-urlencoded");

                return headers;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

    }

    //handling 3 dot menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_attendance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.logout) {
            pd.setMessage("Logging out");
            pd.setTitle("Please Wait");
            pd.setCanceledOnTouchOutside(false);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
            pd.setIndeterminate(true);

            SharedPrefManager.getInstance(this).logout();
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(loginIntent);
            pd.dismiss();
        }
        if(id == R.id.refreshButton){
            if (!Objects.equals(ClassFilterFragment.getCsem(), "SEMESTER") && !Objects.equals(ClassFilterFragment.getCsub(), "SUBJECT")
                    && !Objects.equals(ClassFilterFragment.getCtype(), "CLASS TYPE") && !Objects.equals(ClassFilterFragment.getCbatch(), "BATCH")
                    && !Objects.equals(ClassFilterFragment.getLtype(),"LECTURE")) {

                attendanceCards.clear();
                if (ClassFilterFragment.getCtype().equals("Theory") && !ClassFilterFragment.getCbatch().equals("0")) {
                    AttendanceActivity.showSnackBar(ClassFilterFragment.getCtype() + " Cannot Have Batches");

                } else if (!ClassFilterFragment.getCtype().equals("Theory") && ClassFilterFragment.getCbatch().equals("0")) {

                    AttendanceActivity.showSnackBar(ClassFilterFragment.getCtype() + " Cannot Be For Full Class");

                } else {
                    ImageView imageview = findViewById(R.id.image);
                    LinearLayout defaultSetting = findViewById(R.id.defaultSetting);
                    imageview.setVisibility(View.INVISIBLE);
                    defaultSetting.setVisibility(View.VISIBLE);


                    pd.setTitle("Fetching Data");
                    pd.setMessage("Please Be Patient");
                    pd.show();


                    loadRecyclerViewData(true);
                    isDataLoaded = true;

                }

                MenuItem refreshMenuItem = menu.findItem(R.id.refreshButton);
                refreshMenuItem.setTitle("REFRESH");


            } else {

                AttendanceActivity.showSnackBar("Please Select All Filters");

            }

        }
        if (id == R.id.submitButton) {

            if (attendanceCards.size() != 0) {
                //ATTENDANCE TO BE SUBMITTED
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            /*builder.setTitle("Add Attendance")
                    .setMessage("Do you want to add this attendance?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            submitAttendance();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();*/

            } else {
                AttendanceActivity.showSnackBar("Please Conduct A Class");

            }

        }

        if (id == R.id.saved) {

            if (SharedPrefManager.getInstance(context).getSavedAttendance().size() != 0) {
                //Show the attendance to be submitted
                Intent intent = new Intent(getApplicationContext(), SavedAttendanceActivity.class);
                startActivity(intent);
            } else {
                AttendanceActivity.showSnackBar("There are no pending uploads");
            }

        }

        if(id == android.R.id.home){
            Intent intent = new Intent(getApplicationContext(),ScannerActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public static void showSnackBar(String message) {

        final Snackbar mSnackBar = Snackbar.make(attendanceLayout, message, Snackbar.LENGTH_LONG);
        mSnackBar.setAction("OKAY", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mSnackBar.show();

    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        public DatePickerFragment() {
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            @SuppressLint("ResourceType") DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            dialog.getDatePicker().setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            dialog.setButton(DatePickerDialog.BUTTON_POSITIVE,"Submit", dialog);
            return dialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            date = year + "-" + (month + 1) + "-" + dayOfMonth + " 00:00:00";
            submitAttendance();
        }
    }

    public void submitAttendance() {

        if (isDataLoaded) {
            progressDialog.setTitle("Submitting Attendance");
            progressDialog.setMessage("Please be Patient");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            List<String> TEMP1 = new ArrayList<>();
            TEMP1.addAll(Arrays.asList(IDS));

            final String STUDIDS = android.text.TextUtils.join(",", TEMP1);

            List<Integer> TEMP = new ArrayList<>();
            for (int aATT : ATT) TEMP.add(aATT);

            final String ATT_STATUS = android.text.TextUtils.join(",", TEMP);

            // final String TH_PR = Objects.equals(ClassFilterFragment.getCtype(), "Practical") ? "2" : "1";
            String TH_PR = "0";
            String PERIOD = "0";
            switch (ClassFilterFragment.getCtype()) {
                case "Theory":
                    TH_PR = "1";
                    PERIOD = "1";
                    break;
                case "Tutorial":
                    TH_PR = "8";
                    PERIOD = "1";
                    break;
                default:
                    TH_PR = "2";
                    PERIOD = "2";
                    break;
            }

            //passing parameters to php script
            Map<String, String> params = new HashMap<>();
            Map<String, String> headers = new HashMap<>();


                    params.put("UA_NO", SharedPrefManager.getUaNo());
                    params.put("SHORTNAME", ClassFilterFragment.getCsub());
                    params.put("BATCHNO", ClassFilterFragment.getCbatch());
                    params.put("STUDIDS", STUDIDS);
                    params.put("ATT_STATUS", ATT_STATUS);
                    params.put("TH_PR", TH_PR);
                    params.put("CLASS_TYPE",ClassFilterFragment.getLtype());
                    params.put("ATT_DATE", date);
                    params.put("PERIOD", PERIOD);
                    params.put("SESSIONNO", "74");

                    headers.put("Accept", "application/json");
                    headers.put("Content-Type", "application/x-www-form-urlencoded");

                    API.getInstance(getApplicationContext()).setSubmitAttendance(params, headers);

                    Intent restartIntent = new Intent(getApplicationContext(), AttendanceActivity.class);
                    restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //startActivity(restartIntent);
                    
        } else {
            Toast.makeText(context, "Please Conduct A Class", Toast.LENGTH_SHORT).show();
        }

        submitPressed = !submitPressed;
    }

    public String getWifiIPAddress() {
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return Formatter.formatIpAddress(ip);
    }

}

