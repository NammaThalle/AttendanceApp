package max.project.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import max.project.API;
import max.project.R;
import max.project.SharedPrefManager;

public class SavedAttendanceActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private Button submitButton;

    public static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_attendance);

        tableLayout = findViewById(R.id.tableLayout);

        progressDialog = new ProgressDialog(this);

        submitButton = findViewById(R.id.savedSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSavedAttendance();
            }
        });

        ArrayList<Map<String, String>> savedAttendanceList = SharedPrefManager.getInstance(getApplicationContext()).getSavedAttendance();

        View tableRow = LayoutInflater.from(this).inflate(R.layout.table_item, null, false);
        tableRow.setBackgroundColor(getResources().getColor(R.color.colorBackground));
        TextView srno = tableRow.findViewById(R.id.srno);
        TextView subject = tableRow.findViewById(R.id.subject);
        TextView batchno = tableRow.findViewById(R.id.batchno);
        TextView date = tableRow.findViewById(R.id.date);

        srno.setText("SR NO.");
        subject.setText("Subject");
        batchno.setText("Batch");
        date.setText("Date");
        tableLayout.addView(tableRow);

        for (int i = 0; i < savedAttendanceList.size(); i++) {
            tableRow = LayoutInflater.from(this).inflate(R.layout.table_item, null, false);
            tableRow.setBackgroundColor(getResources().getColor(R.color.colorExtra));
            srno = tableRow.findViewById(R.id.srno);
            subject = tableRow.findViewById(R.id.subject);
            batchno = tableRow.findViewById(R.id.batchno);
            date = tableRow.findViewById(R.id.date);

            Map<String, String> list = SharedPrefManager.getInstance(getApplicationContext()).getSavedAttendance().get(i);
            String batch = list.get("BATCHNO");
            switch (batch) {
                case "0":
                    batch = "Full Class";
                    break;
                case "7":
                    batch = "A";
                    break;
                case "8":
                    batch = "B";
                    break;
                case "9":
                    batch = "C";
                    break;
                case "10":
                    batch = "D";
                    break;
            }
            srno.setText("" + (i + 1));
            subject.setText(list.get("SHORTNAME"));
            batchno.setText(batch);
            date.setText(list.get("ATT_DATE"));
            tableLayout.addView(tableRow);
        }

    }

    private void sendSavedAttendance() {

        progressDialog.setTitle("Submitting Attendance");
        progressDialog.setMessage("Please be Patient");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final ArrayList<Map<String, String>> savedList = SharedPrefManager.getInstance(getApplicationContext()).getSavedAttendance();

        final HashMap<String, String> headers = new HashMap<>();

        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        for (int i = 0; i < savedList.size(); i++) {

            API.getInstance(getApplicationContext()).setSubmitAttendance(savedList.get(i), headers);


        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (SharedPrefManager.getInstance(getApplicationContext()).getSavedAttendance().size() == 0) {
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "ERROR!!!, Some Entries Not Updated", Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), SavedAttendanceActivity.class));
                }
            }
        },savedList.size()*1000);


    }
}
