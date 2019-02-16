package max.project.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import max.project.API;
import max.project.R;
import max.project.RequestHandler;
import max.project.SharedPrefManager;

import static max.project.API.flag;
import static max.project.Data.Utility.LOGIN_URL;

public class LoginActivity extends AppCompatActivity {

    private EditText UA_NAME;
    private EditText password;

    private CheckBox showPassword;

    private MaterialButton login;

    ProgressDialog progressDialog;

    private static ConstraintLayout loginLayout;

    public String logUA_NAME;
    public String logPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        //to check whether user is logged in or not
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {

            Intent attendanceIntent = new Intent(getApplicationContext(), AttendanceActivity.class);
            attendanceIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(attendanceIntent);
            finish();

        }

        flag.setListener(new API.BooVariable.ChangeListener() {
            @Override
            public void onChange() {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Successfully Logged in", Toast.LENGTH_SHORT).show();
                //to lead user to next page
                Intent attendanceIntent = new Intent(getApplicationContext(), AttendanceActivity.class);
                attendanceIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(attendanceIntent);
                finish();
            }
        });

        loginLayout = findViewById(R.id.loginLayout);

        progressDialog = new ProgressDialog(this);

        UA_NAME = findViewById(R.id.UA_NAME);
        password = findViewById(R.id.password);

        showPassword = findViewById(R.id.showPassword);
        //to show password on request
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    password.setTransformationMethod(null);
                } else {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        //to login the user
        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


    }

    private void attemptLogin() {
        // Reset errors.
        UA_NAME.setError(null);
        password.setError(null);

        // Store values at the time of the login attempt.
        logUA_NAME = UA_NAME.getText().toString().trim();
        logPassword = password.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(logPassword)) {
            password.setError("Field Required");
            focusView = password;
            cancel = true;
        }/* else if (!isPasswordValid(logPassword)) {
            password.setError("Invalid Password");
            focusView = password;
            cancel = true;
        }*/


        // Check for a valid email address.
        if (TextUtils.isEmpty(logUA_NAME)) {
            UA_NAME.setError("Field Required");
            focusView = UA_NAME;
            cancel = true;
        } else if (!isUA_NAMEvalid(logUA_NAME)) {
            UA_NAME.setError("Invalid UA_NAME");
            focusView = UA_NAME;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // perform the user login.
            loginUser();
        }
    }

    //to check whether UA_NAME and password is valid or not
    private boolean isUA_NAMEvalid(String logUA_NAME) {
        //TODO: Replace this with your own logic
        return true;
    }



    //user login function
    private void loginUser() {

        progressDialog.setMessage("Validating Credentials");
        progressDialog.setTitle("Logging In");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONObject(response).getJSONArray("output");
                            if (jsonArray.getJSONObject(0).has("failure")) {
                                progressDialog.dismiss();
                                LoginActivity.showSnackBar(jsonArray.getJSONObject(0).getString("error"));

                            } else {
                                SharedPrefManager.getInstance(getApplicationContext())
                                        .loginUser(jsonArray.getJSONObject(0).getString("UA_NO"), jsonArray.getJSONObject(0).getString("UA_FULLNAME"));
                                API.getInstance(getApplicationContext()).callEverything();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
                        String message = null;
                        if (error instanceof ServerError) {
                            message = "The server could not be found. Please try again after some time!!";
                        } else if (error instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                        } else if (error instanceof TimeoutError) {
                            message = "Connection TimeOut! Please check your internet connection.";
                        } else {
                            message = "There was some problem, Please try again later.";
                        }
                        LoginActivity.showSnackBar(message);
                    }
                }

        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //sending UA_NAME and password to php script
                params.put("UA_NAME", logUA_NAME);
                params.put("PASS", logPassword);

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

    public static void showSnackBar(String message) {

        final Snackbar mSnackBar = Snackbar.make(loginLayout, message, Snackbar.LENGTH_LONG);
        mSnackBar.setAction("OKAY", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mSnackBar.show();

    }

}
