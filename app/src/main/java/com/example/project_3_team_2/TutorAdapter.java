package com.example.project_3_team_2;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class TutorAdapter extends BaseAdapter {

    Context context;
    ArrayList<Tutor> tutors;

    public TutorAdapter(ArrayList<Tutor> tutors, Context context) {
        this.context = context;
        this.tutors = tutors;
    }

    @Override
    public int getCount() {return tutors.size();}

    @Override
    public Object getItem(int i) {return tutors.get(i);}

    @Override
    public long getItemId(int i) {return 0;}

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Tutor tutor = (Tutor)getItem(i);
        if(view == null)
            view = LayoutInflater.from(context).inflate(R.layout.tutor_list_item_layout,viewGroup,false);
        TextView txtTutorName = view.findViewById(R.id.txtTutorName);
        TextView txtTutorSubject = view.findViewById(R.id.txtTutorSubject);

        ConstraintLayout root = view.findViewById(R.id.rootTutorListItem);
        root.setOnClickListener(v->{

        });

        txtTutorName.setText(tutor.name);
        txtTutorSubject.setText(tutor.subject);

        root.setOnClickListener(v->{
//            Log.d("TutorHub","Clicked tutor with id " + tutor.id);
            Intent intent = new Intent(context,TutorInformation.class);
            intent.putExtra("userID",tutor.id);
            context.startActivity(intent);
        });
        return view;
    }
}
