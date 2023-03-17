package com.example.yelp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

// https://developers.google.com/maps/documentation/android-sdk/start#getting_the_google_maps_android_api_v2
public class Fragment2 extends Fragment {

    private static final String ARG_PARAM1 = "paramId";

    private String myId;
    private String latitude;
    private String longitude;
    RequestQueue requestQueue;


    public static Fragment2 newInstance(String myId) {
        Fragment2 fragment = new Fragment2(myId);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, myId);
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment2(String inputId) {
        // Required empty public constructor
        myId = inputId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment2_layout, container, false);

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
                            JSONObject Coord = response.getJSONObject("Coord");
                            latitude = Coord.getString("latitude");
                            longitude = Coord.getString("longitude");

                            //https://www.geeksforgeeks.org/how-to-implement-google-map-inside-fragment-in-android/
                            // Initialize map fragment
                            SupportMapFragment supportMapFragment=(SupportMapFragment)
                                    getChildFragmentManager().findFragmentById(R.id.google_map);

                            // Async map
                            supportMapFragment.getMapAsync(new OnMapReadyCallback() {

                                @Override
                                public void onMapReady(@NonNull GoogleMap googleMap) {

                                    double lat = Double.parseDouble(latitude);
                                    double lng = Double.parseDouble(longitude);
                                    LatLng latLng = new LatLng(lat, lng);
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(latLng);
                                    //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                                    googleMap.addMarker(markerOptions);

                                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                        @Override
                                        public void onMapClick(@NonNull LatLng latLng) {
                                            MarkerOptions markerOptions=new MarkerOptions();
                                            markerOptions.position(latLng);
                                            markerOptions.title(latLng.latitude+" : "+latLng.longitude);
                                            googleMap.clear();
                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));
                                            googleMap.addMarker(markerOptions);
                                        }
                                    });
                                }
                            });


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