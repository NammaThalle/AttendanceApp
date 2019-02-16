package max.project.Fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import max.project.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFilterFragment extends Fragment {


    public static Button from,to;

    public ReportFilterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_fragment, container, false);
        to = (Button)view.findViewById(R.id.to);
        from = (Button)view.findViewById(R.id.from);
        from.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new FromDatePickerFragment();
                newFragment.show(getFragmentManager(),"datePicker");
            }
        });

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new ToDatePickerFragment();
                newFragment.show(getFragmentManager(),"datePicker");
            }
        });

        return view;
    }

    public static class FromDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        public FromDatePickerFragment() {
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(),this,year,month,day);
            dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            return dialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            from.setText(dayOfMonth+"/"+(month+1)+"/"+year);
        }
    }
    public static class ToDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        public ToDatePickerFragment() {
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(),this,year,month,day);
            dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            return dialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            to.setText(dayOfMonth+"/"+(month+1)+"/"+year);

        }
    }

}

