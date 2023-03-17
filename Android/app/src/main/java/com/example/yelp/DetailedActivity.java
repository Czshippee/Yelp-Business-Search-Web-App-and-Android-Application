package com.example.yelp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.yelp.ui.main.SectionsPagerAdapter;
import com.example.yelp.databinding.ActivityDetailedBinding;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class DetailedActivity extends AppCompatActivity {

    private ActivityDetailedBinding binding;
    RequestQueue requestQueue;
    String response_id;
    //tab3
    String c11,c12,c13,c14,c21,c22,c23,c24,c31,c32,c33,c34;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        response_id = i.getStringExtra("id");

        binding = ActivityDetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), response_id);
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        Toolbar toolbar = findViewById(R.id.tab_tool_bar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //Toast.makeText(this, "You clicked " + myAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        String detail_info_url = "https://businessyelpsearch.wl.r.appspot.com/searchdetail"+"?&id="+response_id;
        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                detail_info_url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //System.out.println("Receive data from backend!");
                        try {
                            Toolbar DetailCardTitle = findViewById(R.id.tab_tool_bar);
                            String Name = response.getString("Name");
                            DetailCardTitle.setTitle(Name);
                            String More_info = response.getString("More_info");

                            //tab3
                            JSONArray Reviews = response.getJSONArray("Reviews");
                            c11 = Reviews.getJSONObject(0).getJSONObject("user").getString("name");
                            c12 = Reviews.getJSONObject(0).getString("rating");
                            c13 = Reviews.getJSONObject(0).getString("text");
                            c14 = Reviews.getJSONObject(0).getString("time_created");

                            c21 = Reviews.getJSONObject(1).getJSONObject("user").getString("name");
                            c22 = Reviews.getJSONObject(1).getString("rating");
                            c23 = Reviews.getJSONObject(0).getString("text");
                            c24 = Reviews.getJSONObject(1).getString("time_created");

                            c31 = Reviews.getJSONObject(2).getJSONObject("user").getString("name");
                            c32 = Reviews.getJSONObject(2).getString("rating");
                            c33 = Reviews.getJSONObject(2).getString("text");
                            c34 = Reviews.getJSONObject(2).getString("time_created");

                            System.out.println(More_info);
                            System.out.println("https://twitter.com/intent/tweet?text=Check%20"+Name.replaceAll(" ", "%20")+"%20on%20Yelp.&url="+More_info);
                            System.out.println("https://www.facebook.com/sharer/sharer.php?u="+More_info+"&quote=Awesome%20Blog!");
                            //toolbar
                            TextView twitter_link = findViewById(R.id.twitter_link);
                            TextView facebook_link = findViewById(R.id.facebook_link);
                            twitter_link.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String url = "https://twitter.com/intent/tweet?text=Check%20"+Name.replaceAll(" ", "%20")+"%20on%20Yelp.&url="+More_info;
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                }
                            });
                            facebook_link.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String url = "https://www.facebook.com/sharer/sharer.php?u="+More_info+"&quote=Awesome%20Blog!";
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
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

//        Bundle bundle1 = new Bundle();
//        bundle1.putString("id", response_id);
//        Fragment1 fragment1 = new Fragment1(response_id);
//        fragment1.setArguments(bundle1);
//        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout1,fragment1).commit();
    }

    public String[] F3getMyData() {
        return new String[]{c11,c12,c13,c14,c21,c22,c23,c24,c31,c32,c33,c34};
    }

}