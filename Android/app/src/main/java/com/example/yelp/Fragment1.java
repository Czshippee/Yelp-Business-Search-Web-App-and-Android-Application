package com.example.yelp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.XMLReader;

import java.util.Objects;
import java.util.regex.Pattern;

public class Fragment1 extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "paramId";

    private String myId;

    String response_id;
    String Name, Status, Address, Category, Phone_number, Price, More_info;
    TextView a,b,c,d,e,f;
    ImageView image1,image2,image3;
    RequestQueue requestQueue;
    Calendar dialog_calendar;
    Integer select_hour;
    String booked_name, booked_email, booked_date, booked_time;
    SharedPreferences sharedpreferences;

    public static Fragment1 newInstance(String myId) {
        Fragment1 fragment = new Fragment1(myId);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, myId);
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment1(String inputId) {
        // Required empty public constructor
        myId = inputId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment1_layout, container, false);

        sharedpreferences = getContext().getSharedPreferences("mypref", Context.MODE_PRIVATE);

        String detail_info_url = "https://businessyelpsearch.wl.r.appspot.com/searchdetail"+"?&id="+myId;
        requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                detail_info_url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //System.out.println("Receive data from backend!");
                        try {
                            Name = response.getString("Name");
                            Status = response.getString("Status");
                            Address = response.getString("Address");
                            Category = response.getString("Category");
                            Phone_number = response.getString("Phone_number");
                            Price = response.getString("Price");
                            More_info = response.getString("More_info");
                            JSONArray Photos = response.getJSONArray("Photos");

                            a = root.findViewById(R.id.card_Address);
                            b = root.findViewById(R.id.card_Price);
                            c = root.findViewById(R.id.card_Phonenumber);
                            d = root.findViewById(R.id.card_Status);
                            e = root.findViewById(R.id.card_Category);
                            f = root.findViewById(R.id.card_Moreinfo);

                            if (Address!=null)a.setText(Address);
                            else {
                                root.findViewById(R.id.card_Address_title).setVisibility(View.INVISIBLE);
                                a.setVisibility(View.INVISIBLE);
                            }
                            if (Price!=null)b.setText(Price);
                            else {
                                root.findViewById(R.id.card_Price_title).setVisibility(View.INVISIBLE);
                                b.setVisibility(View.INVISIBLE);
                            }
                            if (Phone_number!=null)c.setText(Phone_number);
                            else {
                                root.findViewById(R.id.card_Phonenumber_title).setVisibility(View.INVISIBLE);
                                c.setVisibility(View.INVISIBLE);
                            }
                            if (Status!=null) {
                                if (Objects.equals(Status, "true")){
                                    d.setText("Open Now");
                                    d.setTextColor(Color.parseColor("#00FF00"));
                                }else{
                                    d.setText("Closed");
                                    d.setTextColor(Color.parseColor("#FF0000"));
                                }
                            }else {
                                root.findViewById(R.id.card_Status_title).setVisibility(View.INVISIBLE);
                                d.setVisibility(View.INVISIBLE);
                            }
                            if (Category!=null)e.setText(Category);
                            else {
                                root.findViewById(R.id.card_Category_title).setVisibility(View.INVISIBLE);
                                e.setVisibility(View.INVISIBLE);
                            }
                            if (More_info!=null) {
                                String More_info_link = "<a href="+More_info+">Business Link</a>";
                                f.setMovementMethod(LinkMovementMethod.getInstance());
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                    f.setText(Html.fromHtml(More_info_link,Html.FROM_HTML_MODE_LEGACY));
                                } else {
                                    f.setText(Html.fromHtml(More_info_link));
                                }
                            }else {
                                root.findViewById(R.id.card_Moreinfo_title).setVisibility(View.INVISIBLE);
                                f.setVisibility(View.INVISIBLE);
                            }

                            Button button = root.findViewById(R.id.tab1_button);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //Snackbar.make(view, "Reservation Booked", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    View dialog_view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_layout,null);//获得布局信息

                                    TextView dialog_title = (TextView) dialog_view.findViewById(R.id.dialog_title);
                                    EditText dialog_email = (EditText) dialog_view.findViewById(R.id.dialog_email);
                                    EditText dialog_date = (EditText) dialog_view.findViewById(R.id.dialog_date);
                                    EditText dialog_time = (EditText) dialog_view.findViewById(R.id.dialog_time);

                                    dialog_title.setText(Name);
                                    // https://www.youtube.com/watch?v=XG8OpQ7X-nY
