package com.example.yelp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Fragment3 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String c11,c12,c13,c14,c21,c22,c23,c24,c31,c32,c33,c34;

    // TODO: Rename and change types and number of parameters
    public static Fragment3 newInstance(String param1, String param2) {
        Fragment3 fragment = new Fragment3();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment3() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment3_layout, container, false);

        DetailedActivity activity = (DetailedActivity) getActivity();
        String[] dataFromAct = activity.F3getMyData();

        c11 = dataFromAct[0];
        c12 = "Rating:"+dataFromAct[1]+"/5";
        c13 = dataFromAct[2];
        c14 = dataFromAct[3];
        c21 = dataFromAct[4];
        c22 = "Rating:"+dataFromAct[5]+"/5";
        c23 = dataFromAct[6];
        c24 = dataFromAct[7];
        c31 = dataFromAct[8];
        c32 = "Rating:"+dataFromAct[9]+"/5";
        c33 = dataFromAct[10];
        c34 = dataFromAct[11];

        TextView textView11,textView12,textView13,textView14,textView21,textView22,textView23,textView24,textView31,textView32,textView33,textView34;
        textView11 = root.findViewById(R.id.comment1_name);
        textView12 = root.findViewById(R.id.comment1_rating);
        textView13 = root.findViewById(R.id.comment1_text);
        textView14 = root.findViewById(R.id.comment1_time);
        textView11.setText(c11);
        textView12.setText(c12);
        textView13.setText(c13);
        textView14.setText(c14);
        textView21 = root.findViewById(R.id.comment2_name);
        textView22 = root.findViewById(R.id.comment2_rating);
        textView23 = root.findViewById(R.id.comment2_text);
        textView24 = root.findViewById(R.id.comment2_time);
        textView21.setText(c21);
        textView22.setText(c22);
        textView23.setText(c23);
        textView24.setText(c24);
        textView31 = root.findViewById(R.id.comment3_name);
        textView32 = root.findViewById(R.id.comment3_rating);
        textView33 = root.findViewById(R.id.comment3_text);
        textView34 = root.findViewById(R.id.comment3_time);
        textView31.setText(c31);
        textView32.setText(c32);
        textView33.setText(c33);
        textView34.setText(c34);
        return root;
    }
}