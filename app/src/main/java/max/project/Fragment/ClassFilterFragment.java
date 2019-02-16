package max.project.Fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Objects;

import max.project.Activity.LoginActivity;
import max.project.R;
import max.project.SharedPrefManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClassFilterFragment extends Fragment {

    private Spinner sem;
    private Spinner subject;
    private Spinner batch;
    private Spinner classType;
    private Spinner lectureType;

    private ArrayList<String> s;

    public static String getCsem() {
        return csem;
    }

    private static String csem;

    private static String csub;

    private static String ltype;

    public static String getCsub() {
        return csub;
    }


    public static String getCbatch() {
        return cbatch;
    }


    private static String cbatch;

    public static String getCtype() {
        return ctype;
    }

    public  static  String getLtype(){
        return ltype;
    }

    private static String ctype;

    ArrayAdapter<String> semAdapter;
    ArrayAdapter<String> subjectAdapter;
    ArrayAdapter<String> batchAdapter;
    ArrayAdapter<String> classTypeAdapter;
    ArrayAdapter<String> lectureTypeAdapter;

    private ProgressDialog progressDialog;


    public ClassFilterFragment() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_class_filter, container, false);

        if (!SharedPrefManager.getInstance(getContext()).isLoggedIn()) {

            Intent loginIntent = new Intent(getContext(), LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);

        }

        progressDialog = new ProgressDialog(getContext());

        sem = (Spinner) view.findViewById(R.id.sem);
        subject = (Spinner) view.findViewById(R.id.subject);
        batch = (Spinner) view.findViewById(R.id.batch);
        classType = (Spinner) view.findViewById(R.id.classType);
        lectureType = (Spinner) view.findViewById(R.id.lectureType);

        semAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, SharedPrefManager.getInstance(getContext()).getSemList());
        semAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sem.setAdapter(semAdapter);

        subjectAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, SharedPrefManager.getInstance(getContext()).getCourseList());
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject.setAdapter(subjectAdapter);

        batchAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.batch));
        batchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        batch.setAdapter(batchAdapter);

        classTypeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.classType));
        classTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classType.setAdapter(classTypeAdapter);

        lectureTypeAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.lectureType));
        lectureTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lectureType.setAdapter(lectureTypeAdapter);

        sem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                csem = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                csub = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        batch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                cbatch = parent.getItemAtPosition(position).toString();

                if (Objects.equals(cbatch, "FULL CLASS")) {
                    cbatch = "0";
                } else if (Objects.equals(cbatch, "BATCH A")) {
                    cbatch = "7";
                } else if (Objects.equals(cbatch, "BATCH B")) {
                    cbatch = "8";
                } else if (Objects.equals(cbatch, "BATCH C")) {
                    cbatch = "9";
                } else if (Objects.equals(cbatch, "BATCH D")) {
                    cbatch = "10";
                } else {
                    cbatch = "BATCH";
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        lectureType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ltype = parent.getItemAtPosition(position).toString();
                if(Objects.equals(ltype, "Regular")){
                    ltype="1";
                }
                else if(Objects.equals(ltype, "Extra 1")){
                    ltype="2";
                }
                else if(Objects.equals(ltype, "Extra 2")){
                    ltype="3";
                }
                else if(Objects.equals(ltype, "Extra 3")){
                    ltype="4";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        classType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                ctype = parent.getItemAtPosition(position).toString();
                if(Objects.equals(ctype, "THEORY")){
                    ctype="1";
                    batch.setVisibility(View.GONE);
                    cbatch="0";
                }
                else{
                    batch.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

}
