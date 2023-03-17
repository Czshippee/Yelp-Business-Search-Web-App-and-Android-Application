package com.example.yelp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuWrapperICS;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity implements MyAdapter.ItemClickListener {

    AutoCompleteTextView keyword_val;
    TextView calendar, noResFou;
    EditText distance_val, location_val;
    Spinner category_val;
    CheckBox autoloc;
    Button submit, reset;
    double latitude, longitude;
    boolean isAllFieldsChecked = false;
    RequestQueue requestQueue;
    // boolean gps_enabled = false;
    // boolean network_enabled = false;
    public static final int LOCATION_CODE = 301;
    RecyclerView recyclerView;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        keyword_val = findViewById(R.id.autoCompleteTextView);
        keyword_val.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String typedKeyword = keyword_val.getText().toString();
                String url = "https://businessyelpsearch.wl.r.appspot.com/atcpt?&" + "typedWord" + '=' + typedKeyword;
                requestQueue = Volley.newRequestQueue(MainActivity.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //System.out.println("Receive data from backend!");
                                try {
                                    JSONArray rspSuggestion = response.getJSONArray("suggestions");
                                    int rspLength = rspSuggestion.length();
                                    String[] autocompleteSug = new String[rspLength];
                                    for (int i = 0; i < rspLength; i++) {
                                        autocompleteSug[i] = rspSuggestion.getString(i);
                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                                            android.R.layout.simple_list_item_1, autocompleteSug);
                                    keyword_val.setAdapter(adapter);
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
            }
        });
        distance_val = findViewById(R.id.form_distance);
        category_val = findViewById(R.id.form_category_dropdown);
        location_val = findViewById(R.id.form_location);
        autoloc = findViewById(R.id.form_autodetect);
        autoloc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton autoCheckBox, boolean isChecked) {
                if (autoCheckBox.isChecked()) {
                    location_val.setVisibility(View.INVISIBLE);
                    if (location_val.length() == 0) location_val.setText("false");
                    getLocation();
                } else {
                    if (location_val.getText().toString().equals("false")) location_val.setText("");
                    location_val.setVisibility(View.VISIBLE);
                }
            }
        });
        submit = findViewById(R.id.submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAllFieldsChecked = CheckAllFields();
                if (isAllFieldsChecked) {
                    String url = "https://businessyelpsearch.wl.r.appspot.com/search" + stringifyParams();
                    requestQueue = Volley.newRequestQueue(MainActivity.this);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.GET,
                            url,
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    //System.out.println("Receive data from backend!");
                                    try {
                                        createTable(response);
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
                }
            }
        });
        reset = findViewById(R.id.reset_button);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyword_val.setText("");
                distance_val.setText("");
                category_val.setSelection(0);
                location_val.setText("");
                location_val.setVisibility(View.VISIBLE);
                autoloc.setChecked(false);
                recyclerView = findViewById(R.id.result_table);
                noResFou = findViewById(R.id.searchNoResult);
                recyclerView.setVisibility(View.INVISIBLE);
                noResFou.setVisibility(View.INVISIBLE);
            }
        });
        calendar = findViewById(R.id.toolbar_calendar);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(i);
            }
        });

//        debug
//        keyword_val.setText("sushi");
//        distance_val.setText("5");
//        location_val.setText("usc");
    }

    public void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(MainActivity.this.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        // getting GPS status
        //gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // getting network status
        //network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Obtain permission (if the permission is not enabled, a dialog box will pop up asking whether to enable the permission)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
            } else {
                for (String provider : providers) {
                    Location l = locationManager.getLastKnownLocation(provider);
                    if (l == null) {
                        continue;
                    }
                    if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                        // Found best last known location: %s", l);
                        bestLocation = l;
                    }
                }
            }
        }
        if (bestLocation != null){
            latitude = bestLocation.getLatitude();
            longitude = bestLocation.getLongitude();
        }
    }

    private boolean CheckAllFields() {
        if (keyword_val.length() == 0) {
            keyword_val.setError("This field is required");
            return false;
        }
        if (distance_val.length() == 0) {
            distance_val.setError("This field is required");
            return false;
        }
        if (location_val.length() == 0) {
            location_val.setError("This field is required");
            return false;
        }
        // after all validation return true.
        return true;
    }

    public String stringifyParams(){
        String urlParams = "?";
        urlParams += '&' + "keyword_val" + '=' + keyword_val.getText().toString().replaceAll(" ", "%20");
        urlParams += '&' + "distance_val" + '=' + distance_val.getText().toString();
        urlParams += '&' + "categoary_val" + '=' + category_val.getSelectedItem().toString().replaceAll(" ", "%20");
        urlParams += '&' + "location_val" + '=' + location_val.getText().toString();
        urlParams += '&' + "autoloc" + '=' + autoloc.isChecked();
        urlParams += '&' + "latitude" + '=' + latitude;
        urlParams += '&' + "longitude" + '=' + longitude;
        return urlParams;
    }

    public void createTable(JSONObject response) throws JSONException {
        List<BusinessInfo> resultData = new ArrayList<>();
        int business_to_load = response.length();
        for (int i = 0; i < business_to_load; i++) {
            JSONObject result = response.getJSONObject(String.valueOf(i));
            String resultImageUrl = result.getString("image_url");
            String resultId = result.getString("id");
            String resultName = result.getString("name");
            String resultRating = result.getString("rating");
            String resultDistance = result.getString("distance");

            BusinessInfo bi = new BusinessInfo();
            bi.setSerialNumber(""+(i+1));
            bi.setId(resultId);
            bi.setImageUrl(resultImageUrl);
            bi.setName(resultName);
            bi.setRating(resultRating);
            bi.setDistance(resultDistance);
            resultData.add(bi);
        }
        // test if save successful
        /*for (int i = 0; i < business_to_load; i++){
            System.out.println("Rating:"+resultData.get(i).getRating()+", Name:"+resultData.get(i).getName()
            +", Distance:"+resultData.get(i).getDistance()+", Id:"+resultData.get(i).getId()+", ImageUrl:"+resultData.get(i).getImageUrl());
        }*/

        // set up the RecyclerView
        recyclerView = findViewById(R.id.result_table);
        noResFou = findViewById(R.id.searchNoResult);

        if (resultData.size()==0){
            recyclerView.setVisibility(View.INVISIBLE);
            noResFou.setVisibility(View.VISIBLE);
        }else{
            noResFou.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            // set a GridLayoutManager with 3 number of columns
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
            recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
            recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
            //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            //recyclerview.setLayoutManager(linearLayoutManager);

            myAdapter = new MyAdapter(resultData, this);
            myAdapter.setClickListener((MyAdapter.ItemClickListener) this);
            recyclerView.setAdapter(myAdapter);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        String resp_id = myAdapter.getItem(position).getId();

        Intent i = new Intent(MainActivity.this, DetailedActivity.class);
        Bundle b = new Bundle();
        b.putString("id", resp_id);
        i.putExtras(b);

        startActivity(i);
    }
}


