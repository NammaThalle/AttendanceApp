package max.project.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import max.project.Data.AttendanceCard;
import max.project.R;

import static max.project.Activity.AttendanceActivity.ATT;
import static max.project.Activity.AttendanceActivity.IDS;

/**
 * Created by max on 28/4/17.
 *
 * CLASS FOR CREATING CUSTOM ADAPTER FOR RECYCLER VIEW
 */

public class AttendanceCardAdapter extends RecyclerView.Adapter<AttendanceCardAdapter.ViewHolder>{

    private List<AttendanceCard> attendanceCards;
    public Context context;

    public AttendanceCardAdapter(List<AttendanceCard> attendanceCards, Context context) {
        this.attendanceCards = attendanceCards;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item,parent,false);

        return new ViewHolder(v);

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final AttendanceCard attendanceCard = attendanceCards.get(position);

        holder.setIsRecyclable(false);

        //populating list items
        holder.idno.setText(attendanceCard.getRegno());
        holder.name.setText(attendanceCard.getName());
       /* holder.days.setText(attendanceCard.getDays());
        holder.percent.setText(attendanceCard.getPercent());*/
        holder.att.setChecked(attendanceCard.isFlag());

        //fetching current attendance of selected listitem
        holder.att.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                attendanceCard.setFlag(isChecked);

                if (!isChecked) {
                    for(int i = 0; i< attendanceCards.size(); i++) {

                        if(IDS[i]== attendanceCard.getIdno()){

                            ATT[i]=0;
                        }

                    }
                }
                else{
                    for(int i = 0; i< attendanceCards.size(); i++) {

                        if(IDS[i]== attendanceCard.getIdno()){

                            ATT[i]=1;
                        }

                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return attendanceCards.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView idno;
        public TextView name;
       /* public TextView days;
        public TextView percent;*/
        public Switch att;

        public ViewHolder(View itemView) {
            super(itemView);

            idno =(TextView) itemView.findViewById(R.id.idno);
            name=(TextView) itemView.findViewById(R.id.name);
            /*days=(TextView) itemView.findViewById(R.id.days);
            percent=(TextView) itemView.findViewById(R.id.percent);*/

            att=(Switch) itemView.findViewById(R.id.att);
        }
    }
}
