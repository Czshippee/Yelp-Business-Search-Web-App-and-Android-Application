package com.example.yelp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yelp.databinding.ActivityCalendarBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    Toolbar toolbar;
    SharedPreferences sharedpreferences;
    int number_result;
    String[] booked_names,booked_emails,booked_dates,booked_times;
    RecyclerView recyclerView;
    CalendarAdapter calendarAdapter;
    LinearLayout coordinatorLayout;
    TextView noresultlabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        noresultlabel = findViewById(R.id.noResult);

        sharedpreferences = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);

        if (sharedpreferences.getInt("number_result", -1)!=0){
            noresultlabel.setVisibility(View.INVISIBLE);
            // set up the RecyclerView
            recyclerView = findViewById(R.id.calendar_result);
            coordinatorLayout = findViewById(R.id.coordinatorLayout);
            createTable();
            enableSwipeToDelete();
        }else {
            noresultlabel.setVisibility(View.VISIBLE);
        }

        toolbar = findViewById(R.id.calender_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    //https://www.digitalocean.com/community/tutorials/android-recyclerview-swipe-to-delete-undo#code
    private void enableSwipeToDelete() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final CalendarInfo item = calendarAdapter.getItem(position);

                int index = calendarAdapter.removeItem(position);
                updateTable(index);

                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Removing Existing Reservation", Snackbar.LENGTH_LONG);

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

    public void updateTable(int position){
        List<CalendarInfo> calendarInfos = new ArrayList<>();
        number_result = sharedpreferences.getInt("number_result", -1);
        for (int i = 0; i < number_result; i++){
            if (i==position) continue;
            CalendarInfo ci = new CalendarInfo();
            ci.setBookid(""+(i+1));
            ci.setBooked_name(sharedpreferences.getString("booked_name_"+i, null));
            ci.setBooked_email(sharedpreferences.getString("booked_email_"+i, null));
            ci.setBooked_date(sharedpreferences.getString("booked_date_"+i, null));
            ci.setBooked_time(sharedpreferences.getString("booked_time_"+i, null));
            calendarInfos.add(ci);
        }
        number_result -= 1;
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putInt("number_result", number_result);
        assert number_result==calendarInfos.size();
        for (int n=0;n<number_result;n++){
            editor.putString("booked_name_"+n, calendarInfos.get(n).getBooked_name());
            editor.putString("booked_email_"+n, calendarInfos.get(n).getBooked_email());
            editor.putString("booked_date_"+n, calendarInfos.get(n).getBooked_date());
            editor.putString("booked_time_"+n, calendarInfos.get(n).getBooked_time());
        }
        editor.commit();

        createTable();

        if (number_result==0){
            noresultlabel.setVisibility(View.VISIBLE);
            return;
        }
    }

    public void createTable(){
        List<CalendarInfo> calendarInfos = new ArrayList<>();
        number_result = sharedpreferences.getInt("number_result", -1);
        for (int i = 0; i < number_result; i++){
            CalendarInfo ci = new CalendarInfo();
            ci.setBookid(""+(i+1));
            ci.setBooked_name(sharedpreferences.getString("booked_name_"+i, null));
            ci.setBooked_email(sharedpreferences.getString("booked_email_"+i, null));
            ci.setBooked_date(sharedpreferences.getString("booked_date_"+i, null));
            ci.setBooked_time(sharedpreferences.getString("booked_time_"+i, null));
            calendarInfos.add(ci);
        }

        // set a GridLayoutManager with 3 number of columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(linearLayoutManager);

        calendarAdapter = new CalendarAdapter(calendarInfos, this);
        recyclerView.setAdapter(calendarAdapter);
    }


}