//                                  // https://www.digitalocean.com/community/tutorials/android-date-time-picker-dialog
                                    dialog_date.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog_calendar = Calendar.getInstance();
                                            int year = dialog_calendar.get(Calendar.YEAR);
                                            int month = dialog_calendar.get(Calendar.MONTH);
                                            int day = dialog_calendar.get(Calendar.DAY_OF_MONTH);
                                            DatePickerDialog date_picker_dialog = new DatePickerDialog(
                                                    getContext(),
                                                    new DatePickerDialog.OnDateSetListener() {
                                                        @Override
                                                        public void onDateSet(DatePicker view, int yyyy, int mm, int dd) {
                                                            String selectDate = (mm + 1) + "-" + dd + "-" + yyyy;
                                                            dialog_date.setText(selectDate);
                                                        }
                                                    }, year, month, day);
                                            date_picker_dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                                            date_picker_dialog.show();
                                        }
                                    });
                                    dialog_time.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog_calendar = Calendar.getInstance();
                                            int hour = dialog_calendar.get(Calendar.HOUR_OF_DAY);
                                            int minute = dialog_calendar.get(Calendar.MINUTE);
                                            TimePickerDialog time_picker_dialog = new TimePickerDialog(
                                                    getContext(),
                                                    new TimePickerDialog.OnTimeSetListener() {
                                                        @Override
                                                        public void onTimeSet(TimePicker view, int h, int m) {
                                                            String selectTime = h + ":" + m;
                                                            dialog_time.setText(selectTime);
                                                            select_hour = h;
                                                        }
                                                    }, hour, minute, false);
                                            time_picker_dialog.show();
                                        }
                                    });

                                    builder.setView(dialog_view)
                                            .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //System.out.println("Dialog SUBMIT!");
                                                    //https://www.baeldung.com/java-email-validation-regex
                                                    String regexPattern = "^(.+)@(\\S+)$";
                                                    if (Pattern.compile(regexPattern).matcher(dialog_email.getText().toString()).matches()){
                                                        if((select_hour<10)||(select_hour>17)){
                                                            Toast.makeText(getContext(), "Time should be between 10AM AND 5PM", Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            booked_name = Name;
                                                            booked_email = dialog_email.getText().toString();
                                                            booked_date = dialog_date.getText().toString();
                                                            booked_time = dialog_time.getText().toString();
                                                            int number_result;
                                                            SharedPreferences.Editor editor = sharedpreferences.edit();

                                                            if (sharedpreferences.contains("number_result")) {
                                                                // not first record then get the value
                                                                number_result = sharedpreferences.getInt("number_result", -1);
                                                                String[] booked_names = new String[number_result+1];
                                                                String[] booked_emails = new String[number_result+1];
                                                                String[] booked_dates = new String[number_result+1];
                                                                String[] booked_times = new String[number_result+1];
                                                                for (int n=0;n<number_result;n++){
                                                                    booked_names[n] = sharedpreferences.getString("booked_name_"+n, null);
                                                                    booked_emails[n] = sharedpreferences.getString("booked_email_"+n, null);
                                                                    booked_dates[n] = sharedpreferences.getString("booked_date_"+n, null);
                                                                    booked_times[n] = sharedpreferences.getString("booked_time_"+n, null);
                                                                }
                                                                booked_names[number_result] = booked_name;
                                                                booked_emails[number_result] = booked_email;
                                                                booked_dates[number_result] = booked_date;
                                                                booked_times[number_result] = booked_time;
                                                                editor.putInt("number_result", number_result+1);
                                                                for (int n=0;n<number_result+1;n++){
                                                                    editor.putString("booked_name_"+n, booked_names[n]);
                                                                    editor.putString("booked_email_"+n, booked_emails[n]);
                                                                    editor.putString("booked_date_"+n, booked_dates[n]);
                                                                    editor.putString("booked_time_"+n, booked_times[n]);
                                                                }
                                                                editor.commit();
                                                            }else{
                                                                // first record then save data directively
                                                                editor.putInt("number_result", 1);
                                                                editor.putString("booked_name_0", booked_name);
                                                                editor.putString("booked_email_0", booked_email);
                                                                editor.putString("booked_date_0", booked_date);
                                                                editor.putString("booked_time_0", booked_time);
                                                                editor.commit();
                                                            }
                                                            Toast.makeText(getContext(), "Reservation Booked", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else{
                                                        Toast.makeText(getContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            })
                                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //System.out.println("Dialog CANCEL!");
                                                }
                                            })
                                            .show();
                                }
                            });

                            image1 = root.findViewById(R.id.carousel_image1);
                            Picasso.get().load(Photos.getString(0)).into(image1);
                            image2 = root.findViewById(R.id.carousel_image2);
                            Picasso.get().load(Photos.getString(1)).into(image2);
                            if (Photos.length() > 2){
                                image3 = root.findViewById(R.id.carousel_image3);
                                Picasso.get().load(Photos.getString(2)).into(image3);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("That didn't work!");
                    }
                });
        requestQueue.add(jsonObjectRequest);

        return root;
    }
}